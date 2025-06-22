package com.coursehelper.controllers;

import java.util.List;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.CalendarView;
import com.coursehelper.Course;
import com.coursehelper.UserSession;
import com.coursehelper.dao.CourseDAO;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class HomePageController {

    @FXML
    HBox coursesContainer;

    @FXML
    VBox calendarContainer;

    CalendarSource calendarSource;

    UserSession userSession = UserSession.getInstance();
    
    CourseDAO courseDAO;
    

    public void initialize(){
        //initizialize course data access object
        CourseDAO.init();
        courseDAO = CourseDAO.getInstance();

        
        //get user's courses
        List<Course> user_courses = courseDAO.getCoursesByUser(userSession.getUserId());

        //check if user has courses
        //user does not have courses
        if(user_courses.isEmpty()){

            //update UI
            Text courseNode = new Text("No Courses");
            coursesContainer.getChildren().add(courseNode);

        } 
        //user has courses 
        else {

            //Update UI : add course(s) to UI
            for (Course course : user_courses){
            Text courseNode = new Text(course.getCourseName());
            coursesContainer.getChildren().add(courseNode);
            }

            //display calendars
            createCalendars(user_courses);

        }
        
        


    }

    private void createCalendars(List<Course> user_courses){

            //create calendar view
            CalendarView calendarView = new CalendarView(); 

            //create calendar source 
            calendarSource = new CalendarSource("My Calendars");
        
            
            //for each course
            for (Course course : user_courses){
                //create a calendar
                Calendar courseCal = new Calendar(course.getCourseName());

                //TODO: set style
                courseCal.setStyle(Style.STYLE1); //replace STYLE1

                //add to calendar source 
                calendarSource.getCalendars().addAll(courseCal);

                
            }        

            //add to calendarView
            calendarView.getCalendarSources().addAll(calendarSource);

            //add to FXML
            calendarContainer.getChildren().add(calendarView);


    }

    

    
    

}