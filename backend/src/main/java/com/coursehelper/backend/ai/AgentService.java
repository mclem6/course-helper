package com.coursehelper.backend.ai;

import com.coursehelper.backend.ai.assignment.AssignmentTool;
import com.coursehelper.backend.ai.memory.ConversationMemoryService;
import com.coursehelper.backend.ai.retrieval.ResourceRetrievalTool;
import com.coursehelper.backend.ai.schedule.ScheduleTool;
import com.coursehelper.backend.ai.summary.SummaryTool;
import com.coursehelper.backend.ai.task.TaskTool;
import com.coursehelper.backend.exceptions.AIServiceException;
import com.coursehelper.backend.userSettings.UserSettings;
import com.coursehelper.backend.userSettings.SettingsRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AgentService {

    private final LLMClient llmClient;
    private final ResourceRetrievalTool resourceRetrievalTool;
    private final ScheduleTool scheduleTool;
    private final AssignmentTool assignmentTool;
    private final TaskTool taskTool;
    private final SummaryTool summaryTool;
    private final SettingsRepository settingsRepository;
    private final ConversationMemoryService memory;  
    private final ObjectMapper mapper;
    private static final Logger log = LoggerFactory.getLogger(AgentService.class);

    public AgentService(LLMClient llmClient,
                        ResourceRetrievalTool resourceRetrievalTool,
                        ScheduleTool scheduleTool,
                        AssignmentTool assignmentTool,
                        TaskTool taskTool,
                        SummaryTool summaryTool,
                        SettingsRepository settingsRepository,
                        ConversationMemoryService memory) {   
        this.llmClient = llmClient;
        this.resourceRetrievalTool = resourceRetrievalTool;
        this.scheduleTool = scheduleTool;
        this.assignmentTool = assignmentTool;
        this.taskTool = taskTool;
        this.summaryTool = summaryTool;
        this.settingsRepository = settingsRepository;
        this.memory = memory;                                  
        this.mapper = new ObjectMapper();
    }

    private static final String GREETING_PROMPT =
        "Greet the student by name.\n" +
        "Call get_summary. \n" +
        "End with one short encouraging sentence.";


    public String answerQuery(String userQuestion,
                              Long userId,
                              String username,
                              String conversationId) {

        boolean isGreeting = "greeting".equals(userQuestion);
        String resolvedQuestion = isGreeting ? GREETING_PROMPT : userQuestion;

        // current date/time
        String today = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        String time  = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("HH:mm"));

        // semester context
        UserSettings settings = settingsRepository.findByUserId(userId).orElse(null);
        String semesterContext = settings != null
            ? "Current semester: " + settings.getCurrentSemester() + " "
              + settings.getCurrentYear() + ". "
              + "Semester runs from " + settings.getSemesterStart()
              + " to " + settings.getSemesterEnd() + "."
            : "";

        // ── build message list ──────────────────────────────────
        List<Map<String, Object>> messages = new ArrayList<>();

        // don't store in memory 
        messages.add(Map.of(
            "role", "system",
            "content", buildSystemPrompt(username, today, time, semesterContext)
        ));

        // inject prior conversation turns
        messages.addAll(memory.loadHistory(conversationId));

        // 3) current user message
        messages.add(Map.of("role", "user", "content", resolvedQuestion));

        // ReAct loop
        List<Map<String, Object>> tools = buildToolDefinitions();
        int maxIterations = 10;
        int iterations    = 0;

        while (iterations++ < maxIterations) {

            Map<String, Object> response = llmClient.callWithTools(messages, tools);

            List<Map<String, Object>> choices = mapper.convertValue(
                response.get("choices"),
                new TypeReference<List<Map<String, Object>>>(){});

            Map<String, Object> choice = choices.get(0);
            String finishReason = (String) choice.get("finish_reason");
            Map<String, Object> assistantMessage = mapper.convertValue(
                choice.get("message"),
                new TypeReference<Map<String, Object>>(){});

            messages.add(assistantMessage);

            

            log.info("finishReason={} content={} tool_calls={}",
            finishReason,
            assistantMessage.get("content"),
            assistantMessage.get("tool_calls"));

            if ("stop".equals(finishReason)) {
                String content = (String) assistantMessage.get("content");

                // Store a clean user string (not the verbose greeting prompt)
                String storedUserText = isGreeting ? "Hello" : userQuestion;
                memory.saveTurn(conversationId, userId, storedUserText, content);

                return content;
            }

            if ("tool_calls".equals(finishReason)) {
                List<Map<String, Object>> toolCalls = mapper.convertValue(
                    assistantMessage.get("tool_calls"),
                    new TypeReference<List<Map<String, Object>>>(){});

                for (Map<String, Object> toolCall : toolCalls) {
                    String toolCallId = (String) toolCall.get("id");
                    Map<String, Object> function = mapper.convertValue(
                        toolCall.get("function"),
                        new TypeReference<Map<String, Object>>(){});

                    String toolName = (String) function.get("name");
                    String argsJson = (String) function.get("arguments");
                    String result   = executeTool(toolName, argsJson, userId);

                    messages.add(Map.of(
                        "role",         "tool",
                        "tool_call_id", toolCallId,
                        "content",      result
                    ));
                }
            }
        }

        throw new AIServiceException("Agent exceeded maximum iterations", null);
    }

    
    private String buildSystemPrompt(String username, String today,
                                     String time, String semesterContext) {
        return "You are a friendly and helpful course assistant for students. " +
               "The student's name is " + username + ". " +
               "Today is " + today + ". Current time is " + time + ". " +
               semesterContext + " " +
               "Use the available tools to answer questions about their study materials, " +
               "class schedule, assignments, and tasks. " +
               "When listing schedule always list in chronological order. " +
               "When summarizing assignments and/or tasks, the tools return items pre-grouped " +
               "under === OVERDUE ===, === DUE TODAY ===, and === UPCOMING === headers. " +
               "Present these groups as-is under the same section names. " +
               "Only include a section if it's list is not null or empty. " +
               "When presenting an assignment or task, always include its course, due date, and status. " +
               "For assignments, also include the type (homework, test, project, or lab). " +
               "Do not use or include any formatting markdown. " +
               "If search_resources returns no results, tell the student that no matching " +
               "documents were found and suggest they upload the relevant file. " +
               "When answering from search_resources results, always state which document " +
               "the information came from. " +
               "You may ONLY answer questions related to the student's schedule, assignments, " +
               "tasks, and uploaded course materials. " +
               "If asked about anything outside these topics, politely decline and explain " +
               "you can only help with their coursework and schedule.";
    }


    private String executeTool(String toolName, String argsJson, Long userId) {
        try {
            Map<String, Object> args = mapper.readValue(argsJson,
                new tools.jackson.core.type.TypeReference<Map<String, Object>>() {});
            String query = (String) args.get("query");

            String status    = (String) args.getOrDefault("status", "INCOMPLETE");
            String completed = (String) args.getOrDefault("completed", "incomplete");

            String result = switch (toolName) {
                case "search_resources" -> resourceRetrievalTool.search(query, userId);
                case "get_schedule"     -> scheduleTool.search(query, userId);
                case "get_assignments"  -> assignmentTool.search(query, userId, status);
                case "get_tasks"        -> taskTool.search(query, userId, completed);
                case "get_summary"      -> summaryTool.getSummary(userId);
                default -> "Unknown tool: " + toolName;
            };

            String reasoning = (String) args.get("reasoning");
            log.info("Tool call: {} reasoning=\"{}\" args={} result={}", toolName, reasoning, argsJson, result);
            return result;

        } catch (Exception e) {
            log.error("Tool execution failed: {} args={}", toolName, argsJson, e);
            throw new AIServiceException("Failed to execute tool: " + toolName, e);
        }
    }

    private List<Map<String, Object>> buildToolDefinitions() {

        Map<String, Object> searchResourcesTool = Map.of(
            "type", "function",
            "function", Map.of(
                "name", "search_resources",
                "description", "Search the student's uploaded course documents including syllabi, lecture notes, readings, course outlines, and any other uploaded files. " +
                               "Use whenever the student asks about any course document, resource, or material — including syllabi, grading policies, deadlines in documents, or course content. " +
                               "Always call this before concluding that information is unavailable.",
                "parameters", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "query", Map.of(
                            "type", "string",
                            "description", "The search query to find relevant study material"
                        )
                    ),
                    "required", List.of("query")
                )
            )
        );

        Map<String, Object> getScheduleTool = Map.of(
            "type", "function",
            "function", Map.of(
                "name", "get_schedule",
                "description", "Get the student's full class schedule for the current semester. " +
                               "Returns all courses with their days, times and details. " +
                               "Use this when student asks about their schedule, what classes they have today, " +
                               "tomorrow, or any specific day. You already know today's date so filter the " +
                               "returned courses by the relevant day yourself.",
                "parameters", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "query", Map.of(
                            "type", "string",
                            "description", "The schedule query"
                        )
                    ),
                    "required", List.of("query")
                )
            )
        );

        Map<String, Object> getAssignmentsTool = Map.of(
            "type", "function",
            "function", Map.of(
                "name", "get_assignments",
                "description", "Get the student's assignments for the current semester, filtered by course, type, or title. " +
               "Returns title, course name, type (HOMEWORK/TEST/PROJECT/LAB), due date, and status, as a flat list when filtering by COMPLETED, grouped by due dates otherwise. " +
               "Use when the student filters by a specific course, assignment type, or title, or wants completed/all assignments rather than just what's currently due. " +
               "Default status is INCOMPLETE — only pass COMPLETED or ALL when asked.",
                "parameters", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "reasoning", Map.of(
                            "type", "string",
                            "description", "Briefly explain why this tool is the right choice for the student's question before calling it."
                        ),
                        "query", Map.of(
                            "type", "string",
                            "description", "Filter by course name, assignment type, or title. Pass empty string for all."
                        ),
                        "status", Map.of(
                            "type", "string",
                            "enum", List.of("INCOMPLETE", "COMPLETED", "ALL"),
                            "description", "Filter by completion status. Defaults to INCOMPLETE. Words like 'overdue', 'due today', or 'upcoming' always imply INCOMPLETE regardless of prior conversation context. " +
                            "Use ALL when the student's phrasing includes words like 'all' or 'every'. Use COMPLETED only when they ask specifically for finished/done work."
                        )
                    ),
                    "required", List.of("reasoning", "query")
                )
            )
        );

        Map<String, Object> getTasksTool = Map.of(
            "type", "function",
            "function", Map.of(
                "name", "get_tasks",
                "description","Get the student's tasks for the current semester, filtered by course or title, grouped by due date. " +
               "Returns title, course name, due date, and staus (COMPLETE or INCOMPLETE)" +
               "Use when the student filters by a specific course or title, or wants completed/all tasks rather than just what's currently due. " +
               "Default is incomplete tasks only — only pass 'completed' or 'all' when asked.",
                "parameters", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "reasoning", Map.of(
                            "type", "string",
                            "description", "Briefly explain why this tool is the right choice for the student's question before calling it."
                        ),
                        "query", Map.of(
                            "type", "string",
                            "description", "Filter by course name or title. Pass empty string for all."
                        ),
                        "completed", Map.of(
                            "type", "string",
                            "enum", List.of("INCOMPLETE", "COMPLETED", "ALL"),
                            "description", "Filter by completion status. Defaults to INCOMPLETE. Words like 'overdue', 'due today', or 'upcoming' always imply INCOMPLETE regardless of prior conversation context. " +
                            "Use ALL when the student's phrasing includes words like 'all' or 'every'. Use COMPLETED only when they ask specifically for finished/done work."
                        )
                    ),
                    "required", List.of("reasoning", "query")
                )
            )
        );

        Map<String, Object> getSummaryTool = Map.of(
            "type", "function",
            "function", Map.of(
                "name", "get_summary",
                "description", "Get a combined overview of the student's incomplete assignments and tasks across all courses, with no filtering. Use this for greetings, daily summaries, and general 'what's due' questions with no course, type, or status filter specified.",
                "parameters", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "reasoning", Map.of(
                    "type", "string",
                    "description", "Briefly explain why this tool is the right choice for the student's question before calling it.")
                    ),
                    "required", List.of("reasoning")
                )
            )
        );

        return List.of(searchResourcesTool, getScheduleTool, getAssignmentsTool, getTasksTool, getSummaryTool);
    }
}