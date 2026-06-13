package com.coursehelper.backend.auth.dto;

import lombok.Data;

@Data
public class LoginResponseDto {

    private String accessToken;
    private String username;
    private Long id;
    private String theme;

    public LoginResponseDto(String accessToken, Long id, String username){
        this.accessToken = accessToken;
        this.id = id;
        this.username = username;
        this.theme = "LightMode";
    }

    public LoginResponseDto(String accessToken, Long id, String username, String theme){
        this.accessToken = accessToken;
        this.id = id;
        this.username = username;
        this.theme = theme;
    }

}
