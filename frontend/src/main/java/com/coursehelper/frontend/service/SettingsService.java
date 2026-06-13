package com.coursehelper.frontend.service;

import com.coursehelper.frontend.dto.SettingsRequestDto;
import com.coursehelper.frontend.dto.SettingsResponseDto;
import com.coursehelper.frontend.exceptions.ApiException;
import com.coursehelper.frontend.model.UserSettings;
import com.coursehelper.frontend.service.api.ApiClient;

public class SettingsService {

    private static SettingsService instance;
    private final ApiClient apiClient;

    public SettingsService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public static SettingsService getInstance() {
        if (instance == null) {
            instance = new SettingsService(new ApiClient());
        }
        return instance;
    }

    public UserSettings getSettings() {
        try {
            SettingsResponseDto dto = apiClient.get("/settings", SettingsResponseDto.class);
            if (dto == null) return null;
            return new UserSettings(dto.getSemester(), dto.getYear(), dto.getStartDate(), dto.getEndDate());
        } catch (ApiException e) {
            throw e.getStatus() == 503
                ? new ApiException("Unable to fetch settings. Check connection.", 503) : e;
        }
    }

    public SettingsResponseDto saveSemesterSettings(SettingsRequestDto request) {
        try {
            return apiClient.put("/settings", request, SettingsResponseDto.class);
        } catch (ApiException e) {
            throw e.getStatus() == 503
                ? new ApiException("Unable to update settings. Check connection.", 503) : e;
        }
    }
}
