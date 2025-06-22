package com.coursehelper.controllers;


import com.coursehelper.App;
import com.coursehelper.UserSession;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

public class CoursesPageController {

    UserSession userSession = UserSession.getInstance();

    @FXML
    ComboBox semesterCombo;

    @FXML
    VBox add_course_form_vbox;

    @FXML
    Button add_new_course_btn;

    CreateCourseFormController createCourseFormController;


    public void addNewCourse(){

        //load form fxml and show
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
