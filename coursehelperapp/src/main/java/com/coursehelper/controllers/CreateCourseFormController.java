package com.coursehelper.controllers;


import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.controlsfx.control.CheckComboBox;

import com.coursehelper.CalendarManager;
import com.coursehelper.UserSession;
import com.coursehelper.dao.CourseDAO;
import com.coursehelper.dao.EventDAO;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class CreateCourseFormController {


    @FXML
    CheckComboBox<String> select_days_checkComboBox;

    @FXML
    ComboBox<String> semester_combo;

    @FXML
    ComboBox<String> year_combo;

    @FXML
    ComboBox<String> start_time_combo;

    @FXML
    ComboBox<String> end_time_combo;

    @FXML
    TextField course_name;

    @FXML
    DatePicker date_picker;

    CourseDAO courseDAO;

    EventDAO eventDAO;

    UserSession userSession;

    CalendarManager calendarManager;

    public void initialize(){
        //initialize data usersession access object
        courseDAO = CourseDAO.getInstance();
        eventDAO = EventDAO.getInstance();
        userSession = UserSession.getInstance();


        
        //add select_days options
        select_days_checkComboBox.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

        //add placeholder
        select_days_checkComboBox.setTitle("Select Days");
        select_days_checkComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> {
            if (select_days_checkComboBox.getCheckModel().getCheckedItems().isEmpty()) {
                select_days_checkComboBox.setTitle("Select Days"); // Acts like a placeholder
            } else {

                //get selected options
                 List<String> selected = select_days_checkComboBox.getCheckModel().getCheckedItems();

                //add to title
                select_days_checkComboBox.setTitle(String.join(", ", selected));


            }
        });

        //add start and end time options
    
    }

     //add course handler
    @FXML
    private void addCourse() throws IOException{
        //get user input from form fields
        String new_course_name = course_name.getText();
        String new_course_semester = semester_combo.getValue();
        int new_course_year = Integer.parseInt(year_combo.getValue());
        String new_course_start_time = start_time_combo.getValue();
        String new_course_end_time = end_time_combo.getValue();
        String new_course_days = select_days_checkComboBox.getTitle();
        LocalDate new_course_start_date = date_picker.getValue();


        //add course to course database
        int course_id = courseDAO.addCourse(new_course_name, new_course_semester, new_course_year, new_course_start_date, new_course_start_time, new_course_end_time, new_course_days, userSession.getUserId());

        //add schedule to event database
        eventDAO.addEvent(userSession.getUserId(), new_course_name, EventDAO.EVENT_CLASS, course_id, new_course_start_date, new_course_start_time, new_course_end_time, new_course_days);


    }

    


    
}
