package com.coursehelper.frontend.service;

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
            return apiClient.post("/agent/chat", new ChatRequestDto(message), String.class);
        } catch (ApiException e) {
            throw new ApiException(friendlyMessage(e.getStatus()), e.getStatus());
        }
    }

    private String friendlyMessage(int status) {
        return switch (status) {
            case 401, 403 -> "Your session has expired. Please sign out and sign back in.";
            case 404      -> "Could not reach the assistant. Please try again.";
            case 429      -> "Too many requests. Please wait a moment and try again.";
            case 500, 502 -> "Something went wrong on our end. Please try again shortly.";
            case 503      -> "The server is unavailable. Check your connection and try again.";
            default       -> "Something went wrong (error " + status + "). Please try again.";
        };
    }
}
