package com.coursehelper.frontend.dto;

import java.time.LocalDate;

public class AddTaskRequestDto {

    private String title;
    private Long courseId;
    private LocalDate dueDate;

    public AddTaskRequestDto(String title, Long courseId, LocalDate dueDate){
        this.title = title;
        this.courseId = courseId;
        this.dueDate = dueDate;
    }

    public String getTitle(){
        return title;
    }

    public Long getCourseId(){
        return courseId;
    }

    public LocalDate getDueDate(){
        return dueDate;
    }


    
}
