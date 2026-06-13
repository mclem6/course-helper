package com.coursehelper.backend.course;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coursehelper.backend.auth.CustomUserPrincipal;
import com.coursehelper.backend.course.dto.CourseResponseDto;
import com.coursehelper.backend.course.dto.CreateCourseRequest;
import com.coursehelper.backend.user.User;
import com.coursehelper.backend.user.UserService;


@RestController
@RequestMapping("/api")
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;


    public CourseController(CourseService courseService, UserService userservice){
        this.courseService = courseService;
        this.userService = userservice;
    }

    @PostMapping("/course")
    public ResponseEntity<CourseResponseDto> addCourse(@RequestBody CreateCourseRequest request, Authentication auth){

        CustomUserPrincipal user = 
            (CustomUserPrincipal) auth.getPrincipal();

        Long userId = user.getUserId();
        User userObj = userService.findByUserId(userId);

        //create event

        Course course = courseService.addCourse(request.getName(), request.getStyle(), userObj, request.getSemester(),
                        request.getCourseYear(), request.getStartDate(), request.getEndDate(), request.getStartTime(),
                        request.getEndTime(), request.getRecurring(), request.getLectureDays());

        return ResponseEntity.ok(courseService.toResponse(course));

    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponseDto>> getUserCourses(
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Integer year,
            Authentication auth) {

        Long userId = ((CustomUserPrincipal) auth.getPrincipal()).getUserId();

        List<CourseResponseDto> courses;

        if (semester != null && year != null) {
            // filter by semester and year
            courses = courseService.getUserCoursesBySemester(userId, semester, year);
        } else {
            // return all courses
            courses = courseService.getAllUserCourses(userId);
        }

        return ResponseEntity.ok(courses);
    }





    // @DeleteMapping
    // public 




    
}
