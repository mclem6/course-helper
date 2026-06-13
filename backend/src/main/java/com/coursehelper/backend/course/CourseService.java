package com.coursehelper.backend.course;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.coursehelper.backend.course.dto.CourseResponseDto;
import com.coursehelper.backend.exceptions.ResourceNotFoundException;
import com.coursehelper.backend.user.User;
import com.coursehelper.backend.userSettings.SettingsRepository;
import com.coursehelper.backend.userSettings.UserSettings;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final SettingsRepository settingsRepository;

    public CourseService(CourseRepository courseRepository, SettingsRepository settingsRepository){
        this.courseRepository = courseRepository;
        this.settingsRepository = settingsRepository;
    }

    public Course addCourse(String name, String style, User user, String semester, Integer courseYear,
    LocalDate startDate, LocalDate endDate, String startTime, String endTime, Boolean recurring, String lectureDays ){

        Course course = new Course(name, style, user, semester, courseYear,
        startDate, endDate,startTime, endTime, recurring, lectureDays);
        return courseRepository.save(course);

    }


    public List<CourseResponseDto> getAllUserCourses(Long userId){

        List<Course> courses = courseRepository.findAllByUserId(userId);
        return courses.stream().map(this::toResponse).toList();

    }

    public List<CourseResponseDto> getUserCoursesBySemester(Long userId ,String semester, int year){
        
    
        // only return courses for current semester
        List<Course> courses = courseRepository.findByUserIdAndSemesterAndCourseYear(
            userId,
            semester,
            year
        );

        return courses.stream().map(this::toResponse).toList();
        
    }

    public CourseResponseDto toResponse(Course course){
        
        return new CourseResponseDto(course.getId(), course.getName(), course.getStyle(), course.getSemester(), course.getCourseYear(),
                course.getStartDate(), course.getEndDate(),course.getStartTime(), course.getEndTime(), course.getRecurring(), course.getLectureDays());

    }



}
