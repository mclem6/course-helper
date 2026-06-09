package com.coursehelper.backend.auth;



public class CustomUserPrincipal{

    private Long userId;
    private String username;

    public CustomUserPrincipal(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public Long getUserId(){
        return userId;
    }

    public String getUsername(){
        return username;
    }




    
}
