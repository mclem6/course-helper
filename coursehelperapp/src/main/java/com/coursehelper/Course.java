package com.coursehelper;

import java.util.List;

import com.calendarfx.model.Calendar;


public class Course {

    private int course_id;
    private String course_name;
    private Calendar calendar;


    //counstructor for creating course
    public Course(int id, String name){
        course_id = id;
        course_name = name;
        this.calendar = new Calendar(name);
    }

    //load events for course 
    public void loadEvents(List<Event> events){
        for(Event e : events) {

            //get event details and add to entry

            //add entry to calendar
            
        }
        
    }

    public String getCourseName(){
        return course_name;
    }


    
}
