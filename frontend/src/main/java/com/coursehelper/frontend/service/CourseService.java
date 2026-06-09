package com.coursehelper.frontend.service;

import java.io.IOException;
import java.util.List;

import com.coursehelper.frontend.dto.AddCourseRequestDto;
import com.coursehelper.frontend.dto.CourseResponseDto;
import com.coursehelper.frontend.exceptions.ApiException;
import com.coursehelper.frontend.model.Course;
import com.coursehelper.frontend.service.api.ApiClient;
import com.fasterxml.jackson.core.type.TypeReference;

public class CourseService {

    private static CourseService instance;
    private final ApiClient apiClient;

    public static final String SOURCE_TYPE = "class";

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
        } catch (IOException e) {
            throw new ApiException("Unable to create course. Check connection.", 503);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), 500);
        }
    }

    public List<Course> getUserCourses() {
        try {
            List<CourseResponseDto> courseResponseList = apiClient.get(
                "/courses", new TypeReference<List<CourseResponseDto>>() {});

            return courseResponseList.stream()
                .map(dto -> new Course(dto.getId(), dto.getName(), dto.getStyle(),
                    dto.getSemester(), dto.getCourseYear(), dto.getStartDate(), dto.getEndDate(),
                    dto.getStartTime(), dto.getEndTime(), dto.getRecurring(),
                    dto.getLectureDays()))
                .toList();

        } catch (IOException e) {
            throw new ApiException("Unable to fetch courses. Check connection.", 503);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), 500);
        }
    }

    public List<Course> getUserSemesterCourses(String semester, int year) {
        try {
            List<CourseResponseDto> courseResponseList = apiClient.get(
                "/courses?semester=" + semester + "&year=" + year, new TypeReference<List<CourseResponseDto>>() {});

            return courseResponseList.stream()
                .map(dto -> new Course(dto.getId(), dto.getName(), dto.getStyle(),
                    dto.getSemester(), dto.getCourseYear(), dto.getStartDate(), dto.getEndDate(),
                    dto.getStartTime(), dto.getEndTime(), dto.getRecurring(),
                    dto.getLectureDays()))
                .toList();

        } catch (IOException e) {
            throw new ApiException("Unable to fetch courses. Check connection.", 503);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), 500);
        }
    }
}