package com.coursehelper.frontend.model;

import java.time.LocalDate;

public class Assignment {

    
   //Status {INCOMPLETE, COMPLETED}
   // AssignmentType {HOMEWORK, TEST, PROJECT, LAB}
  

    private Long id;
    private Long userId;
    private Long courseId;        
    private String title;
    private String description;
    private LocalDate dueDate;    
    private String dueTime;    
    private String status;
    private String assignmentType;          

    public Assignment() {}  

    public Assignment(Long id, Long userId, Long courseId, String title,
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

    // getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getDueDate() { return dueDate; }
    public String getDueTime() { return dueTime; }
    public String getStatus() { return status; }
    public String getAssignmentType() { return assignmentType; }

    // setters 
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setDueTime(String dueTime) { this.dueTime = dueTime; }
    public void setStatus(String status) { this.status = status; }
    public void setAssignmentType(String assignmentType) { this.assignmentType = assignmentType; }

}