package com.coursehelper.frontend.dto;

import java.time.LocalDate;

public class EventResponseDto {

    private Long id;
    private Long userId;
    private Long courseId;
    private String title;
    private String eventType;
    private Long assignmentId;
    private String startTime;
    private String endTime;
    private String recurringDays;
    private LocalDate startDate;
    private Boolean isRecurring;

    public EventResponseDto(){}

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }

    public void setCourseId(Long courseId){
        this.courseId = courseId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setRecurringDays(String recurringDays) {
        this.recurringDays = recurringDays;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

   
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }


    public Long getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getEventType() {
        return eventType;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getRecurringDays() {
        return recurringDays;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Boolean getIsRecurring() {
        return isRecurring;
    }
  
    
    
}
