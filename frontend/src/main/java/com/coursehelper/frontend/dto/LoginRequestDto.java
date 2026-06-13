package com.coursehelper.frontend.dto;

public class LoginRequestDto {

    private final String username;
    private final String password;

    public LoginRequestDto(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }


    
}
