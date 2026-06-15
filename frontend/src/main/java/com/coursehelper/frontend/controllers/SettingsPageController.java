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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SettingsPageController {

    @FXML private ComboBox<String> semesterCombo;
    @FXML private ComboBox<Integer> yearCombo;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label errorLabel;
    @FXML private Button editSemesterBtn;
    @FXML private VBox semesterEditSection;
    @FXML private ImageView profileImageView;

    @FXML private Label usernameLabel;
    @FXML private Button changeUsernameBtn;
    @FXML private HBox usernameEditRow;
    @FXML private TextField newUsernameField;
    @FXML private Label usernameError;

    @FXML private Button changePasswordBtn;
    @FXML private VBox passwordEditSection;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label passwordError;


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

        usernameLabel.setText("Username: " + UserSession.getUser().getUsername());

        UserSettings settings = userStore.getSettings();
        if (settings != null) {
            semesterCombo.setValue(settings.getSemester());
            yearCombo.setValue(settings.getYear());
            startDatePicker.setValue(settings.getStartDate());
            endDatePicker.setValue(settings.getEndDate());
        } else {
            // not yet configured — show all fields enabled so user can fill them in
            semesterCombo.setDisable(false);
            yearCombo.setDisable(false);
            semesterEditSection.setVisible(true);
            semesterEditSection.setManaged(true);
            editSemesterBtn.setDisable(true);
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
       userStore.setAssignments(assignmentService.getAllAssignments("INCOMPLETE"));
       userStore.filterDataByCurrentCourses();





        
    }

    private Boolean validateSemesterInput(){
        String error = null;

        if (semesterCombo.getValue() == null) {
            error = "* select semester";
        } else if (yearCombo.getValue() == null) {
            error = "* select year";
        } else if (startDatePicker.getValue() == null) {
            error = "* select start date";
        } else if (endDatePicker.getValue() == null) {
            error = "* select end date";
        } else if (!startDatePicker.getValue().isBefore(endDatePicker.getValue())) {
            error = "Start date must be before end date";
        }

        if (error != null) {
            errorLabel.setText(error);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            return false;
        }

        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
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
    public void toggleUsernameEdit() {
        usernameEditRow.setVisible(true);
        usernameEditRow.setManaged(true);
        usernameError.setVisible(false);
        usernameError.setManaged(false);
        changeUsernameBtn.setDisable(true);
    }

    @FXML
    public void cancelUsernameEdit() {
        usernameEditRow.setVisible(false);
        usernameEditRow.setManaged(false);
        usernameError.setVisible(false);
        usernameError.setManaged(false);
        newUsernameField.clear();
        changeUsernameBtn.setDisable(false);
    }

    @FXML
    public void saveUsername() {
        String newUsername = newUsernameField.getText().trim();
        if (newUsername.isEmpty()) {
            showUsernameError("Username cannot be empty.");
            return;
        }
        new Thread(() -> {
            try {
                userService.changeUsername(newUsername);
                Platform.runLater(() -> {
                    UserSession.getUser().setUsername(newUsername);
                    usernameLabel.setText("Username: " + newUsername);
                    cancelUsernameEdit();
                });
            } catch (ApiException e) {
                Platform.runLater(() -> showUsernameError(e.getMessage()));
            }
        }).start();
    }

    @FXML
    public void togglePasswordEdit() {
        passwordEditSection.setVisible(true);
        passwordEditSection.setManaged(true);
        passwordError.setVisible(false);
        passwordError.setManaged(false);
        changePasswordBtn.setDisable(true);
    }

    @FXML
    public void cancelPasswordEdit() {
        passwordEditSection.setVisible(false);
        passwordEditSection.setManaged(false);
        passwordError.setVisible(false);
        passwordError.setManaged(false);
        clearPasswordFields();
        changePasswordBtn.setDisable(false);
    }

    @FXML
    public void savePassword() {
        String current = currentPasswordField.getText();
        String newPw = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();
        if (current.isEmpty() || newPw.isEmpty() || confirm.isEmpty()) {
            showPasswordError("All fields are required.");
            return;
        }
        if (!newPw.equals(confirm)) {
            showPasswordError("New passwords do not match.");
            return;
        }
        if (newPw.length() < 6) {
            showPasswordError("Password must be at least 6 characters.");
            return;
        }
        new Thread(() -> {
            try {
                userService.changePassword(current, newPw);
                Platform.runLater(this::cancelPasswordEdit);
            } catch (ApiException e) {
                Platform.runLater(() -> showPasswordError(e.getMessage()));
            }
        }).start();
    }

    private void showUsernameError(String msg) {
        usernameError.setText(msg);
        usernameError.setVisible(true);
        usernameError.setManaged(true);
    }

    private void showPasswordError(String msg) {
        passwordError.setText(msg);
        passwordError.setVisible(true);
        passwordError.setManaged(true);
    }

    private void clearPasswordFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    @FXML
    public void toggleSemesterEdit() {
        semesterCombo.setDisable(false);
        yearCombo.setDisable(false);
        semesterEditSection.setVisible(true);
        semesterEditSection.setManaged(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        editSemesterBtn.setDisable(true);
    }

    @FXML
    public void saveSemester() {
        if (!validateSemesterInput()) return;

        String semester = semesterCombo.getValue();
        int year = yearCombo.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        new Thread(() -> {
            try {
                saveSemesterSettings(semester, year, startDate, endDate);
                Platform.runLater(() -> {
                    collapseSemesterEdit();
                    CalendarManager.getInstance().initialize();
                    mainLayoutController.enableNavigation();
                    mainLayoutController.loadPage("/FXML/HomePage.fxml");
                });
            } catch (ApiException e) {
                Platform.runLater(() -> {
                    errorLabel.setText(e.getMessage());
                    errorLabel.setVisible(true);
                    errorLabel.setManaged(true);
                });
            }
        }).start();
    }

    @FXML
    public void cancelSemesterEdit() {
        UserSettings settings = userStore.getSettings();
        if (settings != null) {
            semesterCombo.setValue(settings.getSemester());
            yearCombo.setValue(settings.getYear());
            startDatePicker.setValue(settings.getStartDate());
            endDatePicker.setValue(settings.getEndDate());
        }
        collapseSemesterEdit();
    }

    private void collapseSemesterEdit() {
        semesterCombo.setDisable(true);
        yearCombo.setDisable(true);
        semesterEditSection.setVisible(false);
        semesterEditSection.setManaged(false);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        editSemesterBtn.setDisable(false);
    }

    private void loadProfilePicture() { 

        profileImageView.setImage(AvatarUtils.getProfileImage(userStore.getProfilePicture(),
        
        UserSession.getUser().getUsername()));
        
        AvatarUtils.makeCircular(profileImageView, 40);
    }




    
}
