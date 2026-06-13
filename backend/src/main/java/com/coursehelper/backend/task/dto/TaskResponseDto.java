package com.coursehelper.backend.task.dto;


import java.time.LocalDate;

import lombok.Getter;

@Getter
public class TaskResponseDto {

    Long id;
    Long courseId;
    Long userId;
    String title;
    LocalDate dueDate;
    Boolean completed;
    
    public TaskResponseDto(Long id, Long courseId, Long userId, String title, LocalDate dueDate, Boolean completed){
        this.id = id;
        this.courseId = courseId;
        this.userId = userId;
        this.title = title;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    
}
