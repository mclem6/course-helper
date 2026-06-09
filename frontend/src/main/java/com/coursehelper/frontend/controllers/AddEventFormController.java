package com.coursehelper.frontend.controllers;

import java.util.List;

import org.controlsfx.control.CheckComboBox;

import com.coursehelper.frontend.CalendarManager;
import com.coursehelper.frontend.UserSession;
import com.coursehelper.frontend.model.Course;
import com.coursehelper.frontend.util.TimeUtils;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;

public class AddEventFormController {

    @FXML private TextField event_title;
    @FXML private ComboBox<String> course_combo;
    @FXML private ComboBox<String> sourceType;
    @FXML private DatePicker date_picker;
    @FXML private ComboBox<String> start_time_combo;
    @FXML private ComboBox<String> end_time_combo;
    @FXML private CheckBox recurring_checkbox;
    @FXML private ComboBox<String> recurrence_combo;
    @FXML private HBox recurrence_hbox;
    @FXML private CheckComboBox<String> repeat_days_checkComboBox;

    private List<Course> userCourses;
    private HomePageController homePageController;
    private Popup popup;
    private UserSession userSession;
    private CalendarManager calendarManager;
    

    public void initialize(){
        // //initialize DAOs
        // courseDAO = CourseDAO.getInstance();
        // eventDAO = EventDAO.getInstance();
        // userSession = UserSession.getInstance();
        // calendarManager = CalendarManager.getInstance();

        // //add user's courses to course combobox
        // userCourses = courseDAO.getCoursesByUser(userSession.getUserId());
        // for (Course course : userCourses){
        //     course_combo.getItems().add(course.getCourseName());
        // }

        // //add recurrence 
        // recurrence_combo.getItems().addAll("Daily", "Weekly", "Monthly", "Custom");

        // //add repeat_days options
        // repeat_days_checkComboBox.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

        //create time options then add to start and end combos
        start_time_combo.setItems(TimeUtils.timeOptions());
        end_time_combo.setItems(TimeUtils.timeOptions());
    }

     //get access to homepage controller 
    public void setHomePageController(HomePageController controller){
        this.homePageController = controller;
    }

     public void setPopup(Popup popup){
        this.popup = popup;
    }

    @FXML
    public void addEvent(){

        // //add event to events table 
        // int user_id = userSession.getUserId();
        // String title = event_title.getText();
        // String event_type = EventDAO.EVENT_CUSTOM;
        // int course_id = courseDAO.findCourseByName(course_combo.getValue(), user_id).getCourseId();
        // LocalDate start_date = date_picker.getValue();
        // String start_time = start_time_combo.getValue();
        // String end_time = end_time_combo.getValue();
        // Boolean recurring = recurring_checkbox.isSelected();
        // String recurrence_days = repeat_days_checkComboBox.getTitle();
        // String source_type = sourceType.getValue(); 
        // int source_id = -1; 

        
        // //add event to database and retrieve id
        // int event_id = eventDAO.addEvent(user_id, title, event_type, course_id, start_date, start_time, end_time, recurring, recurrence_days, source_type, source_id);

        // //add to calendar
        // calendarManager.addEvent(courseDAO.findCourseByName(course_combo.getValue(), user_id), new Event(event_id, course_id, title, event_type, start_date, start_time, end_time, recurring, recurrence_days));
        
        // //close popup
        // popup.hide();
     

    }


    @FXML
    public void cancelEvent(){
        popup.hide();
    }

    @FXML
    public void handleRecurringToggle(){
        if(recurring_checkbox.isSelected()){
            recurrence_combo.setVisible(true);
            handleRecurrenceSelection();
        } else {
            recurrence_combo.setVisible(false);
            recurrence_hbox.setVisible(false);
        }
        
        
    }

    @FXML
    public void handleRecurrenceSelection(){
        String selected = recurrence_combo.getValue();

        if("Custom".equals(selected)){
            recurrence_hbox.setVisible(true);
        } else {
            recurrence_hbox.setVisible(false);
        }
    }

    







    
}
