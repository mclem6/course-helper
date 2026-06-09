package com.coursehelper.backend.assignment.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddAssignmentRequestDto {

    private Long courseId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private String dueTime;
    private String status;
    private String assignmentType;


    
}
