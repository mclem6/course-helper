package com.coursehelper.controllers;

import com.coursehelper.App;
import com.coursehelper.SceneManager;

import javafx.fxml.FXML;

public class NavigationController {

    SceneManager sceneManager = new SceneManager(App.primaryStage);

    @FXML
    private void goToHomePage(){
        
        //load user's homepage
        sceneManager.switchScene("/FXML/HomePage.fxml");
    }
    
    
}
