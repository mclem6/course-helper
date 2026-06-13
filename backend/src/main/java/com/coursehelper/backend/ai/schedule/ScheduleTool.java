package com.coursehelper.backend.ai.schedule;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.coursehelper.backend.course.Course;
import com.coursehelper.backend.course.CourseRepository;
import com.coursehelper.backend.userSettings.SettingsRepository;
import com.coursehelper.backend.userSettings.UserSettings;


@Component
public class ScheduleTool {

    private final CourseRepository courseRepository;
    private final SettingsRepository settingsRepository;

    public ScheduleTool(CourseRepository courseRepository,
                        SettingsRepository settingsRepository) {
        this.courseRepository = courseRepository;
        this.settingsRepository = settingsRepository;
    }

    public String search(String query, Long userId) {
        UserSettings settings = settingsRepository.findByUserId(userId).orElse(null);
        if (settings == null) {
            return "User settings not configured.";
        }

        String semester = settings.getCurrentSemester();
        int year = settings.getCurrentYear();

        List<Course> courses = findRelevantCourses(query, userId, semester, year);

        if (courses.isEmpty()) {
            return "No courses found" + (query != null && !query.isBlank() ? " matching: " + query : ".");
        }

        return formatCourses(courses);
    }

    private List<Course> findRelevantCourses(String query, Long userId, String semester, int year) {
        String lower = query != null ? query.toLowerCase() : "";

        List<String> days = List.of("monday", "tuesday", "wednesday",
                                     "thursday", "friday", "saturday", "sunday");

        for (String day : days) {
            if (lower.contains(day)) {
                return courseRepository.findByUserIdAndDayAndSemesterAndCourseYear(userId, day, semester, year);
            }
        }

        List<Course> semesterCourses = courseRepository
                .findByUserIdAndSemesterAndCourseYear(userId, semester, year);

        if (!lower.isBlank()) {
            List<Course> byName = semesterCourses.stream()
                .filter(c -> c.getName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
            if (!byName.isEmpty()) return byName;
        }

        return semesterCourses;
    }

    private String formatCourses(List<Course> courses) {
        return courses.stream()
                .map(c -> String.format(
                    "Course: %s\nDays: %s\nTime: %s - %s\nSemester: %s %s\nStart Date: %s",
                    c.getName(),
                    c.getLectureDays(),
                    c.getStartTime(),
                    c.getEndTime(),
                    c.getSemester(),
                    c.getCourseYear(),
                    c.getStartDate()
                ))
                .collect(Collectors.joining("\n\n---\n\n"));
    }
}
