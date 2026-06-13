package com.coursehelper.backend.task.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AddTaskRequestDto {

    private String title;
    private Long courseId;
    private LocalDate dueDate;

    public AddTaskRequestDto(String title, Long courseId, LocalDate dueDate){
        this.title = title;
        this.courseId = courseId;
        this.dueDate = dueDate;
    }

    
}
