package com.coursehelper.frontend.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

import com.coursehelper.frontend.CalendarManager;
import com.coursehelper.frontend.UserStore;
import com.coursehelper.frontend.dto.AddTaskRequestDto;
import com.coursehelper.frontend.dto.TaskResponseDto;
import com.coursehelper.frontend.model.Course;
import com.coursehelper.frontend.model.Task;
import com.coursehelper.frontend.service.CourseService;
import com.coursehelper.frontend.service.TaskService;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Popup;

public class AddTaskFormController {

    @FXML private ComboBox<String> course_combo;
    @FXML private TextField task_title;
    @FXML private DatePicker due_date_picker;
    @FXML private Label errorLabel;

    private TasksController tasksController;
    private HomePageController homePageController;
    private Popup popup;

    private TaskService taskService;
    private CourseService courseService;
    private CalendarManager calendarManager;
    private UserStore userStore;


    private Consumer<Task> onTaskCreated;
    

    public void initialize(){
        
        //initialize DAOs
        taskService = TaskService.getInstance();
        courseService = CourseService.getInstance();
        calendarManager = CalendarManager.getInstance();
        userStore = UserStore.getInstance();

        //add user's courses to course combobox
        List<Course>userCourses = userStore.getCourses();
        for (Course course : userCourses){
            course_combo.getItems().add(course.getName());
        }


    }


    //get access to TaskList controller 
    public void setTasksController(TasksController controller){
        this.tasksController = controller;
    }

    public void setPopup(Popup popup){
        this.popup = popup;
    }

    @FXML
    public void addTask(){

        if (task_title.getText().equals("")){
            errorLabel.setText("* enter task name");
            return;
        }

        if (course_combo.getValue() == null){
            errorLabel.setText("* select choose course");
            return;
        }

        //remove error text if any 
        errorLabel.setText("");


        String title = task_title.getText();
        Long courseId = userStore.findCourseIdByName(course_combo.getValue());
        LocalDate duedate = due_date_picker.getValue();


        //send request to backend 
        AddTaskRequestDto request = new AddTaskRequestDto(title, courseId, duedate);
        TaskResponseDto response = taskService.addTask(request);
       
        //create task
        Task task = new Task(response.getId(), response.getCourseId(), response.getUserId(), 
                    response.getTitle(), response.getDueDate(), response.getCompleted());

        //add to userStore
        userStore.addTask(task, task.getCourseId());

        popup.hide();
        
        //update UI
        if(onTaskCreated != null) {
            onTaskCreated.accept(task);
        }


    }

    @FXML
    public void cancelTask() {
        //close window
        popup.hide();
    }

    public void setOnTaskCreated(Consumer<Task> onTaskCreated) {
        this.onTaskCreated = onTaskCreated;
    }


    
}
