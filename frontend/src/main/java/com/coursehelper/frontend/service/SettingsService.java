package com.coursehelper.frontend.service;

import java.io.IOException;

import com.coursehelper.frontend.dto.SettingsRequestDto;
import com.coursehelper.frontend.dto.SettingsResponseDto;
import com.coursehelper.frontend.exceptions.ApiException;
import com.coursehelper.frontend.model.UserSettings;
import com.coursehelper.frontend.service.api.ApiClient;

public class SettingsService {

    private static SettingsService instance;
    private final ApiClient apiClient;
   

    public SettingsService(ApiClient apiClient){
        this.apiClient = apiClient;
    }

    public static SettingsService getInstance() {
        if (instance == null) {
            instance = new SettingsService(new ApiClient());
        }
        return instance;
    }

    //get user settings
    public UserSettings getSettings(){

        try {
            SettingsResponseDto dto = apiClient.get("/settings", SettingsResponseDto.class);
            if (dto == null) return null;
            return new UserSettings(
                dto.getSemester(),
                dto.getYear(),
                dto.getStartDate(),
                dto.getEndDate()
            );
        } catch (IOException e) {
            throw new ApiException("Unable to fetch settings. Check connection.", 503);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), 500); // ← message from backend
        }

    }

    public SettingsResponseDto saveSemesterSettings(SettingsRequestDto request){

        try {
            return apiClient.put("/settings", request, SettingsResponseDto.class);
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage()); // ← what does it say?
            throw new ApiException("Unable to update settings. Check connection.", 503);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), 500); // ← message from backend
        }


    }
    
}
