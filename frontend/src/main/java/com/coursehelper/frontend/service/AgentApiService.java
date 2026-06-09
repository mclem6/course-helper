package com.coursehelper.frontend.service;

import java.io.IOException;

import com.coursehelper.frontend.dto.ChatRequestDto;
import com.coursehelper.frontend.exceptions.ApiException;
import com.coursehelper.frontend.service.api.ApiClient;

public class AgentApiService {

    private final ApiClient apiClient;

    public AgentApiService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public String chat(String message) {
        try {
            return apiClient.post("/agent/chat",
                new ChatRequestDto(message), String.class);
        } catch (IOException e) {
            throw new ApiException("Network error. Check connection.", 503);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), 500);
        }
    }
}