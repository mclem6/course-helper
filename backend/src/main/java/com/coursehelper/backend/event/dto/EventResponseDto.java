package com.coursehelper.backend.event.dto;


import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EventResponseDto {

    private Long id;
    private Long courseId;
    private String title;
    private String eventType;
    private Long assignmentId;
    private String startTime;
    private String endTime;
    private String recurringDays;
    private LocalDate startDate;
    private Boolean isRecurring;

    public EventResponseDto(Long id, Long courseId, String title, String eventType, Long assignmentId, LocalDate startDate, String startTime, String endTime , Boolean isRecurring, String recurringDays){
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.eventType = eventType;
        this.assignmentId = assignmentId;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isRecurring = isRecurring;
        this.recurringDays = recurringDays;
    }


    
}
