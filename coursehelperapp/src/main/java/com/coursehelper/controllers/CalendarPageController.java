package com.coursehelper.controllers;

import java.util.List;

import com.calendarfx.view.CalendarView;
import com.coursehelper.CalendarManager;
import com.coursehelper.Course;
import com.coursehelper.UserSession;
import com.coursehelper.dao.CourseDAO;
import com.coursehelper.dao.EventDAO;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class CalendarPageController {

    @FXML
    VBox calendarContainer;

    CalendarManager calendarManager;

    UserSession userSession = UserSession.getInstance();
    
    CourseDAO courseDAO;

    EventDAO eventDAO;

    public void initialize(){
        //get DAO instances 
        courseDAO = CourseDAO.getInstance();
        eventDAO = EventDAO.getInstance();
        calendarManager = CalendarManager.getInstance();

        
        //get user's courses
        List<Course> user_courses = courseDAO.getCoursesByUser(userSession.getUserId());

        if(!user_courses.isEmpty()){
            //display calendars
            createCalendars(user_courses);
        }

        }





    private void createCalendars(List<Course> user_courses){

            //create calendar view
            //TODO: edit calendarView
            CalendarView calendarView = new CalendarView(); 
            // calendarView.setPrefHeight(400);

    
            //for each course
            for (Course course : user_courses){
                
                //add entries to calendar
                calendarManager.addCourseCalendar(course);
                
            }        

            //add to calendarView
            calendarView.getCalendarSources().addAll(calendarManager.getCalendarSource());
           

            //add to FXML
            calendarContainer.getChildren().add(calendarView);


    }
    
}
