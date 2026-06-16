package com.coursehelper.backend.ai.summary;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.coursehelper.backend.assignment.Assignment;
import com.coursehelper.backend.assignment.AssignmentRepository;
import com.coursehelper.backend.course.Course;
import com.coursehelper.backend.course.CourseRepository;
import com.coursehelper.backend.task.Task;
import com.coursehelper.backend.task.TaskRepository;
import com.coursehelper.backend.userSettings.SettingsRepository;
import com.coursehelper.backend.userSettings.UserSettings;

@Component
public class SummaryTool {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yy");

    private final AssignmentRepository assignmentRepository;
    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;
    private final SettingsRepository settingsRepository;

    public SummaryTool(AssignmentRepository assignmentRepository,
                       TaskRepository taskRepository,
                       CourseRepository courseRepository,
                       SettingsRepository settingsRepository) {
        this.assignmentRepository = assignmentRepository;
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
        this.settingsRepository = settingsRepository;
    }

    public String getSummary(Long userId) {
        UserSettings settings = settingsRepository.findByUserId(userId).orElse(null);
        if (settings == null) return "User settings not configured.";

        List<Course> semesterCourses = courseRepository.findByUserIdAndSemesterAndCourseYear(
            userId, settings.getCurrentSemester(), settings.getCurrentYear());
        if (semesterCourses.isEmpty()) return "No courses found for the current semester.";

        Map<Long, String> courseNames = semesterCourses.stream()
            .collect(Collectors.toMap(Course::getId, Course::getName));
        Set<Long> courseIds = courseNames.keySet();

        LocalDate today = LocalDate.now();

        // fetch and classify assignments
        List<Assignment> assignments = assignmentRepository
            .findByUserIdAndStatus(userId, "INCOMPLETE")
            .stream()
            .filter(a -> courseIds.contains(a.getCourseId()) && a.getDueDate() != null)
            .sorted(Comparator.comparing(Assignment::getDueDate))
            .collect(Collectors.toList());

        // fetch and classify tasks
        List<Task> tasks = taskRepository
            .findByUserIdAndCompleted(userId, false)
            .stream()
            .filter(t -> courseIds.contains(t.getCourseId()) && t.getDueDate() != null)
            .sorted(Comparator.comparing(Task::getDueDate))
            .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();

        // OVERDUE
        sb.append("=== OVERDUE ===\n");
        List<Assignment> overdueA = assignments.stream().filter(a -> a.getDueDate().isBefore(today)).toList();
        List<Task> overdueT = tasks.stream().filter(t -> t.getDueDate().isBefore(today)).toList();
        if (overdueA.isEmpty() && overdueT.isEmpty()) {
            sb.append("None\n");
        } else {
            overdueA.forEach(a -> sb.append(formatAssignment(a, courseNames)).append("\n\n"));
            overdueT.forEach(t -> sb.append(formatTask(t, courseNames)).append("\n\n"));
        }

        // DUE TODAY
        sb.append("\n=== DUE TODAY ===\n");
        List<Assignment> todayA = assignments.stream().filter(a -> a.getDueDate().isEqual(today)).toList();
        List<Task> todayT = tasks.stream().filter(t -> t.getDueDate().isEqual(today)).toList();
        if (todayA.isEmpty() && todayT.isEmpty()) {
            sb.append("None\n");
        } else {
            todayA.forEach(a -> sb.append(formatAssignment(a, courseNames)).append("\n\n"));
            todayT.forEach(t -> sb.append(formatTask(t, courseNames)).append("\n\n"));
        }

        // UPCOMING
        sb.append("\n=== UPCOMING ===\n");
        List<Assignment> upcomingA = assignments.stream().filter(a -> a.getDueDate().isAfter(today)).toList();
        List<Task> upcomingT = tasks.stream().filter(t -> t.getDueDate().isAfter(today)).toList();
        if (upcomingA.isEmpty() && upcomingT.isEmpty()) {
            sb.append("None\n");
        } else {
            upcomingA.forEach(a -> sb.append(formatAssignment(a, courseNames)).append("\n\n"));
            upcomingT.forEach(t -> sb.append(formatTask(t, courseNames)).append("\n\n"));
        }

        return sb.toString().trim();
    }

    private String formatAssignment(Assignment a, Map<Long, String> courseNames) {
        String type = a.getAssignmentType() != null ? a.getAssignmentType() : "Assignment";
        return String.format("[%s] %s — %s, due %s%s",
            type,
            a.getTitle(),
            courseNames.get(a.getCourseId()),
            a.getDueDate().format(DATE_FORMAT),
            a.getDueTime() != null ? " " + a.getDueTime() : "");
    }

    private String formatTask(Task t, Map<Long, String> courseNames) {
        return String.format("[Task] %s — %s, due %s",
            t.getTitle(),
            courseNames.get(t.getCourseId()),
            t.getDueDate().format(DATE_FORMAT));
    }
}
