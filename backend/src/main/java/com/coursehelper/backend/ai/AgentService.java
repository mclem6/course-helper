package com.coursehelper.backend.ai;

import com.coursehelper.backend.ai.assignment.AssignmentTool;
import com.coursehelper.backend.ai.retrieval.ResourceRetrievalTool;
import com.coursehelper.backend.ai.schedule.ScheduleTool;
import com.coursehelper.backend.ai.task.TaskTool;
import com.coursehelper.backend.exceptions.AIServiceException;
import com.coursehelper.backend.userSettings.UserSettings;
import com.coursehelper.backend.userSettings.SettingsRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.type.TypeReference;

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
    private final SettingsRepository settingsRepository;
    private final ObjectMapper mapper;

    public AgentService(LLMClient llmClient,
                        ResourceRetrievalTool resourceRetrievalTool,
                        ScheduleTool scheduleTool,
                        AssignmentTool assignmentTool,
                        TaskTool taskTool,
                        SettingsRepository settingsRepository) {
        this.llmClient = llmClient;
        this.resourceRetrievalTool = resourceRetrievalTool;
        this.scheduleTool = scheduleTool;
        this.assignmentTool = assignmentTool;
        this.taskTool = taskTool;
        this.settingsRepository = settingsRepository;
        this.mapper = new ObjectMapper();
    }

    public String answerQuery(String userQuestion, Long userId, String username) {

        // get current date and time
        String today = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        String time = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("HH:mm"));

        // get user semester settings
        UserSettings settings = settingsRepository.findByUserId(userId).orElse(null);

        String semesterContext = settings != null
            ? "Current semester: " + settings.getCurrentSemester() + " " + settings.getCurrentYear() + ". " +
              "Semester runs from " + settings.getSemesterStart() + " to " + settings.getSemesterEnd() + "."
            : "";

        // build conversation history
        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(Map.of(
            "role", "system",
            "content", "You are a friendly and helpful course assistant for students. " +
                       "The student's name is " + username + ". " +
                       "Today is " + today + ". Current time is " + time + ". " +
                       semesterContext + " " +
                       "Use the available tools to answer questions about their study materials, class schedule, assignments, and tasks. " +
                       "When asked about schedule, use get_schedule to get all courses then filter by the day the student is asking about. " +
                       "When listing schedule always list in time order. " +
                       "Avoid special formatting like bold and italics. " +
                       "When a student asks about any course document or resource — such as a syllabus, lecture notes, readings, course outline, or any uploaded material — always call search_resources first. " +
                       "If search_resources returns no results, tell the student that no matching documents were found and suggest they upload the relevant file. " +
                       "You may ONLY answer questions related to the student's schedule, assignments, tasks, and uploaded course materials. " +
                       "If asked about anything outside these topics, politely decline and explain you can only help with their coursework and schedule."
        ));

        messages.add(Map.of(
            "role", "user",
            "content", userQuestion
        ));

        List<Map<String, Object>> tools = buildToolDefinitions();

        int maxIterations = 10;
        int iterations = 0;

        // loop until GPT-4o gives a final answer
        while (iterations++ < maxIterations) {

            // call GPT-4o
            Map<String, Object> response = llmClient.callWithTools(messages, tools);

            // extract the first choice
            List<Map<String, Object>> choices = mapper.convertValue(
                response.get("choices"), new TypeReference<List<Map<String, Object>>>(){});
         
            Map<String, Object> choice = choices.get(0);
            String finishReason = (String) choice.get("finish_reason");
            Map<String, Object> assistantMessage = mapper.convertValue(
                 choice.get("message"), new TypeReference<Map<String, Object>>(){});    

            // add assistant response to conversation history
            messages.add(assistantMessage);

            // done, return the answer
            if ("stop".equals(finishReason)) {
                return (String) assistantMessage.get("content");
            }

            // GPT-4o tool call executions
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
                               "Use when student asks about assignments, homework, tests, projects, or deadlines. " +
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
                               "Use when student asks about tasks or to-do items. " +
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

        return List.of(searchResourcesTool, getScheduleTool, getAssignmentsTool, getTasksTool);
    }
}