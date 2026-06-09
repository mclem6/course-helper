package com.coursehelper.frontend.dto;

import java.time.LocalDate;

public class AddEventRequestDto {

    private Long courseId;
    private String title;
    private String eventType;
    private String startTime;
    private String endTime;
    private String recurringDays;
    private LocalDate startDate;
    private Boolean isRecurring;

    public AddEventRequestDto( Long courseId, String title, String eventType, LocalDate startDate, String startTime, String endTime , Boolean isRecurring, String recurringDays){

        this.courseId = courseId;
        this.title = title;
        this.eventType = eventType;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isRecurring = isRecurring;
        this.recurringDays = recurringDays;
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
