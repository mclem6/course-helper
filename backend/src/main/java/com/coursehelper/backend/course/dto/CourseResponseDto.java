package com.coursehelper.backend.course.dto;


import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CourseResponseDto {

    Long id;
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

    public CourseResponseDto(Long id, String name, String style, String semester, 
    Integer courseYear, LocalDate startDate, LocalDate endDate,String startTime, String endTime,
    Boolean recurring, String lectureDays){
     
     this.id = id;
     this.name = name; 
     this.style = style;
     this.semester = semester;
     this.courseYear = courseYear;
     this.startDate = startDate;
     this.endDate = endDate;
     this.startTime = startTime;
     this.endTime = endTime;
     this.recurring = recurring;
     this.lectureDays = lectureDays;

    }

    
    
}
