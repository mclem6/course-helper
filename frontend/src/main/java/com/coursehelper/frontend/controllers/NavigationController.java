package com.coursehelper.frontend.controllers;

import com.coursehelper.frontend.UserSession;
import com.coursehelper.frontend.UserStore;
import com.coursehelper.frontend.util.AvatarUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class NavigationController {


    private MainLayoutController main;


    @FXML private Text welcomeText;
    @FXML private ImageView profileImageView;
    @FXML private Hyperlink homeLink;
    @FXML private Hyperlink documentsLink;
    @FXML private Hyperlink tasksLink;

    private final UserStore userStore = UserStore.getInstance();


    public void initialize(){
        setWelcomeMessage(UserSession.getUser().getUsername());

        //get user profile photo
        loadProfilePicture();

        userStore.profilePictureProperty().addListener((obs, oldBytes, newBytes) -> {
        profileImageView.setImage( 
            AvatarUtils.getProfileImage(newBytes, UserSession.getUser().getUsername())
        );
    });

    }

    public void setMain(MainLayoutController main){
        this.main = main;
    }

    public void setWelcomeMessage(String userName){
        welcomeText.setText("Welcome, " + userName);
    }

    @FXML
    public void goToHomePage(){
        
        //load user's homepage
        main.loadPage("/FXML/HomePage.fxml");
        
    }

    @FXML
    public void goToCalendarPage(){
        
        //load user's homepage
        main.loadPage("/FXML/calendarPage.fxml");
    }

    @FXML
    public void goToTasksPage(){
        
        //load user's homepage
        main.loadPage("/FXML/tasksPage.fxml");
    }

    @FXML
    public void goToDocumentsPage() {
        main.loadPage("/FXML/DocumentsPage.fxml");
    }

    @FXML
    public void goToSettingsPage() {
        main.loadPage("/FXML/SettingsPage.fxml");
    }

    public void setNavigationEnabled(boolean enabled) {
        homeLink.setDisable(!enabled);
        documentsLink.setDisable(!enabled);
        tasksLink.setDisable(!enabled);
    }

    private void loadProfilePicture() { 

        profileImageView.setImage(AvatarUtils.getProfileImage(userStore.getProfilePicture(),
        UserSession.getUser().getUsername()));
        
        AvatarUtils.makeCircular(profileImageView, 50);
    }


    
}
