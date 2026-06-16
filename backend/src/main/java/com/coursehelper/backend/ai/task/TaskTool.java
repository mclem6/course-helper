package com.coursehelper.backend.ai.task;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.coursehelper.backend.course.Course;
import com.coursehelper.backend.course.CourseRepository;
import com.coursehelper.backend.task.Task;
import com.coursehelper.backend.task.TaskRepository;
import com.coursehelper.backend.userSettings.SettingsRepository;
import com.coursehelper.backend.userSettings.UserSettings;

@Component
public class TaskTool {

    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;
    private final SettingsRepository settingsRepository;

    public TaskTool(TaskRepository taskRepository,
                    CourseRepository courseRepository,
                    SettingsRepository settingsRepository) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
        this.settingsRepository = settingsRepository;
    }

    public String search(String query, Long userId, String completed) {
        UserSettings settings = settingsRepository.findByUserId(userId).orElse(null);
        if (settings == null) {
            return "User settings not configured.";
        }

        List<Course> semesterCourses = courseRepository.findByUserIdAndSemesterAndCourseYear(
            userId, settings.getCurrentSemester(), settings.getCurrentYear());

        if (semesterCourses.isEmpty()) {
            return "No courses found for the current semester.";
        }

        Map<Long, String> courseNames = semesterCourses.stream()
            .collect(Collectors.toMap(Course::getId, Course::getName));
        Set<Long> courseIds = courseNames.keySet();

        List<Task> tasks = ("all".equalsIgnoreCase(completed)
                ? taskRepository.findByUserId(userId)
                : taskRepository.findByUserIdAndCompleted(userId,
                    "completed".equalsIgnoreCase(completed)))
            .stream()
            .filter(t -> courseIds.contains(t.getCourseId()))
            .collect(Collectors.toList());

        String lower = query != null ? query.toLowerCase() : "";
        List<Task> filtered = tasks.stream()
            .filter(t -> {
                String courseName = courseNames.get(t.getCourseId()).toLowerCase();
                return lower.isBlank()
                    || courseName.contains(lower)
                    || t.getTitle().toLowerCase().contains(lower);
            })
            .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            return "No tasks found" + (query != null && !query.isBlank() ? " matching: " + query : ".");
        }

        LocalDate today = LocalDate.now();
        List<Task> sorted = filtered.stream()
            .sorted(Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList());

        List<Task> overdue  = sorted.stream().filter(t -> t.getDueDate() != null && t.getDueDate().isBefore(today)).toList();
        List<Task> dueToday = sorted.stream().filter(t -> t.getDueDate() != null && t.getDueDate().isEqual(today)).toList();
        List<Task> upcoming = sorted.stream().filter(t -> t.getDueDate() != null && t.getDueDate().isAfter(today)).toList();

        StringBuilder sb = new StringBuilder();
        sb.append("=== OVERDUE ===\n");
        if (overdue.isEmpty()) sb.append("None\n\n");
        else overdue.forEach(t -> sb.append(formatTask(t, courseNames)).append("\n\n"));

        sb.append("=== DUE TODAY ===\n");
        if (dueToday.isEmpty()) sb.append("None\n\n");
        else dueToday.forEach(t -> sb.append(formatTask(t, courseNames)).append("\n\n"));

        sb.append("=== UPCOMING ===\n");
        if (upcoming.isEmpty()) sb.append("None\n\n");
        else upcoming.forEach(t -> sb.append(formatTask(t, courseNames)).append("\n\n"));

        return sb.toString().trim();
    }

    private String formatTask(Task t, Map<Long, String> courseNames) {
        return String.format("Title: %s\nCourse: %s\nDue: %s",
            t.getTitle(),
            courseNames.get(t.getCourseId()),
            t.getDueDate() != null ? t.getDueDate() : "No date");
    }
}
