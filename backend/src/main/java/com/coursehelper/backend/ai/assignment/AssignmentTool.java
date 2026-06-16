package com.coursehelper.backend.ai.assignment;

import java.time.LocalDate;
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
import com.coursehelper.backend.userSettings.SettingsRepository;
import com.coursehelper.backend.userSettings.UserSettings;

@Component
public class AssignmentTool {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final SettingsRepository settingsRepository;

    public AssignmentTool(AssignmentRepository assignmentRepository,
                          CourseRepository courseRepository,
                          SettingsRepository settingsRepository) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.settingsRepository = settingsRepository;
    }

    public String search(String query, Long userId, String status) {
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

        List<Assignment> assignments = ("ALL".equalsIgnoreCase(status)
                ? assignmentRepository.findByUserId(userId)
                : assignmentRepository.findByUserIdAndStatus(userId,
                    "COMPLETED".equalsIgnoreCase(status) ? "COMPLETED" : "INCOMPLETE"))
            .stream()
            .filter(a -> courseIds.contains(a.getCourseId()))
            .collect(Collectors.toList());

        String lower = query != null ? query.toLowerCase() : "";
        List<Assignment> filtered = assignments.stream()
            .filter(a -> {
                String courseName = courseNames.get(a.getCourseId()).toLowerCase();
                return lower.isBlank()
                    || courseName.contains(lower)
                    || a.getTitle().toLowerCase().contains(lower)
                    || (a.getAssignmentType() != null && a.getAssignmentType().toLowerCase().contains(lower))
                    || (a.getStatus() != null && a.getStatus().toLowerCase().contains(lower));
            })
            .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            return "No assignments found" + (query != null && !query.isBlank() ? " matching: " + query : ".");
        }

        LocalDate today = LocalDate.now();
        List<Assignment> sorted = filtered.stream()
            .sorted(Comparator.comparing(Assignment::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList());

        List<Assignment> overdue  = sorted.stream().filter(a -> a.getDueDate() != null && a.getDueDate().isBefore(today)).toList();
        List<Assignment> dueToday = sorted.stream().filter(a -> a.getDueDate() != null && a.getDueDate().isEqual(today)).toList();
        List<Assignment> upcoming = sorted.stream().filter(a -> a.getDueDate() != null && a.getDueDate().isAfter(today)).toList();

        StringBuilder sb = new StringBuilder();
        sb.append("=== OVERDUE ===\n");
        if (overdue.isEmpty()) sb.append("None\n\n");
        else overdue.forEach(a -> sb.append(formatAssignment(a, courseNames)).append("\n\n"));

        sb.append("=== DUE TODAY ===\n");
        if (dueToday.isEmpty()) sb.append("None\n\n");
        else dueToday.forEach(a -> sb.append(formatAssignment(a, courseNames)).append("\n\n"));

        sb.append("=== UPCOMING ===\n");
        if (upcoming.isEmpty()) sb.append("None\n\n");
        else upcoming.forEach(a -> sb.append(formatAssignment(a, courseNames)).append("\n\n"));

        return sb.toString().trim();
    }

    private String formatAssignment(Assignment a, Map<Long, String> courseNames) {
        return String.format("Title: %s\nCourse: %s\nType: %s\nDue: %s%s\nStatus: %s",
            a.getTitle(),
            courseNames.get(a.getCourseId()),
            a.getAssignmentType() != null ? a.getAssignmentType() : "N/A",
            a.getDueDate() != null ? a.getDueDate() : "No date",
            a.getDueTime() != null ? " " + a.getDueTime() : "",
            a.getStatus() != null ? a.getStatus() : "N/A");
    }
}
