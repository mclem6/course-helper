package com.coursehelper.controllers;

import java.io.IOException;

import com.coursehelper.App;
import com.coursehelper.User;
import com.coursehelper.UserSession;
import com.coursehelper.dao.UserDAO;

import javafx.application.Platform;
import javafx.concurrent.Task;
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
    private void handleLogin() throws IOException {

         //authenticating user 

        //check if entries is empty
        if(username.getText().equals("") || password.getText().equals("")){
            errorText.setText("*missing username or password");
        } 
        
        //entries not empty, check if user exists/credentials are correct
        else {

            //create new thread for database access
            Task <Void> task = new Task<>(){
                
                @Override
                protected Void call() throws Exception {

                    //user does not exist or credentials not correct // update UI
                    if(userDao.authUser(username.getText(), password.getText()) == userDao.USER_DOES_NOT_EXIST){
                        Platform.runLater(() -> errorText.setText("incorrect username or password"));
                        
                    } 

                    //user exists, log in and load user's homepage 
                    else{

                        //initialize User object
                        user = userDao.getUserByCredentials(username.getText(), password.getText());

                        //null check
                        if(user != null){
                            UserSession.init(user);
                            //upload user's homepage 
                            Platform.runLater(() -> {
                                try{
                                    initHomePage(App.primaryStage);
                            } catch (IOException e){
                                e.printStackTrace();
                            }
                            
                        });
                
                        }
                    }

                    return null;
                }


            };

            //start thread
            new Thread(task).start();
        }

    


    }

  
    @FXML
    private void createAccount() throws IOException{
        //verify if all needed fields are inputted
        if(username.getText().equals("") || password.getText().equals("")){
            System.out.println("*missing field");
        } 
        
        //attempt to create account
        else {
            //create task for database access
            Task <Void> task = new Task<>(){
                
                @Override
                protected Void call() throws Exception {

                    //check if user name is taken 
                    if(userDao.registerUser(username.getText(), password.getText()) == userDao.USER_EXISTS){
                        //update UI

                        Platform.runLater(() -> errorText.setText("*username taken. Enter a different username"));
                    } 

                    //username is available, account created
                    else {
                        //load new user's homepage 
                        //initialize User object
                        user = userDao.getUserByCredentials(username.getText(), password.getText());

                        //null check
                        if(user != null){
                            UserSession.init(user);

                            //upload user's homepage 
                            Platform.runLater(() -> {
                                try{
                                    initHomePage(App.primaryStage);
                            } catch (IOException e){
                                e.printStackTrace();
                            }
                            
                        });
                
                        }
                        
                    }

                    return null;

                }
            };

            //start thread
            new Thread(task).start();
        }
    
    }

    //loads screen for homepage using homepage controller
    public void initHomePage(Stage stage) throws IOException{

        //get dimensions
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        //get homepage controller
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/FXML/homePage.fxml"));
        Parent root = loader.load();


        //set style sheet of homepage
        root.getStylesheets().add(getClass().getResource("/stylesheets/homepage.css").toExternalForm());
       
        //set scene
        Scene scene = new Scene(root, 1275, bounds.getHeight());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(true);


    }



}
