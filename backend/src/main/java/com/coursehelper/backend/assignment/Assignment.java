package com.coursehelper.backend.assignment;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity 
@Getter 
@Setter
@NoArgsConstructor
@Table(name = "assignments")
public class Assignment {

    //Status {INCOMPLETE, COMPLETED}
    // AssignmentType {HOMEWORK, TEST, PROJECT, LAB}
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long courseId;        
    private String title;
    private String description;
    private LocalDate dueDate;    
    private String dueTime;    
    private String status;
    private String assignmentType;      
    
    @CreationTimestamp
    private LocalDateTime createdAt;

    public Assignment(Long userId, Long courseId, String title, String description, 
    LocalDate dueDate, String dueTime, String status, String assignmentType){

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
