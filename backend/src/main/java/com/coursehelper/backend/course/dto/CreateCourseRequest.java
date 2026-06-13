package com.coursehelper.backend.course.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateCourseRequest {

    private String name;
    private String style;
    private String semester;
    private Integer courseYear;
    private LocalDate startDate;
    private LocalDate endDate;
    private String startTime;
    private String endTime;
    private Boolean recurring;
    private String lectureDays;

    
}
