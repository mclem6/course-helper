package com.coursehelper.controllers;

import java.io.IOException;

import com.coursehelper.App;
import com.coursehelper.UserDAO;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

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

    UserDAO userDao = new UserDAO();



    @FXML
    private void login() throws IOException {

        //authenticate user 
        if(username.getText().equals("") || password.getText().equals("")){
            errorText.setText("*missing username or password");
        } else {

            //check if user does not exist
            if(userDao.findUser(username.getText(), password.getText()) == userDao.USER_DOESNOTEXIST){
                errorText.setText("incorrect username or password");
            } else{
                //user exists, log in and load user's homepage 
                errorText.setText("user found, logging in");
                init_homePage(App.primaryStage);
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
            if(userDao.registerUser(username.getText(), password.getText()) == userDao.USER_EXISTS){
                errorText.setText("*username taken. Enter a different username");
            } else {
                //account created, load user's homepage 
                errorText.setText("account created");
            }
        }
    
    }

    //loads screen for homepage using homepage controller
    public void init_homePage(Stage stage) throws IOException{

        //get dimensions
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        
        //center screen
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());

        //get homepage controller
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/FXML/HomePage.fxml"));
        Parent root = loader.load();

        //get controller to set welcome message
        HomePageController controller = loader.getController();
        controller.welcomeText(userDao.get_userName(username.getText(), password.getText()));

        //set style sheet of homepage
        root.getStylesheets().add(getClass().getResource("/stylesheets/homepage.css").toExternalForm());
        //get homepage loader
        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
        stage.setScene(scene);

       
       
    }



}
