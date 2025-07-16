package com.coursehelper;


public class Course {

    private int course_id;
    private String course_name;
    // private String calendarName;


    //counstructor for creating course
    public Course(int id, String name){
        this.course_id = id;
        this.course_name = name;
        // this.calendarName = name;
    }


    public String getCourseName(){
        return course_name;
    }

    public int getCourseId(){
        return course_id;
    }


    
}
