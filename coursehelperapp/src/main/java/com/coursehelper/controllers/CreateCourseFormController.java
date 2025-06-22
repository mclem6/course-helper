package com.coursehelper.controllers;


import java.io.IOException;
import java.util.List;

import org.controlsfx.control.CheckComboBox;

import com.coursehelper.UserSession;
import com.coursehelper.dao.CourseDAO;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
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

    CourseDAO courseDAO;

    UserSession userSession = UserSession.getInstance();

    public void initialize(){
        //initialize course data access object
        courseDAO = CourseDAO.getInstance();
        
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
        int new_course_year = Integer.valueOf(year_combo.getValue());
        String new_course_start_time = start_time_combo.getValue();
        String new_course_end_time = end_time_combo.getValue();
        String new_course_days = select_days_checkComboBox.getTitle();


        //add course to database
        courseDAO.addCourse(new_course_name, new_course_semester, new_course_year, new_course_start_time, new_course_end_time, new_course_days, userSession.getUserId());



    }

    


    
}
