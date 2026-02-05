package com.coursehelper.model;

public class Task {

    private String name;
    private Boolean completed;


    public Task(String name, Boolean completed){
        this.name = name;
        this.completed = completed;

    }

    public String getName(){
        return name;
    }


    public Boolean isCompleted(){
        return completed;
    }

    public void setCompleted(Boolean completed){
        this.completed = completed;
    }

    
    
}
