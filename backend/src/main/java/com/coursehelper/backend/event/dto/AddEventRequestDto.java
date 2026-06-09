package com.coursehelper.backend.event.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class AddEventRequestDto {

    private Long courseId;
    private String title;
    private String eventType;
    private Long assignmentId;
    private String startTime;
    private String endTime;
    private String recurringDays;
    private LocalDate startDate;
    private Boolean isRecurring;

    
}
