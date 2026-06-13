package com.coursehelper.frontend.model;

import java.time.LocalDate;

public class UserSettings {

    String semester;
    Integer year;
    LocalDate semesterStart;
    LocalDate semesterEnd;

    public UserSettings(String semester, Integer year, LocalDate semesterStart, LocalDate semesterEnd){
        
        this.semester = semester;
        this.year = year;
        this.semesterStart = semesterStart;
        this.semesterEnd = semesterEnd;
    }


    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void setSemesterStart(LocalDate startDate) {
        this.semesterStart = startDate;
    }

    public void setSemesterEnd(LocalDate endDate) {
        this.semesterEnd = endDate;
    }


    public Integer getYear() {
        return year;
    }

    public String getSemester() {
        return semester;
    }

    public LocalDate getStartDate() {
        return semesterStart;
    }

    public LocalDate getEndDate() {
        return semesterEnd;
    }

    

    
}
