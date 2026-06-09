package com.coursehelper.frontend.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class AddCourseRequestDto {

    private String name;
    private String style;
    private String semester;
    private Integer courseYear;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    private LocalDate endDate;
    private String startTime;
    private String endTime;
    private Boolean recurring;
    private String lectureDays;

    public AddCourseRequestDto() {}

    public AddCourseRequestDto( String name, String style, String semester, 
    Integer courseYear, LocalDate startDate, LocalDate endDate, String startTime, String endTime,
    Boolean recurring, String lectureDays){

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

    public String getName(){
        return this.name;
    }

    public String getStyle(){
        return this.style;
        
    }

    public String getSemester(){
        return this.semester;
    }

    public Integer getCourseYear(){
        return this.courseYear;
    }

    public LocalDate getStartDate(){
        return this.startDate;
    }

    public LocalDate getEndDate(){
        return this.endDate;
    }

    public String getStartTime(){
        return this.startTime;
    }

    public String getEndTime(){
        return this.endTime;
    }

    public boolean getRecurring(){
        return this.recurring;
    }

    public String getLectureDays(){
        return this.lectureDays;
    }


    
}
