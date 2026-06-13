package com.coursehelper.backend.ai.task;

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

        return filtered.stream()
            .map(t -> String.format(
                "Title: %s\nCourse: %s\nDue: %s\nCompleted: %s",
                t.getTitle(),
                courseNames.get(t.getCourseId()),
                t.getDueDate() != null ? t.getDueDate() : "No date",
                Boolean.TRUE.equals(t.getCompleted()) ? "Yes" : "No"
            ))
            .collect(Collectors.joining("\n\n---\n\n"));
    }
}
