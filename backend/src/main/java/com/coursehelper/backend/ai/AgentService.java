package com.coursehelper.backend.ai;

import com.coursehelper.backend.ai.assignment.AssignmentTool;
import com.coursehelper.backend.ai.memory.ChatTurn;              // MEMORY
import com.coursehelper.backend.ai.memory.ConversationMemory;    // MEMORY
import com.coursehelper.backend.ai.retrieval.ResourceRetrievalTool;
import com.coursehelper.backend.ai.schedule.ScheduleTool;
import com.coursehelper.backend.ai.summary.SummaryTool;
import com.coursehelper.backend.ai.task.TaskTool;
import com.coursehelper.backend.exceptions.AIServiceException;
import com.coursehelper.backend.userSettings.SettingsRepository;
import com.coursehelper.backend.userSettings.UserSettings;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

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
    private final ConversationMemory memory;                     // MEMORY
    private final ObjectMapper mapper;

    public AgentService(LLMClient llmClient,
                        ResourceRetrievalTool resourceRetrievalTool,
                        ScheduleTool scheduleTool,
                        AssignmentTool assignmentTool,
                        TaskTool taskTool,
                        SummaryTool summaryTool,
                        SettingsRepository settingsRepository,
                        ConversationMemory memory) {             // MEMORY
        this.llmClient = llmClient;
        this.resourceRetrievalTool = resourceRetrievalTool;
        this.scheduleTool = scheduleTool;
        this.assignmentTool = assignmentTool;
        this.taskTool = taskTool;
        this.summaryTool = summaryTool;
        this.settingsRepository = settingsRepository;
        this.memory = memory;                                    // MEMORY
        this.mapper = new ObjectMapper();
    }

    private static final String GREETING_PROMPT =
        "You are a friendly course assistant for students. Greet the student by name.\n" +
        "Call get_summary to fetch the student's incomplete assignments and tasks, already grouped by due date.\n" +
        "Present the three sections exactly as returned — Overdue, Due Today, Upcoming.\n" +
        "End with one short encouraging sentence.";

    /** Back-compat overload: stateless single-shot, no memory. */
    public String answerQuery(String userQuestion, Long userId, String username) {
        return answerQuery(userQuestion, userId, username, null);
    }

    public String answerQuery(String userQuestion, Long userId, String username,
                             String conversationId) {            // MEMORY

        boolean isGreeting = "greeting".equals(userQuestion);
        String resolvedQuestion = isGreeting ? GREETING_PROMPT : userQuestion;

        // A greeting is a fresh daily summary — don't carry stale context into it.
        boolean useMemory = conversationId != null && !isGreeting;   // MEMORY

        String today = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        String time = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("HH:mm"));

        UserSettings settings = settingsRepository.findByUserId(userId).orElse(null);

        String semesterContext = settings != null
            ? "Current semester: " + settings.getCurrentSemester() + " " + settings.getCurrentYear() + ". " +
              "Semester runs from " + settings.getSemesterStart() + " to " + settings.getSemesterEnd() + "."
            : "";

        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(Map.of(
            "role", "system",
            "content", buildSystemPrompt(username, today, time, semesterContext)
        ));

        // MEMORY: replay prior turns between the (freshly rebuilt) system prompt
        // and the new user message.
        if (useMemory) {
            for (ChatTurn turn : memory.load(userId, conversationId)) {
                messages.add(Map.of("role", turn.role(), "content", turn.content()));
            }
        }

        messages.add(Map.of("role", "user", "content", resolvedQuestion));

        List<Map<String, Object>> tools = buildToolDefinitions();

        int maxIterations = 10;
        int iterations = 0;

        while (iterations++ < maxIterations) {

            Map<String, Object> response = llmClient.callWithTools(messages, tools);

            List<Map<String, Object>> choices = mapper.convertValue(
                response.get("choices"), new TypeReference<List<Map<String, Object>>>(){});

            Map<String, Object> choice = choices.get(0);
            String finishReason = (String) choice.get("finish_reason");
            Map<String, Object> assistantMessage = mapper.convertValue(
                choice.get("message"), new TypeReference<Map<String, Object>>(){});

            messages.add(assistantMessage);

            if ("stop".equals(finishReason)) {
                String content = (String) assistantMessage.get("content");

                // MEMORY: persist only the clean exchange, not the ReAct scratchpad.
                if (useMemory && content != null) {
                    memory.append(userId, conversationId, new ChatTurn("user", resolvedQuestion));
                    memory.append(userId, conversationId, new ChatTurn("assistant", content));
                }
                return content;
            }

            if ("tool_calls".equals(finishReason)) {
                List<Map<String, Object>> toolCalls = mapper.convertValue(
                    assistantMessage.get("tool_calls"), new TypeReference<List<Map<String, Object>>>(){});

                for (Map<String, Object> toolCall : toolCalls) {
                    String toolCallId = (String) toolCall.get("id");
                    Map<String, Object> function = mapper.convertValue(
                        toolCall.get("function"), new TypeReference<Map<String, Object>>(){});

                    String toolName = (String) function.get("name");
                    String argsJson = (String) function.get("arguments");

                    String result = executeTool(toolName, argsJson, userId);

                    messages.add(Map.of(
                        "role", "tool",
                        "tool_call_id", toolCallId,
                        "content", result
                    ));
                }
            }
        }

        throw new AIServiceException("Agent exceeded maximum iterations", null);
    }

    private String buildSystemPrompt(String username, String today, String time, String semesterContext) {
        return "You are a friendly and helpful course assistant for students. " +
               "The student's name is " + username + ". " +
               "Today is " + today + ". Current time is " + time + ". " +
               semesterContext + " " +
               "Use the available tools to answer questions about their study materials, class schedule, assignments, and tasks. " +
               "When asked about schedule, use get_schedule to get all courses then filter by the day the student is asking about. " +
               "When listing schedule always list in time order. " +
               "When summarizing assignments or tasks, the tools return items pre-grouped under === OVERDUE ===, === DUE TODAY ===, and === UPCOMING === headers. Present these groups as-is under the same section names. Only include a section if the tool returned items in it. " +
               "Avoid special formatting like bold and italics. " +
               "When a student asks about any course document or resource — such as a syllabus, lecture notes, readings, course outline, or any uploaded material — always call search_resources first. " +
               "If search_resources returns no results, tell the student that no matching documents were found and suggest they upload the relevant file. " +
               "When answering from search_resources results, always state which document the information came from (e.g. 'According to your syllabus.pdf, ...'). " +
               "You may ONLY answer questions related to the student's schedule, assignments, tasks, and uploaded course materials. " +
               "If asked about anything outside these topics, politely decline and explain you can only help with their coursework and schedule. " +
               // MEMORY: teach the model that earlier turns are context, not fresh truth.
               "Earlier messages in this conversation are prior context. Resolve follow-up references " +
               "like 'that one', 'it', or 'what about tomorrow?' against them. " +
               "Never reuse tool results from earlier turns — always re-call the relevant tool, " +
               "since assignments, tasks and the current time may have changed.";
    }

    private String executeTool(String toolName, String argsJson, Long userId) {
        try {
            Map<String, Object> args = mapper.readValue(argsJson,
                new tools.jackson.core.type.TypeReference<Map<String, Object>>() {});
            String query = (String) args.get("query");

            String status    = (String) args.getOrDefault("status", "INCOMPLETE");
            String completed = (String) args.getOrDefault("completed", "incomplete");

            return switch (toolName) {
                case "search_resources" -> resourceRetrievalTool.search(query, userId);
                case "get_schedule"     -> scheduleTool.search(query, userId);
                case "get_assignments"  -> assignmentTool.search(query, userId, status);
                case "get_tasks"        -> taskTool.search(query, userId, completed);
                case "get_summary"      -> summaryTool.getSummary(userId);
                default -> "Unknown tool: " + toolName;
            };

        } catch (Exception e) {
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
                "description", "Get the student's assignments for the current semester. " +
                               "Returns title, course name, type (HOMEWORK/TEST/PROJECT/LAB), due date, and status. " +
                               "Use when generating a student summary or greeting, or when the student asks about assignments, homework, tests, projects, or deadlines. " +
                               "Default status is INCOMPLETE — only pass COMPLETED or ALL when explicitly asked.",
                "parameters", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "query", Map.of(
                            "type", "string",
                            "description", "Filter by course name, assignment type, or title. Pass empty string for all."
                        ),
                        "status", Map.of(
                            "type", "string",
                            "enum", List.of("INCOMPLETE", "COMPLETED", "ALL"),
                            "description", "Filter by completion status. Defaults to INCOMPLETE."
                        )
                    ),
                    "required", List.of("query")
                )
            )
        );

        Map<String, Object> getTasksTool = Map.of(
            "type", "function",
            "function", Map.of(
                "name", "get_tasks",
                "description", "Get the student's tasks for the current semester. " +
                               "Returns title, course name, due date, and completion status. " +
                               "Use when generating a student summary or greeting, or when the student asks about tasks or to-do items. " +
                               "Default is incomplete tasks only — only pass 'completed' or 'all' when explicitly asked.",
                "parameters", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "query", Map.of(
                            "type", "string",
                            "description", "Filter by course name or title. Pass empty string for all."
                        ),
                        "completed", Map.of(
                            "type", "string",
                            "enum", List.of("incomplete", "completed", "all"),
                            "description", "Filter by completion. Defaults to incomplete."
                        )
                    ),
                    "required", List.of("query")
                )
            )
        );

        Map<String, Object> getSummaryTool = Map.of(
            "type", "function",
            "function", Map.of(
                "name", "get_summary",
                "description", "Get a combined summary of the student's incomplete assignments and tasks, pre-grouped into Overdue, Due Today, and Upcoming sections. Use this for greetings and daily summaries.",
                "parameters", Map.of(
                    "type", "object",
                    "properties", Map.of(),
                    "required", List.of()
                )
            )
        );

        return List.of(searchResourcesTool, getScheduleTool, getAssignmentsTool, getTasksTool, getSummaryTool);
    }


}