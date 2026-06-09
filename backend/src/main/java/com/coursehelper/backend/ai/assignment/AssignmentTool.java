package com.coursehelper.backend.ai.assignment;

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

    public String search(String query, Long userId) {
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

        List<Assignment> assignments = assignmentRepository.findByUserId(userId).stream()
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

        return filtered.stream()
            .map(a -> String.format(
                "Title: %s\nCourse: %s\nType: %s\nDue: %s%s\nStatus: %s",
                a.getTitle(),
                courseNames.get(a.getCourseId()),
                a.getAssignmentType() != null ? a.getAssignmentType() : "N/A",
                a.getDueDate() != null ? a.getDueDate() : "No date",
                a.getDueTime() != null ? " " + a.getDueTime() : "",
                a.getStatus() != null ? a.getStatus() : "N/A"
            ))
            .collect(Collectors.joining("\n\n---\n\n"));
    }
}
