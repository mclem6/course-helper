package com.coursehelper.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.calendarfx.model.Entry;
import com.calendarfx.view.DetailedDayView;
import com.calendarfx.view.EntryViewBase;
import com.coursehelper.App;
import com.coursehelper.CalendarManager;
import com.coursehelper.Course;
import com.coursehelper.UserSession;
import com.coursehelper.dao.CourseDAO;
import com.coursehelper.dao.EventDAO;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
    Button add_new_task_button;

    @FXML
    AnchorPane addTaskForm;

    @FXML
    DetailedDayView calendarDetailedDayView;

    @FXML
    private VBox taskList;
    @FXML
    private TaskListController taskListController;

    public CalendarManager calendarManager;

    UserSession userSession = UserSession.getInstance();
    
    CourseDAO courseDAO;

    EventDAO eventDAO;

    Text courseNode;

    Popup addNewEventPopup;


    

    public void initialize(){
        //get DAO instances 
        courseDAO = CourseDAO.getInstance();
        eventDAO = EventDAO.getInstance();
        calendarManager = CalendarManager.getInstance();

        
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
            //------------------------------------------------------------------------------------------------
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


            //disable hover
            calendarDetailedDayView.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> e.consume());
            calendarDetailedDayView.addEventFilter(MouseEvent.MOUSE_MOVED, e -> e.consume());
            calendarDetailedDayView.addEventFilter(MouseEvent.MOUSE_EXITED, e -> e.consume());

            //------------------------------------------------------------------------------------------------

            // CUSTOM CONTEXT MENU
            calendarDetailedDayView.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
                Node node = event.getPickResult().getIntersectedNode();

                // Traverse up the node hierarchy to find the EntryViewBase
                while (node != null && !(node instanceof EntryViewBase)) {
                    node = node.getParent();
                }

                if (node instanceof EntryViewBase) {
                    EntryViewBase<?> entryView = (EntryViewBase<?>) node;
                    Entry<?> entry = entryView.getEntry();

                    //get event's ID // should be same for recurring events 
                    Integer eventId = (Integer)entry.getRecurrenceSourceEntry().getUserObject();

                    // Create custom context menu
                    ContextMenu contextMenu = new ContextMenu();

                    MenuItem editItem = new MenuItem("Edit");
                    editItem.setOnAction(e -> {
                        // Replace this with your custom editing logic
                        // showMyEditDialog(entry);
                    });
                    
                    //Delete entry from database and calendar
                    MenuItem deleteItem = new MenuItem("Delete");
                    deleteItem.setOnAction(e -> {

                        //if reoccuring event, ask if user wants to delete all or just single event
                        if (entry.isRecurring()){

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Delete Recurring Event");
                            alert.setHeaderText("This is a recurring event.");
                            alert.setContentText("Deleting this event will delete all dates associated with this event.");

                            ButtonType deleteAll = new ButtonType("Confirm Delete");
                            ButtonType cancel = ButtonType.CANCEL;

                            alert.getButtonTypes().setAll(deleteAll, cancel);

                            Optional<ButtonType> result = alert.showAndWait();

                            if(result.isPresent()){

                                // delete event from calendar and delete from events table
                                if (result.get() == deleteAll){
                                    //delete event ftom events table 
                                    eventDAO.deleteEvent(userSession.getUserId(), eventId);

                                    //delete all entries from calendar 
                                    entry.getRecurrenceSourceEntry().removeFromCalendar();

                                }
                            } 
                        }
                        //single event
                        else {
                            
                            //delete from events table
                            eventDAO.deleteEvent(userSession.getUserId(), eventId);

                            //remove from calendar
                            entry.removeFromCalendar();
                        }
         
            
                    });

                    contextMenu.getItems().addAll(editItem, deleteItem);
                    contextMenu.show(entryView, event.getScreenX(), event.getScreenY());

                    event.consume(); // Prevent default context menu from appearing
                }
            });        

            // CREATE CALENDAR FOR COURSES
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
            formNode.getStylesheets().add(getClass().getResource("/stylesheets/form.css").toExternalForm());


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
            popup.setAutoHide(true); // closes when user clicks elsewhere
            popup.setAutoFix(true); // repositions if near edge

            //show form on right click 
            Point2D point = add_new_course_button.localToScreen(0, add_new_course_button.getHeight());

            popup.show(add_new_course_button, point.getX(), point.getY());


            
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }

    //popup for add_new_event_button 
    //shows toggling between new assignment and new course event 
    public void addNewPopup(){

        try {

            // load the FXML
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/FXML/addEventPopup.fxml"));
            Parent popupNode = loader.load();
            popupNode.getStylesheets().add(getClass().getResource("/stylesheets/newEventPopup.css").toExternalForm());


            //get controller and pass homepage controller
            AddEventPopupController addEventPopupController = loader.getController();
            addEventPopupController.setHomePageController(this);

            //initialize popup and pass to controller
            addNewEventPopup = new Popup();
            addEventPopupController.setPopup(addNewEventPopup);

            //add eventpopup form node to popup
            addNewEventPopup.getContent().add(popupNode);

            //settings
            addNewEventPopup.setAutoHide(true);
            addNewEventPopup.setAutoFix(true);

            //set popup location
            Point2D point = add_new_event_button.localToScreen(0, add_new_event_button.getHeight());
            addNewEventPopup.show(add_new_event_button, point.getX(), point.getY());

            //show assignment form as default
            addEventPopupController.addNewAssignmentForm();
            
            

        
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ObservableList<String> timeOptions(){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mma");
        ObservableList<String> timeOptions =  FXCollections.observableArrayList();
    
        LocalTime time = LocalTime.MIDNIGHT;

        //while not 11:45PM, loop will keep addingt ime increment 15 minutes.
        while(!time.equals(LocalTime.MIDNIGHT) || timeOptions.isEmpty()){
            timeOptions.add(time.format(formatter));
            time = time.plusMinutes(15);
        }

        return timeOptions;
    }

    @FXML
    public void addNewTask(){

        //load and show couse form
        try {
            
            // load the FXML
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/FXML/taskForm.fxml"));
            Parent formNode = loader.load();
            formNode.getStylesheets().add(getClass().getResource("/stylesheets/form.css").toExternalForm());



            // get access to task's controller
            AddTaskFormController addTaskFormController = loader.getController();
            
            //Pass TaskList Controller
            addTaskFormController.setTaskListController(taskListController);

            addTaskForm.getChildren().add(formNode);

            //callback to update UI
            // addCourseFormController.setOnCourseCreated(course -> {
            //      //remove no course text
            //     if(courseNode != null && courseNode.isVisible()){
            //         coursesContainer.getChildren().remove(courseNode);

            //     }
            //     //update course box
            //     addCourseToHBox(course);

            //     //add schedule to calendar 
            //     calendarManager.addCourseCalendar(course);

            // });


            //wrap formNode in Popup
            Popup popup = new Popup();
            addTaskFormController.setPopup(popup);
            popup.getContent().add(formNode);
            popup.setAutoHide(true); // closes when user clicks elsewhere
            popup.setAutoFix(true); // repositions if near edge

            //show form on right click 
            Point2D point = add_new_task_button.localToScreen(0, add_new_task_button.getHeight());

            popup.show(add_new_task_button, point.getX(), point.getY());

            
        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }



    
    


}