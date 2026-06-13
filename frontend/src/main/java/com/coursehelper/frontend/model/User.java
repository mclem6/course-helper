package com.coursehelper.frontend.model;

public class User {

    private Long id;
    private String username;
    private String theme;

    public User(Long id, String username, String theme){
        this.id = id;
        this.username = username;
        this.theme = theme;
    }


    public Long getId(){
        return id;
    }

    public String getUsername(){
        return username;
    }

    public String getTheme(){
        return theme;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setId(Long id){
        this.id = id;
    }

    public void setTheme(String theme){
        this.theme = theme;
    }

    
}
