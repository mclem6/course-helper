package com.coursehelper.backend.assignment.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter 
public class AssignmentResponseDto {

    private Long id;
    private Long userId;
    private Long courseId;        
    private String title;
    private String description;
    private LocalDate dueDate;    
    private String dueTime;    
    private String status;
    private String assignmentType;       


    public AssignmentResponseDto(Long id, Long userId, Long courseId, String title,
                      String description, LocalDate dueDate, String dueTime,
                      String status, String assignmentType) {
        this.id = id;
        this.userId = userId;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.status = status;
        this.assignmentType = assignmentType;
    }



    
}
