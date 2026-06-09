package com.coursehelper.frontend.dto;

public class LoginResponseDto {

    private String accessToken;
    private String username;
    private Long id;
    private String theme;


    public LoginResponseDto() {}

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

    public void setAccessToken(String accessToken){
        this.accessToken = accessToken;
        
    }

    public String getAccessToken(){
        return accessToken;
        
    }
    public void setId(Long id){
        this.id = id;
    }

    public Long getId(){
        return id;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return username;
    }

    public void setTheme(String theme){
        this.theme = theme;
    }

    public String getTheme(){
        return this.theme;
    }


}
    

