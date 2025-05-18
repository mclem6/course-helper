package com.coursehelper;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

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
    Text errorText;



    @FXML
    private void login() throws IOException {

        //authenticate user 
        if(username.getText().equals("") || password.getText().equals("")){
            errorText.setText("*missing username or password");
        } else {

            //check if user does not exist
            if(DatabaseUtils.findUser(username.getText(), password.getText()) == DatabaseUtils.USER_DOESNOTEXIST){
                errorText.setText("incorrect username or password");
            } else{
                //user exists, log in and load user's homepage 
                errorText.setText("user found, logging in");
            }
        }

    }

  
    @FXML
    private void createAccount() throws IOException{
        //verify if all needed fields are inputted
        if(username.getText().equals("") || password.getText().equals("")){
            System.out.println("*missing field");
        } else {
            //attempt to create account
            if(DatabaseUtils.registerUser(username.getText(), password.getText()) == DatabaseUtils.USER_EXISTS){
                errorText.setText("*username taken. Enter a different username");
            } else {
                //account created, load user's homepage 
                errorText.setText("account created");
            }
        }
    
    }



}
