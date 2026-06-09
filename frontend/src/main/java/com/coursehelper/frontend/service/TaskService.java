package com.coursehelper.frontend.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.coursehelper.frontend.dto.AddTaskRequestDto;
import com.coursehelper.frontend.dto.TaskResponseDto;
import com.coursehelper.frontend.exceptions.ApiException;
import com.coursehelper.frontend.model.Task;
import com.coursehelper.frontend.service.api.ApiClient;
import com.fasterxml.jackson.core.type.TypeReference;

public class TaskService {

    private static TaskService instance;
    private final ApiClient apiClient;

    public TaskService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public static TaskService getInstance() {
        if (instance == null) {
            instance = new TaskService(new ApiClient());
        }
        return instance;
    }

    public TaskResponseDto addTask(AddTaskRequestDto request) {
        try {
            return apiClient.post("/task", request, TaskResponseDto.class);
        } catch (IOException e) {
            throw new ApiException("Unable to create task. Check connection.", 503);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), 500);
        }
    }

    public void updateCompletion(Long taskId, boolean completed) {
        try {
            apiClient.patch("/task/" + taskId + "/complete?completed=" + completed, null, Void.class);
        } catch (IOException e) {
            throw new ApiException("Unable to update task. Check connection.", 503);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), 500);
        }
    }

    public void deleteTask(Long taskId) {
        try {
            apiClient.delete("/task/" + taskId);
        } catch (IOException e) {
            throw new ApiException("Unable to delete task. Check connection.", 503);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), 500);
        }
    }

    public Map<Long, List<Task>> getAllTasks() {
        try {
            Map<Long, List<TaskResponseDto>> taskResponseList = apiClient.get(
                "/tasks", new TypeReference<Map<Long, List<TaskResponseDto>>>() {});

            if (taskResponseList == null || taskResponseList.isEmpty() ||
                taskResponseList.values().stream().allMatch(List::isEmpty)) {
                return Collections.emptyMap();
            }

            return taskResponseList.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().stream()
                        .map(dto -> new Task(dto.getId(), dto.getCourseId(),
                            dto.getUserId(), dto.getTitle(),
                            dto.getDueDate(), dto.getCompleted()))
                        .collect(Collectors.toList())
                ));

        } catch (IOException e) {
            throw new ApiException("Unable to fetch tasks. Check connection.", 503);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), 500);
        }
    }
}