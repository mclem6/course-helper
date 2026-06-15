package com.coursehelper.frontend.dto;

public class ChangeUsernameRequestDto {
    private final String newUsername;
    public ChangeUsernameRequestDto(String newUsername) { this.newUsername = newUsername; }
    public String getNewUsername() { return newUsername; }
}
