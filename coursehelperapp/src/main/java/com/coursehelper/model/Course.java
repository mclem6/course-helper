package com.coursehelper.model;

import com.calendarfx.model.Calendar;
import static com.calendarfx.model.Calendar.Style.STYLE1;
import static com.calendarfx.model.Calendar.Style.STYLE2;
import static com.calendarfx.model.Calendar.Style.STYLE3;
import static com.calendarfx.model.Calendar.Style.STYLE4;
import static com.calendarfx.model.Calendar.Style.STYLE5;
import static com.calendarfx.model.Calendar.Style.STYLE6;
import static com.calendarfx.model.Calendar.Style.STYLE7;

public class Course {

    private int course_id;
    private String course_name;
    private String course_style;
    // private String calendarName;


    //counstructor for creating course
    public Course(int id, String name, String course_style){
        this.course_id = id;
        this.course_name = name;
        this.course_style = course_style;
        // this.calendarName = name;
    }


    public String getCourseName(){
        return course_name;
    }

    public int getCourseId(){
        return course_id;
    }

    public String getCourseStyle(){
        return course_style;
    }

    public String getCourseStyleHex(){
    
        switch(Calendar.Style.valueOf(course_style)){
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



    
}
