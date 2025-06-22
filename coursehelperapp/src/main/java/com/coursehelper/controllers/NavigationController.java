package com.coursehelper.controllers;

import com.coursehelper.App;
import com.coursehelper.SceneManager;
import com.coursehelper.UserSession;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class NavigationController {

    SceneManager sceneManager = new SceneManager(App.primaryStage);
    UserSession userSession = UserSession.getInstance();

    @FXML
    Text welcomeText;

    public void initialize(){
        setWelcomeMessage(userSession.getUserName());
    }

    public void setWelcomeMessage(String userName){
        welcomeText.setText("Welcome, " + userName);
    }

    @FXML
    private void goToHomePage(){
        
        //load user's homepage
        sceneManager.switchScene("/FXML/homePage.fxml", "/stylesheets/homepage.css");
    }

    @FXML
    private void goToCoursesPage(){
        
        //load user's homepage
        sceneManager.switchScene("/FXML/coursesPage.fxml", "/stylesheets/coursesPage.css");
    }


    
    
}
