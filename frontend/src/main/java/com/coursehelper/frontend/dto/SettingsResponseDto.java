package com.coursehelper.frontend.dto;

import java.time.LocalDate;

public class SettingsResponseDto {

    String semester;
    Integer year;
    LocalDate startDate;
    LocalDate endDate;



    public SettingsResponseDto(){}

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getSemester(){
        return semester;
    }

    public Integer getYear() {
        return year;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    


    
}
