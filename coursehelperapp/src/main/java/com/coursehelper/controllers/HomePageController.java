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
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;


public class HomePageController {

    @FXML
    FlowPane coursesContainer;

    @FXML
    VBox calendarContainer;

    @FXML
    Button add_new_course_button;

    @FXML
    Button add_new_event_button;

    @FXML
    DetailedDayView calendarDetailedDayView;

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
        coursesContainer.getChildren().add(coursesContainer.getChildren().size() - 1, courseHBox);


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

            //customize DetailedDayView
            calendarDetailedDayView.setPrefHeight(600);
            calendarDetailedDayView.setVisibleHours(12);
            
            // DISABLE PopOver and drag-and-drop for now
            //disable dragging/resizing
            calendarDetailedDayView.setEntryEditPolicy(param -> false); 
            //diable context menu
            calendarDetailedDayView.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
                event.consume();
            });
            //disable left click and double click?
            calendarDetailedDayView.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                if(e.getButton() == MouseButton.PRIMARY){
                    e.consume();
                }
            });
            calendarDetailedDayView.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                if(e.getButton() == MouseButton.PRIMARY){
                    e.consume();
                }
            });
             //disable right click and double click?
            calendarDetailedDayView.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                if(e.getButton() == MouseButton.SECONDARY){
                    e.consume();
                }
            });
            calendarDetailedDayView.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                if(e.getButton() == MouseButton.SECONDARY){
                    e.consume();
                }
            });

            //disable hover
            calendarDetailedDayView.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> e.consume());
            calendarDetailedDayView.addEventFilter(MouseEvent.MOUSE_MOVED, e -> e.consume());
            calendarDetailedDayView.addEventFilter(MouseEvent.MOUSE_EXITED, e -> e.consume());

        



            if (user_courses != null){
                //for each course
                for (Course course : user_courses){ 
                    //add entries to calendar
                    calendarManager.addCourseCalendar(course);  
                }   

            }
             

            //add to calendarView
            calendarDetailedDayView.getCalendarSources().addAll(calendarManager.getCalendarSource());

            //update calendar thread
            calendarDetailedDayView.setRequestedTime(LocalTime.now());

            Thread updateTimeThread = new Thread("Calendar: Update Time Thread"){
                @Override
                public void run(){
                    while(true){
                        Platform.runLater(()-> {
                            calendarDetailedDayView.setToday(LocalDate.now());
                            calendarDetailedDayView.setTime(LocalTime.now());
                            
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
            formNode.getStylesheets().add(getClass().getResource("/stylesheets/courseForm.css").toExternalForm());


            // get access to form's controller
            AddCourseFormController addCourseFormController = loader.getController();
            addCourseFormController.setHomePageController(this);

            //callback to update UI
            addCourseFormController.setOnCourseCreated(course -> {
                 //remove no course text
                if(courseNode != null && courseNode.isVisible()){
                    coursesContainer.getChildren().remove(courseNode);

                }
                //update course box
                addCourseToHBox(course);

                //add schedule to calendar 
                calendarManager.addCourseCalendar(course);

            });

            //wrap formNode in Popup
            Popup popup = new Popup();
            addCourseFormController.setPopup(popup);
            popup.getContent().add(formNode);
            // popup.setAutoHide(true); // closes when user clicks elsewhere
            popup.setAutoFix(true); // repositions if near edge

            //show form on right click 
            Point2D point = add_new_course_button.localToScreen(0, add_new_course_button.getHeight());

            popup.show(add_new_course_button, point.getX(), point.getY());



            // // create new window
            // Stage popupStage = new Stage();
            // popupStage.setTitle("Add New Course");

            // // make stage modal(block other windows)
            // popupStage.initModality(Modality.APPLICATION_MODAL);
            // popupStage.setResizable(false);
            // popupStage.setFullScreen(false);
            // popupStage.initOwner(App.primaryStage);

            // // set scene and show
            // Scene scene = new Scene(formNode, 400 , 450);
            // popupStage.setScene(scene);
            // popupStage.showAndWait();

            
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }

    public void addNewEventForm(){

         //load and show event form in custom context-menu form 
        try {
            
            // load the FXML
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/FXML/eventForm.fxml"));
            Parent formNode = loader.load();
            formNode.getStylesheets().add(getClass().getResource("/stylesheets/eventForm.css").toExternalForm());


            // get access to form's controller
            AddEventFormController addEventFormController = loader.getController();
            addEventFormController.setHomePageController(this);

            //wrap formNode in Popup
            Popup popup = new Popup();
            popup.getContent().add(formNode);
            // popup.setAutoHide(true); // closes when user clicks elsewhere
            popup.setAutoFix(true); // repositions if near edge

            //show form on right click 
            Point2D point = add_new_event_button.localToScreen(0, add_new_event_button.getHeight());

            popup.show(add_new_event_button, point.getX(), point.getY());

            



            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }




    

    
    

}