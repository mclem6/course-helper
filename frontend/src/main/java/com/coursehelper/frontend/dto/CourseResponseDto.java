package com.coursehelper.frontend.dto;

import java.time.LocalDate;

public class CourseResponseDto {


    private Long id;
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


    

    public CourseResponseDto(){}


    public void setId(Long id){
        this.id  = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setStyle(String style){
        this.style =style;
    }

    public void setsSemester(String semester){
        this.semester = semester;
    }


    public void setCourseYear(Integer courseYear){
        this.courseYear = courseYear;
    }

    public void setStartDate(LocalDate startDate){
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate){
        this.endDate = endDate;
    }

    public void setStartTime(String startTime){
        this.startTime = startTime;
    }


    public void setEndTime(String endTime){
        this.endTime = endTime;
    }

    public void setRecurring(Boolean recurring){
        this.recurring = recurring;
    }

    public void setLectureDays(String lectureDays){
        this.lectureDays = lectureDays;
    }



    public Long getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public String getStyle(){
        return this.style;
    }

    public String getSemester() {
    return semester;
    }

    public Integer getCourseYear() {
        return courseYear;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public Boolean getRecurring() {
        return recurring;
    }

    public String getLectureDays() {
        return lectureDays;
    }

    
    



    
}
