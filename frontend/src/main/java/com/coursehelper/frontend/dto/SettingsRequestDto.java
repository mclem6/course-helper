package com.coursehelper.frontend.dto;

import java.time.LocalDate;

public class SettingsRequestDto {

    String semester;
    int year;
    LocalDate startDate;
    LocalDate endDate;


    public SettingsRequestDto(String semester, int year, LocalDate startDate, LocalDate endDate){
        this.semester = semester;
        this.year = year;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getSemester(){
        return semester;
    }

    public int getYear(){
        return year;
    }

    public LocalDate getStartDate(){
        return startDate;
    }

    public LocalDate getEndDate(){
        return endDate;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    
    
}
