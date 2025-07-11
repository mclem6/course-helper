package com.coursehelper.controllers;

import java.util.List;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.view.DetailedDayView;
import com.coursehelper.App;
import com.coursehelper.CalendarManager;
import com.coursehelper.Course;
import com.coursehelper.UserSession;
import com.coursehelper.dao.CourseDAO;
import com.coursehelper.dao.EventDAO;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class HomePageController {

    @FXML
    HBox coursesContainer;

    @FXML
    VBox calendarContainer;

    CalendarManager calendarManager;

    UserSession userSession = UserSession.getInstance();
    
    @FXML
    VBox add_course_form_vbox;

    CreateCourseFormController createCourseFormController;
    
    CourseDAO courseDAO;

    EventDAO eventDAO;
    

    public void initialize(){
        //initizialize course data access object
        CourseDAO.init();
        EventDAO.init();

        //get DAO instances 
        courseDAO = CourseDAO.getInstance();
        eventDAO = EventDAO.getInstance();
        calendarManager = new CalendarManager();

        
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
            DetailedDayView calendarView = new DetailedDayView(); 
            calendarView.setPrefHeight(400);
            calendarView.setVisibleHours(4);
        
            
            //for each course
            for (Course course : user_courses){
                //create a calendar
                Calendar<String> courseCal = new Calendar<>(course.getCourseName());

                //add entries to calendar
                calendarManager.addEntry(courseCal, course);


                //TODO: set color, user select color for course in course form
                courseCal.setStyle(Style.STYLE1); //replace

                //add to calendar source 
                calendarManager.getCalendarSource().getCalendars().addAll(courseCal);

                
            }        

            //add to calendarView
            calendarView.getCalendarSources().addAll(calendarManager.getCalendarSource());

            //TODO: edit calendarView
           

            //add to FXML
            calendarContainer.getChildren().add(calendarView);


    }

    public void addNewCourse(){

        //load and show couse form
        //TODO: should be a pop-up window 
        try {

            FXMLLoader loader = new FXMLLoader(App.class.getResource("/FXML/courseForm.fxml"));
            Parent formNode = loader.load();

            //get access to form's controller
            createCourseFormController = loader.getController();

            //add form to course page 
            add_course_form_vbox.getChildren().add(formNode);

            
        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }



    

    
    

}