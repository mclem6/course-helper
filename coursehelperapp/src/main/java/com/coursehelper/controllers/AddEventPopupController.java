package com.coursehelper.controllers;

import com.coursehelper.App;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;

public class AddEventPopupController {


    @FXML
    StackPane formContainer;

    HomePageController homePageController;

    Popup popup;

    public void initialize(){

    }


     //get access to homepage controller 
    public void setHomePageController(HomePageController controller){
        this.homePageController = controller;
    }

     public void setPopup(Popup popup){
        this.popup = popup;
    }

    public void addNewAssignmentForm(){

         //load and show event form in custom context-menu form 
        try {
            
            // load the FXML
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/FXML/assignmentForm.fxml"));
            Parent formNode = loader.load();

            // get access to form's controller
            AddAssignmentFormController addAssignmentFormController = loader.getController();

            //pass homepage controller to form's controller
            addAssignmentFormController.setHomePageController(homePageController);

            //pass popup to event controller
            addAssignmentFormController.setPopup(popup);

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
            addEventFormController.setHomePageController(homePageController);

            //pass popup to event controller
            addEventFormController.setPopup(popup);

             //add form to popup
            formContainer.getChildren().setAll(formNode);

        

            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    
}
