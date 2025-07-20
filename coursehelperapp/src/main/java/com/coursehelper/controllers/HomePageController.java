package com.coursehelper.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.calendarfx.view.DetailedDayView;
import com.coursehelper.App;
import com.coursehelper.CalendarManager;
import com.coursehelper.Course;
import com.coursehelper.UserSession;
import com.coursehelper.dao.CourseDAO;
import com.coursehelper.dao.EventDAO;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class HomePageController {

    @FXML
    FlowPane coursesContainer;

    @FXML
    VBox calendarContainer;

    @FXML
    DetailedDayView calendarView;

    public CalendarManager calendarManager;

    UserSession userSession = UserSession.getInstance();
    
    CourseDAO courseDAO;

    EventDAO eventDAO;

    Text courseNode;
    

    public void initialize(){
        //initizialize course data access object
        CourseDAO.init();
        EventDAO.init();

        //get DAO instances 
        courseDAO = CourseDAO.getInstance();
        eventDAO = EventDAO.getInstance();
        calendarManager = new CalendarManager();

        
        //get user's courses
        List<Course> user_courses = courseDAO.getCoursesByUser(userSession.getUserId());

        //check if user has courses
        //user does not have courses
        if(user_courses.isEmpty()){

            //update UI
            courseNode = new Text("No Courses");
            coursesContainer.getChildren().add(courseNode);
            createCalendars(null);

        } 
        //user has courses 
        else {

            //Update UI : add course(s) to UI
            for (Course course : user_courses){
                addCourseToHBox(course);
            }

            //display calendars
            createCalendars(user_courses);

        }
        
        
    }

    public void addCourseToHBox(Course course){
        //create course title and menu button
        Text courseTitle = new Text(course.getCourseName());
        Button menuButton = new Button("...");
        menuButton.setStyle("-fx-background-color: transparent; -fx-font-size: 18px;");

        //add to course title and button to box
        HBox courseHBox = new HBox(courseTitle, menuButton);

        //set ID for later access/deletion
        courseHBox.setId("course-" + course.getCourseId());

        courseHBox.setSpacing(10);
        courseHBox.setPrefWidth(200);
        courseHBox.setPrefHeight(150);
        courseHBox.setAlignment(Pos.BOTTOM_CENTER);
        courseHBox.setPadding(new Insets(20, 0 , 20 , 20));
        courseHBox.setStyle("-fx-background-color: " + course.getCourseStyleHex() + "; -fx-border-radius: 10; -fx-background-radius: 10;");
        

        //add context menu on button
        ContextMenu menu = createContextMenu(course);
        menuButton.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                menu.show(menuButton, event.getScreenX(), event.getSceneY());
            }
        });

        // add to UI container 
        coursesContainer.getChildren().add(0, courseHBox);


    }

    private ContextMenu createContextMenu(Course course){
        MenuItem edit = new MenuItem("Edit");
        MenuItem delete = new MenuItem("Delete");

        edit.setOnAction(e -> handleEdit(course));
        delete.setOnAction(e -> handleDelete(course));

        return new ContextMenu(edit, delete);
        
    }

    private void handleEdit(Course course){
        //TODO: create edit popup or go to course page?
    }

    private void handleDelete(Course course){

        Node node = coursesContainer.lookup("#course-" + course.getCourseId());
        //remove from DB
        courseDAO.deleteCourse(userSession.getUserId(), course.getCourseId());
        //remove coursebox
        coursesContainer.getChildren().remove(node);
       
        //delete course calendar
        calendarManager.deleteCalendar(course);

    }



    private void createCalendars(List<Course> user_courses){

            //create calendar view
            // calendarView = new DetailedDayView(); 
            calendarView.setPrefHeight(600);
            calendarView.setVisibleHours(12);

            if (user_courses != null){
                //for each course
                for (Course course : user_courses){ 
                    //add entries to calendar
                    calendarManager.addCourseCalendar(course);  
                }   

            }
             

            //add to calendarView
            calendarView.getCalendarSources().addAll(calendarManager.getCalendarSource());

            //update calendar thread
            calendarView.setRequestedTime(LocalTime.now());

            Thread updateTimeThread = new Thread("Calendar: Update Time Thread"){
                @Override
                public void run(){
                    while(true){
                        Platform.runLater(()-> {
                            calendarView.setToday(LocalDate.now());
                            calendarView.setTime(LocalTime.now());
                            
                        });
                        try {
                            sleep(10000);
                            
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            //thread settings
            updateTimeThread.setPriority(Thread.MIN_PRIORITY);
            updateTimeThread.setDaemon(true);
            updateTimeThread.start();
           

            //add to FXML
            // calendarContainer.getChildren().add(calendarView);


    }

    public void addNewCourseForm(){

        //load and show couse form
        try {
            
            // load the FXML
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/FXML/courseForm.fxml"));
            Parent formNode = loader.load();

            // get access to form's controller
            CreateCourseFormController createCourseFormController = loader.getController();
            createCourseFormController.setHomePageController(this);

            //callback to update UI
            createCourseFormController.setOnCourseCreated(course -> {
                 //remove no course text
                if(courseNode != null && courseNode.isVisible()){
                    coursesContainer.getChildren().remove(courseNode);

                }
                //update course box
                addCourseToHBox(course);

                //add schedule to calendar 
                calendarManager.addCourseCalendar(course);

                //print color
                System.out.println(course.getCourseStyle());


            });


            // create new window
            Stage popupStage = new Stage();
            popupStage.setTitle("Add New Course");

            // make stage modal(block other windows)
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setResizable(false);

            // set scene and show
            Scene scene = new Scene(formNode, 400 , 450);
            popupStage.setScene(scene);
            popupStage.showAndWait();;

            
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }




    

    
    

}