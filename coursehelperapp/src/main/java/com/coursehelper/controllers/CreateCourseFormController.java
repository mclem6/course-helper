package com.coursehelper.controllers;


import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

import org.controlsfx.control.CheckComboBox;

import com.calendarfx.model.Calendar;
import com.coursehelper.CalendarManager;
import com.coursehelper.Course;
import com.coursehelper.UserSession;
import com.coursehelper.dao.CourseDAO;
import com.coursehelper.dao.EventDAO;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class CreateCourseFormController {

    @FXML
    Button add_course_button;

    @FXML
    Button cancel_course_button;
    

    @FXML
    CheckComboBox<String> select_days_checkComboBox;

    @FXML
    ComboBox<String> semester_combo;

    @FXML
    ComboBox<String> year_combo;

    @FXML
    ComboBox<String> start_time_combo;

    @FXML
    ComboBox<String> end_time_combo;

    @FXML
    ComboBox<Calendar.Style> style_comboBox;

    @FXML
    TextField course_name;

    @FXML
    DatePicker date_picker;

    CourseDAO courseDAO;

    EventDAO eventDAO;

    UserSession userSession;

    CalendarManager calendarManager;

    HomePageController homePageController;

    private Consumer<Course> onCourseCreated;

    public void initialize(){
        //initialize data usersession access object
        courseDAO = CourseDAO.getInstance();
        eventDAO = EventDAO.getInstance();
        userSession = UserSession.getInstance();
    
        
        //add select_days options
        select_days_checkComboBox.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

        //add placeholder
        select_days_checkComboBox.setTitle("");
        
        select_days_checkComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> {
            if (select_days_checkComboBox.getCheckModel().getCheckedItems().isEmpty()) {
                select_days_checkComboBox.setTitle("Select Days"); // Acts like a placeholder
            } else {

                //get selected options
                 List<String> selected = select_days_checkComboBox.getCheckModel().getCheckedItems();

                //add to title
                select_days_checkComboBox.setTitle(String.join(", ", selected));


            }
        });

        // add course color selection
        style_comboBox.getItems().addAll(Calendar.Style.values());

        //define colors in dropdown list 
        style_comboBox.setCellFactory(lv -> new ListCell<>() {
            private final Rectangle rect = new Rectangle(15, 15);

            @Override
            protected void updateItem(Calendar.Style style, boolean empty){
                super.updateItem(style, empty);
                if(empty || style == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(style.name());
                    rect.setFill(getColorForStyle(style));
                    setGraphic(rect);
                }
            }
            
        });

        //show selected color in cell
        style_comboBox.setButtonCell(new ListCell<>() {
            private final Rectangle rect = new Rectangle( 15, 15);

            @Override
            protected void updateItem(Calendar.Style style, boolean empty){
                super.updateItem(style, empty);
                if(empty || style == null){
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(style.name());
                    rect.setFill(getColorForStyle(style));
                    setGraphic(rect);
                }
            }
        });


        //TODO:add start and end time options
    
    
    }

     //add course handler
    @FXML
    private void addCourse() throws IOException{
        //get user input from form fields
        String new_course_name = course_name.getText();
        String new_course_semester = semester_combo.getValue();
        int new_course_year = Integer.parseInt(year_combo.getValue());
        String new_course_start_time = start_time_combo.getValue();
        String new_course_end_time = end_time_combo.getValue();
        String new_course_days = select_days_checkComboBox.getTitle();
        LocalDate new_course_start_date = date_picker.getValue();
        String style = style_comboBox.getValue().name();


        //add course to course database
        int course_id = courseDAO.addCourse(new_course_name, new_course_semester, new_course_year, new_course_start_date, new_course_start_time, new_course_end_time, new_course_days, style, userSession.getUserId());
        Course course = new Course(course_id, new_course_name, style);

        //add schedule to event database
        eventDAO.addEvent(userSession.getUserId(), new_course_name, EventDAO.EVENT_CLASS, course_id, new_course_start_date, new_course_start_time, new_course_end_time, new_course_days);

        //TODO: remove // close form 
        Stage stage = (Stage) add_course_button.getScene().getWindow();
        stage.close();


        //update UI
        if(onCourseCreated != null) {
            onCourseCreated.accept(course);
        }




    }

    public void cancelCourse(){
        //close window
        Stage stage = (Stage) cancel_course_button.getScene().getWindow();
        stage.close();

    }

    private void loadCourses(){
        List<Course> updatedCourse = courseDAO.getCoursesByUser(userSession.getUserId());

    }

    public void setHomePageController(HomePageController controller){
        this.homePageController = controller;
    }


    private Color getColorForStyle(Calendar.Style style){
        switch(style){
            case STYLE1: return Color.web("#77C04B");
            case STYLE2: return Color.web("#418FCB");
            case STYLE3: return Color.web("#F7D15B");
            case STYLE4: return Color.web("#9D5B9F");
            case STYLE5: return Color.web("#D0525F");
            case STYLE6: return Color.web("#F9844B");
            case STYLE7: return Color.web("#AE663E");
            default: return Color.GRAY;
        }
        
    }

    public void setOnCourseCreated(Consumer<Course> onCourseCreated) {
        this.onCourseCreated = onCourseCreated;
    }

    


    
}
