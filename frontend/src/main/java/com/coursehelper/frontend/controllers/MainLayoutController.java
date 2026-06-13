package com.coursehelper.frontend.controllers;

import com.coursehelper.frontend.CalendarManager;
import com.coursehelper.frontend.UserSession;
import com.coursehelper.frontend.UserStore;
import com.coursehelper.frontend.service.AgentApiService;
import com.coursehelper.frontend.service.api.ApiClient;
import com.coursehelper.frontend.theme.ThemeManager;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class MainLayoutController {

    @FXML private StackPane root;
    @FXML private BorderPane borderPane;
    @FXML private StackPane contentArea;
    @FXML private StackPane themeToggle;
    @FXML private NavigationController navigationController;
    @FXML private SidebarController sidebarController;
    @FXML private Circle toggleThumb;

    // chat fields
    @FXML private VBox chatPanel;
    @FXML private Button chatToggleBtn;
    @FXML private ScrollPane chatScrollPane;
    @FXML private VBox messageContainer;
    @FXML private TextField chatInput;
    @FXML private Button sendButton;

    private boolean chatExpanded = false;

    private AgentApiService agentApiService;
    private CalendarManager calendarManager; 
    private TranslateTransition transition;

    UserStore userStore = UserStore.getInstance();


    @FXML
    public void initialize() {


        //set up theme toggle button
        transition = new TranslateTransition(Duration.millis(200), toggleThumb);
    
        if(ThemeManager.isDarkMode()){
            transition.setToX(12);
        } else{
            transition.setToX(-12);
        }

        transition.play();

        chatExpanded = true;
        chatToggleBtn.setVisible(false);
        chatToggleBtn.setManaged(false);

        chatInput.setOnAction(e -> sendMessage());

        //set maincontroller on children controllers
        if (navigationController != null) {
                navigationController.setMain(this);
            }
            
        if (sidebarController != null) {
            sidebarController.setMain(this);
        }



        //check if user has setttings configured
        if(userStore.getSettings() == null){
            
            navigationController.setNavigationEnabled(false);
            loadPage("/FXML/SettingsPage.fxml");

        } 
        else {

            navigationController.setNavigationEnabled(true);

            calendarManager = CalendarManager.getInstance();
            agentApiService = new AgentApiService(new ApiClient());

            //setup calendar
            calendarManager.initialize();

            loadPage("/FXML/HomePage.fxml");

            String pending = UserSession.getPendingGreeting();
            if (pending != null) {
                UserSession.setPendingGreeting(null);
                addMessage(pending, false);
            } else {
                sendGreeting();
            }

        }

    }


    public void loadPage(String fxml){
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent view = loader.load();
            ThemeManager.setTheme(view, ThemeManager.getCurrentTheme());

            Object controller = loader.getController();
            if (controller instanceof SettingsPageController settingsController) {
                settingsController.setMain(this); // 
            }

            contentArea.getChildren().setAll(view);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onToggleTheme(){
        
        String newTheme = !ThemeManager.isDarkMode() ? "DarkMode" : "LightMode";
        ThemeManager.setTheme(root, newTheme);

        if(ThemeManager.isDarkMode()){
            transition.setToX(12);
        } else{
            transition.setToX(-12);
        }

        transition.play();

        // Content pages carry their own stylesheets, so re-theme them as well.
        if (!contentArea.getChildren().isEmpty() && contentArea.getChildren().get(0) instanceof Parent pageRoot) {
            ThemeManager.setTheme(pageRoot, newTheme);
        }

        ThemeManager.saveThemePreference(newTheme);
        

    }


    private void sendGreeting() {
        new Thread(() -> {
            try {
                String response = agentApiService.chat(
                    "Greet the student by name, give them a summary of their day " +
                    "(today's classes, upcoming assignments, incomplete tasks), " +
                    "and end with something encouraging.");
                Platform.runLater(() -> addMessage(response, false));
            } catch (Exception e) {
                Platform.runLater(() -> addMessage("Hi! How can I help you today?", false));
            }
        }).start();
    }

    @FXML
    public void sendMessage() {
        String message = chatInput.getText().trim();
        if (message.isEmpty()) return;

        // show user message
        addMessage(message, true);
        chatInput.clear();

        // disable input while waiting for response
        chatInput.setDisable(true);
        sendButton.setDisable(true);

        // call agent on background thread — never block JavaFX thread
        new Thread(() -> {

            try {
                String response = agentApiService.chat(message);

                Platform.runLater(() -> {
                    addMessage(response, false);
                    chatInput.setDisable(false);
                    sendButton.setDisable(false);
                    chatInput.requestFocus();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    addMessage(e.getMessage(), false);
                    chatInput.setDisable(false);
                    sendButton.setDisable(false);
                });
            }
        }).start();
    }

    private void addMessage(String text, boolean isUser) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setMaxWidth(250);
        message.getStyleClass().add(isUser ? "user-bubble" : "agent-bubble");
    

        HBox messageBox = new HBox(message);
        
        messageBox.setAlignment(isUser ?
                                Pos.CENTER_RIGHT :
                                Pos.CENTER_LEFT);
        

        messageContainer.getChildren().add(messageBox);

        // auto scroll to bottom
        chatScrollPane.layout();
        chatScrollPane.setVvalue(1.0);
    }

    @FXML
    public void toggleChat(){

        if(!chatExpanded) {

            chatPanel.setVisible(true);
            chatPanel.setManaged(true);
            chatToggleBtn.setVisible(false);
            chatToggleBtn.setManaged(false);

            ScaleTransition scale = new ScaleTransition(Duration.millis(250), chatPanel);
            scale.setFromX(0);
            scale.setFromY(0);
            scale.setToX(1);
            scale.setToY(1);

            FadeTransition fade = new FadeTransition(Duration.millis(250), chatPanel);
            fade.setFromValue(0);
            fade.setToValue(1);

            new ParallelTransition(scale, fade).play();
            chatExpanded = true;

        } else{
            
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), chatPanel);
            scale.setToX(0);
            scale.setToY(0);

            FadeTransition fade = new FadeTransition(Duration.millis(200), chatPanel);
            fade.setToValue(0);

            ParallelTransition close = new ParallelTransition(scale, fade);
            close.setOnFinished(e -> {
                chatPanel.setVisible(false);
                chatPanel.setManaged(false);
                chatToggleBtn.setVisible(true);
                chatToggleBtn.setManaged(true);
            });
            close.play();
            chatExpanded = false;
        }
    
    }

    public void enableNavigation() {
        navigationController.setNavigationEnabled(true);
    }


    
}
