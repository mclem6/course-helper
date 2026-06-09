package com.coursehelper.frontend.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

import com.coursehelper.frontend.model.Assignment;
import com.coursehelper.frontend.model.Course;
import com.coursehelper.frontend.model.Event;
import com.coursehelper.frontend.service.AssignmentService;
import com.coursehelper.frontend.util.TimeUtils;
import com.coursehelper.frontend.*;
import com.coursehelper.frontend.dto.AddAssignmentRequestDto;
import com.coursehelper.frontend.dto.AssignmentCreatedResponseDto;
import com.coursehelper.frontend.dto.AssignmentResponseDto;
import com.coursehelper.frontend.dto.EventResponseDto;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Popup;

public class AddAssignmentFormController {

    @FXML private ComboBox<String> course_combo;
    @FXML private TextField assignment_title;
    @FXML private DatePicker due_date_picker;
    @FXML private ComboBox<String> due_time_combo;
    @FXML private ComboBox<String> assignment_type;

    private HomePageController homePageController;
    private Popup popup;
    private AssignmentService assignmentService;
    private UserStore userStore;
    private CalendarManager calendarManager;

    private Consumer<Assignment> onAssignmentCreated;

    public void initialize(){
        //init services
        assignmentService = AssignmentService.getInstance();
        userStore = UserStore.getInstance();
        calendarManager = CalendarManager.getInstance();


        //add user's courses to course combobox
        List<Course> userCourses = userStore.getCourses();
        for (Course course : userCourses){
            course_combo.getItems().add(course.getName());
        }

        //add assignmnent type
        assignment_type.getItems().addAll();

        //create time options and add to due_time
        due_time_combo.setItems(TimeUtils.timeOptions());
    
    }


    //get access to homepage controller 
    public void setHomePageController(HomePageController controller){
        this.homePageController = controller;
    }

    public void setPopup(Popup popup){
        this.popup = popup;
    }


    @FXML
    public void addAssignment(){


        //spawn new thread for api call
        Task <Void> task = new Task<>(){

            Long course_id = userStore.findCourseIdByName(course_combo.getValue());
            String title = assignment_title.getText();
            String event_type = assignment_type.getValue();
            LocalDate due_date = due_date_picker.getValue();
            String due_time = due_time_combo.getValue();

            @Override
            protected Void call() throws Exception{

                //create assignment request dto
                AddAssignmentRequestDto request = new AddAssignmentRequestDto(course_id, title, "",due_date, due_time, "INCOMPLETE", event_type);

                //add assignment to database
                AssignmentCreatedResponseDto response = assignmentService.addAssignment(request);

                //get assignment request object 
                AssignmentResponseDto assignmentResponseDto = response.getAssignment();

                //create assignment object
                Assignment new_assignment = new Assignment(assignmentResponseDto.getId(), assignmentResponseDto.getUserId(), assignmentResponseDto.getCourseId(),assignmentResponseDto.getTitle(), 
                                            assignmentResponseDto.getDescription(), assignmentResponseDto.getDueDate(), assignmentResponseDto.getDueTime(), assignmentResponseDto.getStatus(), 
                                            assignmentResponseDto.getAssignmentType());

                //add assignment to userStore
                userStore.addAssignment(new_assignment, course_id);   

                //get event request object
                EventResponseDto eventResponseDto = response.getEvent();

                //create event object
                Event new_event = new Event(eventResponseDto.getId(), eventResponseDto.getUserId(), eventResponseDto.getCourseId(), eventResponseDto.getTitle(),
                                  eventResponseDto.getEventType(), eventResponseDto.getAssignmentId(),eventResponseDto.getStartDate(),eventResponseDto.getStartTime(),
                                  eventResponseDto.getEndTime(), eventResponseDto.getIsRecurring(), eventResponseDto.getRecurringDays());


                //add event to userStore
                userStore.addEvent(new_event, course_id);  

                // notify parent
                if (onAssignmentCreated != null) {
                    onAssignmentCreated.accept(new_assignment);
                }

                return null;

                
            }


        };

        task.setOnSucceeded(e -> {

            //add to calendar
            // calendarManager.addEvent(

            //close popup
            popup.hide();

        });

        task.setOnFailed(e -> {
            Throwable error = task.getException();
            System.out.println("Task failed: " + error.getMessage());
            error.printStackTrace();
        });

        //start thread
        new Thread(task).start();

}

    @FXML
    public void cancelAssignment(){
        popup.hide();
    }

    public void setOnAssignmentCreated(Consumer<Assignment> onAssignmentCreated) {
        this.onAssignmentCreated = onAssignmentCreated;
    }

  

    
}
