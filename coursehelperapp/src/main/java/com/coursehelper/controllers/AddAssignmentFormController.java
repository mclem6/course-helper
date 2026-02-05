package com.coursehelper.controllers;

import java.time.LocalDate;
import java.util.List;

import com.coursehelper.CalendarManager;
import com.coursehelper.UserSession;
import com.coursehelper.dao.AssignmentDAO;
import com.coursehelper.dao.CourseDAO;
import com.coursehelper.dao.EventDAO;
import com.coursehelper.model.Course;
import com.coursehelper.model.Event;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Popup;

public class AddAssignmentFormController {

    @FXML
    ComboBox<String> course_combo;

    @FXML
    TextField assignment_title;

    @FXML
    DatePicker due_date_picker;

    @FXML
    ComboBox<String> due_time_combo;

    @FXML
    ComboBox<String> assignment_type;

    List<Course> userCourses;

    HomePageController homePageController;
    Popup popup;
    CourseDAO courseDAO;
    UserSession userSession;
    AssignmentDAO assignmentDAO;
    EventDAO eventDAO;
    CalendarManager calendarManager;
   

    public void initialize(){
        //initialize DAOs
        courseDAO = CourseDAO.getInstance();
        userSession = UserSession.getInstance();
        assignmentDAO = AssignmentDAO.getInstance();
        eventDAO = EventDAO.getInstance();
        calendarManager = CalendarManager.getInstance();

        //add user's courses to course combobox
        userCourses = courseDAO.getCoursesByUser(userSession.getUserId());
        for (Course course : userCourses){
            course_combo.getItems().add(course.getCourseName());
        }

        //add assignmnent type
        assignment_type.getItems().addAll();
        
    
    }


    //get access to homepage controller 
    public void setHomePageController(HomePageController controller){
        this.homePageController = controller;

        //create time options and add to due_time
        ObservableList<String> timeOptions = this.homePageController.timeOptions();
        due_time_combo.setItems(timeOptions);


    }

    public void setPopup(Popup popup){
        this.popup = popup;
    }


    @FXML
    public void addAssignment(){

        //get user input
        int user_id = userSession.getUserId();
        int course_id = courseDAO.findCourseByName(course_combo.getValue(), user_id).getCourseId();
        String title = assignment_title.getText();
        String event_type = assignment_type.getValue();
        LocalDate due_date = due_date_picker.getValue();
        String due_time = due_time_combo.getValue();

        System.out.println(event_type);
        
        
        

        //spwn new thread for Database queries
        Task <Void> task = new Task<>(){

            @Override
            protected Void call() throws Exception{

                //add event to assignment table 
                int assignment_id = assignmentDAO.addAssignment(user_id, title, event_type, course_id, due_date, due_time);

                //add event to event table
                int event_id = eventDAO.addEvent(user_id, title, event_type, course_id, due_date, due_time, due_time, false, "", "Assignment", assignment_id );
                //add to calendar
                calendarManager.addEvent(courseDAO.findCourseByName(course_combo.getValue(), user_id), new Event(event_id, course_id, title, event_type, due_date, due_time, due_time, false, ""));

                return null;
            }

            
        };

        task.setOnSucceeded(e -> {


            //close popup
            popup.hide();

        });

        //start thread
        new Thread(task).start();

    }

    @FXML
    public void cancelAssignment(){
        popup.hide();
    }

  

    
}
