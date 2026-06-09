package com.coursehelper.frontend.controllers;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.controlsfx.control.CheckComboBox;

import com.calendarfx.model.Calendar;
import com.coursehelper.frontend.UserStore;
import com.coursehelper.frontend.dto.AddCourseRequestDto;
import com.coursehelper.frontend.dto.AddEventRequestDto;
import com.coursehelper.frontend.dto.CourseResponseDto;
import com.coursehelper.frontend.model.Course;
import com.coursehelper.frontend.model.Event;
import com.coursehelper.frontend.service.CourseService;
import com.coursehelper.frontend.service.EventService;
import com.coursehelper.frontend.util.TimeUtils;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;

public class AddCourseFormController {

    @FXML private CheckComboBox<String> select_days_checkComboBox;
    // @FXML private ComboBox<String> semester_combo;
    // @FXML private ComboBox<Integer> year_combo;
    @FXML private Label semesterYearLabel;
    @FXML private ComboBox<String> start_time_combo;
    @FXML private ComboBox<String> end_time_combo;
    @FXML private ComboBox<Calendar.Style> style_combo;
    @FXML private TextField course_name;
    @FXML private Label errorLabel;

    private CourseService courseService;
    private EventService eventService;
    private UserStore userStore;

    private HomePageController homePageController;
    private Popup popup;
    private Consumer<Course> onCourseCreated;

    

    public void initialize(){

        courseService = CourseService.getInstance();
        eventService = EventService.getInstance();
        userStore = UserStore.getInstance();

        //add semester, year label
        semesterYearLabel.setText(userStore.getSettings().getSemester() + " " +
        userStore.getSettings().getYear());

        //create time options then add to start and end combos
        start_time_combo.setItems(TimeUtils.timeOptions());
        end_time_combo.setItems(TimeUtils.timeOptions());

        //add select_days options
        select_days_checkComboBox.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        select_days_checkComboBox.setTitle("");
        select_days_checkComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> {
            if (select_days_checkComboBox.getCheckModel().getCheckedItems().isEmpty()) {
                select_days_checkComboBox.setTitle(""); // Acts like a placeholder
            } else {

                //get selected options
                 List<String> selected = select_days_checkComboBox.getCheckModel().getCheckedItems();

                //add to title
                select_days_checkComboBox.setTitle(String.join(", ", selected));
            }
        });

        // add course color selection
        style_combo.getItems().addAll(Calendar.Style.values());

        //define colors in dropdown list 
        style_combo.setCellFactory(lv -> new ListCell<>() {
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
        style_combo.setButtonCell(new ListCell<>() {
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
    }

    public void setHomePageController(HomePageController controller){
        this.homePageController = controller;
    }

     //add course handler
    @FXML
    private void addCourse() {

        if(!valid_submission()){
            return;
        }
        
        //get user input from form fields
        String name = course_name.getText();
        String semester = userStore.getSettings().getSemester();
        int courseYear = userStore.getSettings().getYear();
        String startTime = start_time_combo.getValue();
        String endTime = end_time_combo.getValue();
        String lectureDays = select_days_checkComboBox.getTitle();
        LocalDate startDate = userStore.getSettings().getStartDate();
        LocalDate endDate = userStore.getSettings().getEndDate();
        String style = style_combo.getValue().name();

        //send course to database
        AddCourseRequestDto request = new AddCourseRequestDto(name, style, semester, courseYear, startDate, endDate, startTime, endTime, true, lectureDays);

        CourseResponseDto response = courseService.addCourse(request);
        
        Course course = new Course(response.getId(), response.getName(), response.getStyle(), response.getSemester(), response.getCourseYear(),
                response.getStartDate(), response.getEndDate(), response.getStartTime(), response.getEndTime(), response.getRecurring(), response.getLectureDays());
        

        //send event database
        AddEventRequestDto request2 = new AddEventRequestDto(course.getId(), name, CourseService.SOURCE_TYPE, startDate, startTime, endTime, true,lectureDays);
        eventService.addEvent(request2);

        //fetch new data
        List<Course> userCourses = courseService.getUserCourses();
        Map<Long, List<Event>> userEvents = eventService.getAllEvents();
        
        //refresh store
        userStore.refreshCourses(userCourses);
        userStore.refreshEvents(userEvents);
        
        popup.hide();
        
        //update UI
        if(onCourseCreated != null) {
            onCourseCreated.accept(course);
        }
        

    }



    @FXML
    public void cancelCourse(){
        //close window
        popup.hide();
    }


    public void setPopup(Popup popup){
        this.popup = popup;
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


    Boolean valid_submission(){
         if (course_name.getText().equals("")){
            errorLabel.setText("* enter course name");
            return false;
        }

        if (start_time_combo.getValue() == null){
            errorLabel.setText("* select start time");
            return false;
        }

        if (end_time_combo.getValue() == null){
            errorLabel.setText("* select end time");
            return false;
        }

        if (!TimeUtils.isStartBeforeEnd(start_time_combo.getValue(), end_time_combo.getValue())){
            errorLabel.setText("* end time must be after start time");
            return false;
        }

        if (select_days_checkComboBox.getTitle().equals("")){
            errorLabel.setText("* select class days");
            return false;
        }

        if (select_days_checkComboBox.getTitle() == null){
            errorLabel.setText("* select lecture days");
            return false;
        }


        if (style_combo.getValue() == null){
            errorLabel.setText("* select course color");
            return false;
        }

        errorLabel.setText("");
        return true;
    }

    
    
}
