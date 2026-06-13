package com.coursehelper.frontend.service;

import java.util.List;

import com.coursehelper.frontend.dto.AddCourseRequestDto;
import com.coursehelper.frontend.dto.CourseResponseDto;
import com.coursehelper.frontend.exceptions.ApiException;
import com.coursehelper.frontend.model.Course;
import com.coursehelper.frontend.service.api.ApiClient;
import com.fasterxml.jackson.core.type.TypeReference;

public class CourseService {

    public static final String SOURCE_TYPE = "class";

    private static CourseService instance;
    private final ApiClient apiClient;

    public CourseService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public static CourseService getInstance() {
        if (instance == null) {
            instance = new CourseService(new ApiClient());
        }
        return instance;
    }

    public CourseResponseDto addCourse(AddCourseRequestDto request) {
        try {
            return apiClient.post("/course", request, CourseResponseDto.class);
        } catch (ApiException e) {
            throw e.getStatus() == 503
                ? new ApiException("Unable to create course. Check connection.", 503) : e;
        }
    }

    public List<Course> getUserCourses() {
        try {
            List<CourseResponseDto> dtos = apiClient.get("/courses", new TypeReference<List<CourseResponseDto>>() {});
            return dtos.stream().map(this::toModel).toList();
        } catch (ApiException e) {
            throw e.getStatus() == 503
                ? new ApiException("Unable to fetch courses. Check connection.", 503) : e;
        }
    }

    public List<Course> getUserSemesterCourses(String semester, int year) {
        try {
            List<CourseResponseDto> dtos = apiClient.get(
                "/courses?semester=" + semester + "&year=" + year,
                new TypeReference<List<CourseResponseDto>>() {});
            return dtos.stream().map(this::toModel).toList();
        } catch (ApiException e) {
            throw e.getStatus() == 503
                ? new ApiException("Unable to fetch courses. Check connection.", 503) : e;
        }
    }

    private Course toModel(CourseResponseDto dto) {
        return new Course(dto.getId(), dto.getName(), dto.getStyle(),
            dto.getSemester(), dto.getCourseYear(), dto.getStartDate(), dto.getEndDate(),
            dto.getStartTime(), dto.getEndTime(), dto.getRecurring(), dto.getLectureDays());
    }
}
