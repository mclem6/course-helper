package com.coursehelper.frontend.controllers;

import java.util.function.Consumer;

import com.coursehelper.frontend.App;
import com.coursehelper.frontend.model.Assignment;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;

public class AddEventPopupController {

    @FXML StackPane formContainer;

    private Popup popup;
    private Consumer<Assignment> onAssignmentCreated;

    public void initialize(){}

    public void setPopup(Popup popup){
        this.popup = popup;
    }

    public void setOnAssignmentCreated(Consumer<Assignment> onAssignmentCreated) {
        this.onAssignmentCreated = onAssignmentCreated;
    }

    public void addNewAssignmentForm(){

        //load and show assignment form in custom context-menu form 
        try {
            
            // load the FXML
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/FXML/assignmentForm.fxml"));
            Parent formNode = loader.load();

            // get access to assignmentform's controller
            AddAssignmentFormController addAssignmentFormController = loader.getController();
            
            //pass assignment controller
            addAssignmentFormController.setPopup(popup);
            addAssignmentFormController.setOnAssignmentCreated(onAssignmentCreated);

            //add form to popup
            formContainer.getChildren().setAll(formNode);

            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public void addNewEventForm(){

         //load and show event form in custom context-menu form 
        try {
            
            // load the FXML
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/FXML/eventForm.fxml"));
            Parent formNode = loader.load();

            // get access to form's controller
            AddEventFormController addEventFormController = loader.getController();

            //pass popup to event controller
            addEventFormController.setPopup(popup);

             //add form to popup
            formContainer.getChildren().setAll(formNode);

        

            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    
}
