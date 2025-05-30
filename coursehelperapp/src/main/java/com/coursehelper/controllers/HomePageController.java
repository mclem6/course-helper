package com.coursehelper.controllers;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class HomePageController {

    @FXML
    Text welcomeText;

    public void setWelcomeMessage(String userName){
        welcomeText.setText("Welcome, " + userName);
    }

    
    

}