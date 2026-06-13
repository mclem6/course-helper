package com.coursehelper.frontend.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.coursehelper.frontend.service.api.ApiClient;
import com.coursehelper.frontend.dto.AddAssignmentRequestDto;
import com.coursehelper.frontend.dto.AssignmentCreatedResponseDto;
import com.coursehelper.frontend.dto.AssignmentResponseDto;
import com.coursehelper.frontend.exceptions.ApiException;
import com.coursehelper.frontend.model.Assignment;
import com.fasterxml.jackson.core.type.TypeReference;

public class AssignmentService {

    private static AssignmentService instance;
    private final ApiClient apiClient;

    public AssignmentService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public static AssignmentService getInstance() {
        if (instance == null) {
            instance = new AssignmentService(new ApiClient());
        }
        return instance;
    }

    public AssignmentCreatedResponseDto addAssignment(AddAssignmentRequestDto request) {
        try {
            return apiClient.post("/assignment", request, AssignmentCreatedResponseDto.class);
        } catch (ApiException e) {
            throw e.getStatus() == 503
                ? new ApiException("Unable to create assignment. Check connection.", 503) : e;
        }
    }

    public void markComplete(Long assignmentId) {
        try {
            apiClient.patch("/assignment/" + assignmentId + "/complete", null, Void.class);
        } catch (ApiException e) {
            throw e.getStatus() == 503
                ? new ApiException("Unable to update assignment. Check connection.", 503) : e;
        }
    }

    public Map<Long, List<Assignment>> getAllAssignments(String status) {
        try {
            String url = status != null ? "/assignments?status=" + status : "/assignments";
            Map<Long, List<AssignmentResponseDto>> response = apiClient.get(
                url, new TypeReference<Map<Long, List<AssignmentResponseDto>>>() {});

            if (response == null || response.isEmpty() ||
                    response.values().stream().allMatch(List::isEmpty)) {
                return Collections.emptyMap();
            }

            return response.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().stream()
                        .map(dto -> new Assignment(dto.getId(), dto.getUserId(),
                            dto.getCourseId(), dto.getTitle(), dto.getDescription(),
                            dto.getDueDate(), dto.getDueTime(), dto.getStatus(),
                            dto.getAssignmentType()))
                        .collect(Collectors.toList())
                ));
        } catch (ApiException e) {
            throw e.getStatus() == 503
                ? new ApiException("Unable to fetch assignments. Check connection.", 503) : e;
        }
    }
}
