package com.coursehelper.frontend.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Collections;
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
        } catch (IOException e) {
            throw new ApiException("Unable to create assignment. Check connection.", 503);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), 500);
        }
    }

    public Map<Long, List<Assignment>> getAllAssignments() {
        try {
            Map<Long, List<AssignmentResponseDto>> assignmentResponseList = apiClient.get(
                "/assignments", new TypeReference<Map<Long, List<AssignmentResponseDto>>>() {});

            if (assignmentResponseList == null || assignmentResponseList.isEmpty() ||
                assignmentResponseList.values().stream().allMatch(List::isEmpty)) {
                return Collections.emptyMap();
            }

            return assignmentResponseList.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().stream()
                        .map(dto -> new Assignment(dto.getId(), dto.getUserId(),
                            dto.getCourseId(), dto.getTitle(), dto.getDescription(),
                            dto.getDueDate(), dto.getDueTime(), dto.getStatus(),
                            dto.getAssignmentType()))
                        .collect(Collectors.toList())
                ));

        } catch (IOException e) {
            throw new ApiException("Unable to fetch assignments. Check connection.", 503);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), 500);
        }
    }
}