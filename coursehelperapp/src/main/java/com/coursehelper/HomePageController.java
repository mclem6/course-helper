package com.coursehelper;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class HomePageController {

    @FXML
    Text welcomeText;

    public void welcomeText(String userName){
        welcomeText.setText("Welcome, " + userName);
    }

    
    

}