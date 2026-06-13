package com.coursehelper.frontend.service;

import java.io.File;

import com.coursehelper.frontend.dto.ChangePasswordRequestDto;
import com.coursehelper.frontend.dto.ChangeUsernameRequestDto;
import com.coursehelper.frontend.dto.LoginRequestDto;
import com.coursehelper.frontend.dto.LoginResponseDto;
import com.coursehelper.frontend.dto.RegisterRequestDto;
import com.coursehelper.frontend.exceptions.ApiException;
import com.coursehelper.frontend.exceptions.LoginException;
import com.coursehelper.frontend.exceptions.RegisterException;
import com.coursehelper.frontend.service.api.ApiClient;

public class UserService {

    private static UserService instance;
    private final ApiClient apiClient;

    public UserService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService(new ApiClient());
        }
        return instance;
    }

    public LoginResponseDto registerUser(String username, String password) {
        try {
            LoginResponseDto response = apiClient.post("/register",
                new RegisterRequestDto(username, password), LoginResponseDto.class);
            if (response == null || response.getAccessToken() == null) {
                throw new RegisterException("Registration failed. Try again.");
            }
            return response;
        } catch (ApiException e) {
            throw e.getStatus() == 503
                ? new RegisterException("Unable to create account. Check connection.")
                : new RegisterException(e.getMessage());
        }
    }

    public LoginResponseDto loginUser(String username, String password) {
        try {
            LoginResponseDto response = apiClient.post("/auth/login",
                new LoginRequestDto(username, password), LoginResponseDto.class);
            if (response == null || response.getAccessToken() == null) {
                throw new LoginException("Login failed.");
            }
            return response;
        } catch (ApiException e) {
            throw e.getStatus() == 503
                ? new LoginException("Unable to login. Check connection.")
                : new LoginException(e.getMessage());
        }
    }

    public void uploadProfilePicture(File file) {
        try {
            apiClient.postFile("/users/profile-picture", file);
        } catch (ApiException e) {
            throw e.getStatus() == 503
                ? new ApiException("Failed to upload picture. Check connection.", 503) : e;
        }
    }

    public byte[] getProfilePicture() {
        try {
            return apiClient.getBytes("/users/profile-picture");
        } catch (ApiException e) {
            throw e.getStatus() == 503
                ? new ApiException("Failed to fetch picture. Check connection.", 503) : e;
        }
    }

    public void changeUsername(String newUsername) {
        try {
            apiClient.patch("/users/username", new ChangeUsernameRequestDto(newUsername), String.class);
        } catch (ApiException e) {
            throw e.getStatus() == 503
                ? new ApiException("Failed to update username. Check connection.", 503) : e;
        }
    }

    public void changePassword(String currentPassword, String newPassword) {
        try {
            apiClient.patch("/users/password", new ChangePasswordRequestDto(currentPassword, newPassword), String.class);
        } catch (ApiException e) {
            throw e.getStatus() == 503
                ? new ApiException("Failed to update password. Check connection.", 503) : e;
        }
    }
}
