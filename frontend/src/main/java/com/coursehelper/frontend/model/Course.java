package com.coursehelper.frontend.model;

import java.time.LocalDate;

import com.calendarfx.model.Calendar;


public class Course {

    private final Long id;
    private final String name;
    private final String style;
    private String semester;
    private Integer courseYear;
    private LocalDate startDate;
    private LocalDate endDate;
    private String startTime;
    private String endTime;
    private Boolean recurring;
    private String lectureDays;


    //counstructors

    public Course(Long id, String name, String style){
        this.id = id;
        this.name = name;
        this.style = style;
    }

    public Course(Long id, String name, String style, String semester, Integer courseYear,
    LocalDate startDate, LocalDate endDate, String startTime, String endTime,
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


    public String getName(){
        return name;
    }

    public Long getId(){
        return id;
    }

    public String getStyle(){
        return style;
    }

    public String getStyleHex(){
    
        switch(Calendar.Style.valueOf(style)){
            case STYLE1: return "#77C04B";
            case STYLE2: return "#418FCB";
            case STYLE3: return "#F7D15B";
            case STYLE4: return "#9D5B9F";
            case STYLE5: return "#D0525F";
            case STYLE6: return "#F9844B";
            case STYLE7: return "#AE663E";
            default: return "#77C04B";
        }
        
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

    public Boolean getRecurring(){
        return this.recurring;
    }

    public String getLectureDays(){
        return this.lectureDays;
    }



    
}
