package com.coursehelper.frontend.service;

import java.io.File;
import java.io.IOException;

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
        RegisterRequestDto request = new RegisterRequestDto(username, password);
        try {
            LoginResponseDto response = apiClient.post("/register", request, LoginResponseDto.class);
            if (response == null || response.getAccessToken() == null) {
                throw new RegisterException("Registration failed. Try again.");
            }
            return response;
        } catch (IOException e) {
            throw new RegisterException("Unable to create account. Check connection.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RegisterException("Request interrupted.");
        } catch (RuntimeException e) {
            throw new RegisterException(e.getMessage());
        }
    }

    public LoginResponseDto loginUser(String username, String password) {
        LoginRequestDto request = new LoginRequestDto(username, password);
        try {
            LoginResponseDto response = apiClient.post("/auth/login", request, LoginResponseDto.class);
            if (response == null || response.getAccessToken() == null) {
                throw new LoginException("Login failed.");
            }
            return response;
        } catch (IOException e) {
            throw new LoginException("Unable to login. Check connection.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LoginException("Request interrupted.");
        } catch (RuntimeException e) {
            throw new LoginException(e.getMessage());
        }
    }

    public void uploadProfilePicture(File file) {
        try {
            apiClient.postFile("/users/profile-picture", file);
        } catch (IOException e) {
            throw new ApiException("Failed to upload picture. Check connection.", 503);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), 500);
        }
    }

    public byte[] getProfilePicture() {
        try {
            return apiClient.getBytes("/users/profile-picture");
        } catch (IOException e) {
            throw new ApiException("Failed to fetch picture. Check connection.", 503);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Failed to fetch picture. Request interrupted.", 503);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), 500);
        }
    }
}