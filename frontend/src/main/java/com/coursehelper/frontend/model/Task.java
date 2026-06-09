package com.coursehelper.frontend.model;

import java.time.LocalDate;

public class Task {

    Long id;
    Long courseId;
    Long userId;
    private String title;
    LocalDate dueDate;
    private Boolean completed;


    public Task(Long id, Long courseId, Long userId, String title, LocalDate dueDate, Boolean completed){
        this.id = id;
        this.courseId = courseId;
        this.userId = userId;
        this.title = title;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    public Task(Long id, Long courseId, Long userId, String title, Boolean completed){
        this.id = id;
        this.courseId = courseId;
        this.userId = userId;
        this.title = title;
        this.completed = completed;
    }


    public void setId(Long id){
        this.id = id;
    }

    public void setCourseId(Long courseId){
        this.courseId = courseId;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setDueDate(LocalDate dueDate){
        this.dueDate = dueDate;
    }

    public void setCompleted(Boolean completed){
        this.completed = completed;
    }

    public Long getId(){
        return id;
    }

    public Long getCourseId(){
        return courseId;
    }

    public Long getUserId(){
        return userId;
    }

    public String getTitle(){
        return title;
    }

    public LocalDate getDueDate(){
        return dueDate;
    }

    public Boolean getCompleted(){
        return completed;
    }
  
    
}
