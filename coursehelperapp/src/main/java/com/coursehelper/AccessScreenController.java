package com.coursehelper;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class AccessScreenController {

    @FXML
    Button createAccountButton;

    @FXML
    Button login;

    @FXML
    TextField username;

    @FXML
    TextField password;



    @FXML
    private void login() throws IOException {
        //authenticate user 
        

        //if user exists// correct info entered, load user's home page 

        //if not alert user of error
    }

  
    @FXML
    private void createAccount() throws IOException{
        //verify if all needed fields are inputted
        if(username.getText() == "" || password.getText() == ""){
            System.out.println("missing field");
        } else {
            DatabaseUtils.registerUser(username.getText(), password.getText());
        }


        //if verified create account

        //if not alert user of error
    }


}
