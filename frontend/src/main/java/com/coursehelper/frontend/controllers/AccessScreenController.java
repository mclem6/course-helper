package com.coursehelper.frontend.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.coursehelper.frontend.App;
import com.coursehelper.frontend.UserSession;
import com.coursehelper.frontend.UserStore;
import com.coursehelper.frontend.dto.LoginResponseDto;
import com.coursehelper.frontend.exceptions.LoginException;
import com.coursehelper.frontend.exceptions.RegisterException;
import com.coursehelper.frontend.model.Assignment;
import com.coursehelper.frontend.model.Course;
import com.coursehelper.frontend.model.Event;
import com.coursehelper.frontend.model.Task;
import com.coursehelper.frontend.model.User;
import com.coursehelper.frontend.model.UserSettings;
import com.coursehelper.frontend.service.AgentApiService;
import com.coursehelper.frontend.service.AssignmentService;
import com.coursehelper.frontend.service.CourseService;
import com.coursehelper.frontend.service.EventService;
import com.coursehelper.frontend.service.SettingsService;
import com.coursehelper.frontend.service.TaskService;
import com.coursehelper.frontend.service.UserService;
import com.coursehelper.frontend.service.api.ApiClient;
import com.coursehelper.frontend.theme.ThemeManager;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.util.Duration;

public class AccessScreenController {
     
    @FXML private HBox root;
    @FXML private ToggleGroup authGroup;
    @FXML private ToggleButton tab_login;
    @FXML private TextField usernameText;
    @FXML private PasswordField passwordText;
    @FXML private Label errorLabel;
    @FXML private Button logInButton;
    @FXML private Button createAccountButton;
    @FXML private Circle toggleThumb;
    @FXML private VBox loadingBox;

    private final ApiClient apiClient = new ApiClient();
    private final UserService userService =  new UserService(apiClient);
    private final CourseService courseService = new CourseService(apiClient);
    private final EventService eventService = new EventService(apiClient);
    private final AssignmentService assignmentService = new AssignmentService(apiClient);
    private final TaskService taskService = new TaskService(apiClient);
    private final SettingsService settingsService = new SettingsService(apiClient);

    private final UserStore userStore = UserStore.getInstance();
    
    private TranslateTransition transition;


    public void initialize(){
        //set up theme toggle button
        transition = new TranslateTransition(Duration.millis(200), toggleThumb);
        
        if(ThemeManager.isDarkMode()){
            transition.setToX(12);
        } else{
            transition.setToX(-12);
        }

        transition.play();

        //set up tab-toggle
        //start on Login
        tab_login.setSelected(true);
        

        //set up listener
        authGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null){
                ToggleButton selected = (ToggleButton) newToggle;
                if(selected.getText().equals("Login")){
                    
                    //show only login button
                    createAccountButton.setVisible(false);
                    createAccountButton.setManaged(false);
                    logInButton.setVisible(true);
                    logInButton.setManaged(true);

                    errorLabel.setText(""); 
                    usernameText.setText(""); 
                    passwordText.setText("");
              
                    
                } else if (selected.getText().equals("Sign Up")){
                    
                    //show only create account
                    logInButton.setVisible(false);
                    logInButton.setManaged(false);
                    createAccountButton.setVisible(true);
                    createAccountButton.setManaged(true);

                    errorLabel.setText(""); 
                    usernameText.setText(""); 
                    passwordText.setText("");

                }

            }
        });

        


    }

    @FXML
    public void onToggleTheme(){

        String newTheme = !ThemeManager.isDarkMode() ? "DarkMode" : "LightMode";

        if(ThemeManager.isDarkMode()){
            transition.setToX(12);
        } else{
            transition.setToX(-12);
        }

        transition.play();
        
        ThemeManager.setAccessScreenTheme(root, newTheme);
        ThemeManager.saveLocalThemePreference(newTheme);
       

    }



    @FXML
    public void handleLogin() {

        //check if entries is empty
        if(usernameText.getText().equals("") || passwordText.getText().equals("")){
            errorLabel.setText("*missing username or password");
            return;
        }
    
        try{

            LoginResponseDto response = userService.loginUser(usernameText.getText(), passwordText.getText());
            User user = new User(response.getId(),response.getUsername(), response.getTheme());
            UserSession.setSession(response.getAccessToken(), user);
            showLoading();
            initMain();

        } catch(LoginException | RegisterException e){
            errorLabel.setText(e.getMessage());
        }
           

    }

  
    @FXML
    public void createAccount() {

         //check if entries is empty
        if(usernameText.getText().equals("") || passwordText.getText().equals("")){
            errorLabel.setText("*missing username or password");
            return;
        }  
        
        try{
            LoginResponseDto response = userService.registerUser(usernameText.getText(), passwordText.getText());
            User user = new User(response.getId(),response.getUsername(), response.getTheme());
            UserSession.setSession(response.getAccessToken(), user);
            showLoading();
            initMain();

        } catch(LoginException | RegisterException e){
            errorLabel.setText(e.getMessage());
        }
    }
        
    

    private void showLoading() {
        logInButton.setVisible(false);
        logInButton.setManaged(false);
        createAccountButton.setVisible(false);
        createAccountButton.setManaged(false);
        usernameText.setDisable(true);
        passwordText.setDisable(true);
        loadingBox.setVisible(true);
        loadingBox.setManaged(true);
    }

    private void resetForm() {
        usernameText.setDisable(false);
        passwordText.setDisable(false);
        loadingBox.setVisible(false);
        loadingBox.setManaged(false);
        logInButton.setVisible(true);
        logInButton.setManaged(true);
    }

    //loads screen for homepage using homepage controller
    public void initMain(){

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        new Thread(() -> {
            try {
                ThemeManager.initialize(UserSession.getUser().getId());

                UserSettings settings = settingsService.getSettings();
                userStore.setSettings(settings);

                if (settings != null) {

                    Thread dataThread = new Thread(() -> {
                        try {
                            List<Course> courses = courseService.getUserSemesterCourses(settings.getSemester(), settings.getYear());
                            Map<Long, List<Event>> events = eventService.getAllEvents();
                            Map<Long, List<Assignment>> assignments = assignmentService.getAllAssignments("INCOMPLETE");
                            Map<Long, List<Task>> tasks = taskService.getAllTasks();
                            byte[] profilePicture = userService.getProfilePicture();

                            userStore.setCourses(courses);
                            userStore.setEvents(events);
                            userStore.setAssignments(assignments);
                            userStore.setTasks(tasks);
                            userStore.setProfilePicture(profilePicture);
                            userStore.filterDataByCurrentCourses();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                    Thread greetingThread = new Thread(() -> {
                        try {
                            String greeting = new AgentApiService(new ApiClient()).chat(
                                "Greet the student by name. Report only on incomplete assignments and tasks — never completed ones.\n" +
                                "Only include a section if there are actual items to list:\n" +
                                "**Overdue:** Incomplete assignments or tasks already past due (only if any exist).\n" +
                                "**Due Soon:** Upcoming incomplete assignments or tasks (only if any exist). If the total across both sections exceeds 5 items, limit to items due within the next 3 days.\n" +
                                "If there is nothing overdue and nothing due soon, skip both sections and just say there is nothing on their schedule. End with one short encouraging sentence.");
                            UserSession.setPendingGreeting(greeting);
                        } catch (Exception e) {
                            UserSession.setPendingGreeting("Hi! How can I help you today?");
                        }
                    });

                    dataThread.start();
                    greetingThread.start();
                    dataThread.join();
                    greetingThread.join();
                } else {
                    UserSession.setPendingGreeting(
                        "Welcome to CourseHelper, " + UserSession.getUser().getUsername() +
                        "! Fill out your current semester to get started.");
                }

                Platform.runLater(() -> loadMainApp(bounds));

            } catch (Exception e) {
                Platform.runLater(() -> {
                    errorLabel.setText("Unable to load. Check connection.");
                    resetForm();
                });
                e.printStackTrace();
            }
        }).start();

    }

    private void loadMainApp(Rectangle2D bounds){

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/FXML/mainLayout.fxml"));

        try {

            Parent root2 = loader.load();
            ThemeManager.setTheme(root2, ThemeManager.getCurrentTheme());
            Scene scene = new Scene(root2);
            App.primaryStage.setWidth(1275);
            App.primaryStage.setHeight(bounds.getHeight());
            App.primaryStage.setScene(scene);
            App.primaryStage.centerOnScreen();
            App.primaryStage.setResizable(true);

        } catch (IOException e) {
            errorLabel.setText("Unable to open main page.");
            e.printStackTrace();
        }

    }





}
