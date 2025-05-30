package com.coursehelper.controllers;

import java.io.IOException;

import com.coursehelper.App;
import com.coursehelper.User;
import com.coursehelper.UserDAO;
import com.coursehelper.UserSession;

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

    User user;


    @FXML
    private void login() throws IOException {

        //authenticating user 
        //check if entry is not valid
        if(username.getText().equals("") || password.getText().equals("")){
            errorText.setText("*missing username or password");
        } 
        
        //check if user exists/credentials are correct
        else {

            //user does not exist or credentials not correct
            if(userDao.findUser(username.getText(), password.getText()) == userDao.USER_DOESNOTEXIST){
                errorText.setText("incorrect username or password");
            } 

            //user exists, log in and load user's homepage 
            else{
                
                //initialize User object
                user = userDao.getUserByCredentials(username.getText(), password.getText());
                //null check
                if(user != null){
                    UserSession.init(user.getId(), user.getName(), user.getUsername());
                    init_homePage(App.primaryStage);

                }
        
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

        //get homepage controller
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/FXML/HomePage.fxml"));
        Parent root = loader.load();

        //get controller to set welcome message
        HomePageController controller = loader.getController();
        String userName = user.getName();
        
        if(userName != null){
            controller.setWelcomeMessage(userName);
        }

        //set style sheet of homepage
        root.getStylesheets().add(getClass().getResource("/stylesheets/homepage.css").toExternalForm());
       
        //set scene
        Scene scene = new Scene(root, (bounds.getWidth() - 500), bounds.getHeight());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(true);


    }



}
