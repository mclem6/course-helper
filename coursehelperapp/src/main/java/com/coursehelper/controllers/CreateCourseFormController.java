package com.coursehelper.controllers;

import java.util.List;

import org.controlsfx.control.CheckComboBox;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;

public class CreateCourseFormController {

    @FXML
    CheckComboBox<String> select_days_checkComboBox;

    public void initialize(){

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

    


    
}
