package com.coursehelper.frontend.controllers;

import java.io.File;
import java.time.LocalDate;

import com.coursehelper.frontend.CalendarManager;
import com.coursehelper.frontend.UserSession;
import com.coursehelper.frontend.UserStore;
import com.coursehelper.frontend.dto.SettingsRequestDto;
import com.coursehelper.frontend.dto.SettingsResponseDto;
import com.coursehelper.frontend.exceptions.ApiException;
import com.coursehelper.frontend.model.UserSettings;
import com.coursehelper.frontend.service.AssignmentService;
import com.coursehelper.frontend.service.CourseService;
import com.coursehelper.frontend.service.EventService;
import com.coursehelper.frontend.service.SettingsService;
import com.coursehelper.frontend.service.UserService;
import com.coursehelper.frontend.util.AvatarUtils;
import com.coursehelper.frontend.util.TimeUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SettingsPageController {

    @FXML private ComboBox<String> semesterCombo;
    @FXML private ComboBox<Integer> yearCombo;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label errorLabel;
    @FXML private Button editSemesterBtn;
    @FXML private ImageView profileImageView;


    private MainLayoutController mainLayoutController;
    private final UserStore userStore = UserStore.getInstance();
    private final SettingsService  settingsService = SettingsService.getInstance();
    private final CourseService courseService = CourseService.getInstance();
    private final EventService eventService = EventService.getInstance();
    private final AssignmentService assignmentService = AssignmentService.getInstance();
    private final UserService userService = UserService.getInstance();

    public void initialize(){

        yearCombo.setItems(TimeUtils.yearOptions());

        loadProfilePicture();

        //set initial values if they exist
        UserSettings settings = userStore.getSettings();
        if (settings != null){
            semesterCombo.setValue(settings.getSemester());
            yearCombo.setValue(settings.getYear());
            startDatePicker.setValue(settings.getStartDate());
            endDatePicker.setValue(settings.getEndDate());
        }

        
    }


    public void setMain(MainLayoutController mainLayoutController){
        this.mainLayoutController = mainLayoutController;
    }

    private void saveSemesterSettings(String semester, int year, LocalDate startDate, LocalDate endDate){


        //send to backend
        SettingsRequestDto request = new SettingsRequestDto(semester, year, startDate, endDate);
        SettingsResponseDto response = settingsService.saveSemesterSettings(request);

        //create setting
        UserSettings userSettings = new UserSettings(response.getSemester(), response.getYear(), 
                                        response.getStartDate(), response.getEndDate());

        //save to userStore
        userStore.setSettings(userSettings);
        userStore.setCourses(courseService.getUserSemesterCourses(semester, year));

        // fetch all events then filter to current courses
       userStore.setEvents(eventService.getAllEvents());
       userStore.setAssignments(assignmentService.getAllAssignments());
       userStore.filterDataByCurrentCourses();





        
    }

    private Boolean validateSemesterInput(){
        //check for errors
        if(semesterCombo.getValue() == null){
            errorLabel.setText("* select semester");
            return false;
        } 
        if(yearCombo.getValue() == null){
            errorLabel.setText("* select year");
            return false;
        } 

        if(startDatePicker.getValue() == null){
            errorLabel.setText("* select start date");
            return false;
        } 
        
        if(endDatePicker.getValue() == null){
            errorLabel.setText("* select end date");
            return false;
        } 

         // validate start is before end
        if (!startDatePicker.getValue().isBefore(endDatePicker.getValue())) {
            errorLabel.setText("Start date must be before end date");
            return false;
        }

        errorLabel.setText("");
        return true;
    }

    @FXML
    public void uploadProfilePhoto() {
        // open file chooser on JavaFX thread
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) profileImageView.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file == null) return; // user cancelled

        // upload on background thread
        new Thread(() -> {
            try {
                userService.uploadProfilePicture(file);

                // fetch updated picture
                byte[] updatedBytes = userService.getProfilePicture();


                Platform.runLater(() -> {
                    userStore.setProfilePicture(updatedBytes);
                    profileImageView.setImage(
                        AvatarUtils.getProfileImage(updatedBytes,
                            UserSession.getUser().getUsername())
                    );
                });

            } catch (ApiException e) {
                Platform.runLater(() -> errorLabel.setText(e.getMessage()));
            }
        }).start();
    }

    @FXML
    public void deleteProfilePhoto() {}

    @FXML
    public void editSaveSemester(){

        if (editSemesterBtn.getText().equals("Edit Semester Setting")){

            editSemesterBtn.setText("Save Semester Setting");
            semesterCombo.setDisable(false);
            yearCombo.setDisable(false);
            startDatePicker.setDisable(false);
            endDatePicker.setDisable(false);


        } else {

            if(!validateSemesterInput()){
                return;
            }

            //get user input
            String semester = semesterCombo.getValue();
            int year = yearCombo.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();


            new Thread(() -> {
                try {

                    saveSemesterSettings(semester, year, startDate, endDate);

                    Platform.runLater(() -> {
            
                        editSemesterBtn.setText("Edit Semester Setting");
                        semesterCombo.setDisable(true);
                        yearCombo.setDisable(true);
                        startDatePicker.setDisable(true);
                        endDatePicker.setDisable(true);

                        // re-enable navigation
                        // need reference to MainLayoutController
                        CalendarManager.getInstance().initialize();
                        mainLayoutController.enableNavigation();
                        mainLayoutController.loadPage("/FXML/HomePage.fxml");
                    });
                } catch (ApiException e) {
                    Platform.runLater(() -> 
                        errorLabel.setText(e.getMessage())); // ← show to user
                }
            }).start();
        }

    }

    private void loadProfilePicture() { 

        profileImageView.setImage(AvatarUtils.getProfileImage(userStore.getProfilePicture(),
        
        UserSession.getUser().getUsername()));
        
        AvatarUtils.makeCircular(profileImageView, 40);
    }




    
}
