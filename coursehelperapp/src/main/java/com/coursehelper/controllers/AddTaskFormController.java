package com.coursehelper.controllers;

import java.util.List;

import com.coursehelper.CalendarManager;
import com.coursehelper.Course;
import com.coursehelper.UserSession;
import com.coursehelper.dao.AssignmentDAO;
import com.coursehelper.dao.CourseDAO;
import com.coursehelper.dao.EventDAO;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Popup;

public class AddTaskFormController {

    @FXML
    ComboBox<String> course_combo;

    @FXML
    TextField task_title;

    @FXML
    DatePicker due_date_picker;

    List<Course> userCourses;

    TaskListController taskListController;
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


    }


    //get access to TaskList controller 
    public void setTaskListController(TaskListController controller){
        this.taskListController = controller;
    }

    public void setPopup(Popup popup){
        this.popup = popup;
    }

    @FXML
    public void addTask(){

        

    }


    
}
