package com.coursehelper.frontend.dto;

import java.time.LocalDate;

public class AddAssignmentRequestDto {

    private Long courseId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private String dueTime;
    private String status;
    private String assignmentType;


    public AddAssignmentRequestDto(Long courseId, String title, String description, LocalDate dueDate,
            String dueTime, String status, String assignmentType){
                this.courseId = courseId;
                this.title = title;
                this.description = description;
                this.dueDate = dueDate;
                this.dueTime = dueTime;
                this.status = status;
                this.assignmentType = assignmentType;
            
            
    }

    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setDueTime(String dueTime) { this.dueTime = dueTime; }
    public void setStatus(String status) { this.status = status; }
    public void setAssignmentType(String assignmentType) { this.assignmentType = assignmentType; }

    public Long getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getDueDate() { return dueDate; }
    public String getDueTime() { return dueTime; }
    public String getStatus() { return status; }
    public String getAssignmentType() { return assignmentType; }

}
