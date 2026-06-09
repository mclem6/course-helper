package com.coursehelper.backend.ai.schedule;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.coursehelper.backend.course.Course;
import com.coursehelper.backend.course.CourseRepository;


@Component
public class ScheduleTool {


    private final CourseRepository courseRepository;

    public ScheduleTool(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public String search(String query, Long userId) {

        // try to find courses matching the query as a name or day
        List<Course> courses = findRelevantCourses(query, userId);

        if (courses.isEmpty()) {
            return "No courses found matching: " + query;
        }

        return formatCourses(courses);
    }

    private List<Course> findRelevantCourses(String query, Long userId) {
        String lower = query.toLowerCase();

        // return courses for current semester 
        List<String> days = List.of("monday", "tuesday", "wednesday",
                                     "thursday", "friday", "saturday", "sunday");

        for (String day : days) {
            if (lower.contains(day)) {
                return courseRepository.findByUserIdAndDay(userId, day);
            }
        }

        // otherwise search by course name
        List<Course> byName = courseRepository
                .findByUserIdAndNameContainingIgnoreCase(userId, query);

        if (!byName.isEmpty()) {
            return byName;
        }

        // fallback — return all courses for the user
        return courseRepository.findAllByUserId(userId);
    }

    private String formatCourses(List<Course> courses) {
        return courses.stream()
                .map(c -> String.format(
                    "Course: %s\nDays: %s\nTime: %s - %s\nSemester: %s %s\nStart Date: ",
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
