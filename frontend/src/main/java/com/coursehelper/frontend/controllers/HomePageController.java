package com.coursehelper.frontend.controllers;
import java.util.List;

import com.calendarfx.view.DetailedDayView;
import com.coursehelper.frontend.App;
import com.coursehelper.frontend.CalendarManager;
import com.coursehelper.frontend.UserStore;
import com.coursehelper.frontend.model.Course;
import com.coursehelper.frontend.model.Event;
import com.coursehelper.frontend.theme.ThemeManager;
import com.coursehelper.frontend.ui.CalendarViewConfigurator;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;


public class HomePageController {

    @FXML private FlowPane coursesContainer;
    @FXML private Button add_new_course_button;
    @FXML private Button add_new_event_button; 
    @FXML private Button addNewTaskBtn;
    @FXML private DetailedDayView calendarDetailedDayView;
    @FXML private TasksController tasksController;
    @FXML private SidebarController sidebarController;
    @FXML private Label noCourseLabel;

    private Text courseNode;
    private Popup addNewEventPopup; 
    private final UserStore userStore = UserStore.getInstance();
    private final CalendarManager calendarManager = CalendarManager.getInstance();
    

    public void initialize(){ 
        //setup calendars
        setUpCalendars();

        //create course box
        addAllCoursesToHbox(userStore.getCourses());
    }


    public void addCourseToHBox(Course course){
        //create course title and menu button
        Text courseTitle = new Text(course.getName());
        Button menuButton = new Button("...");
        menuButton.setStyle("-fx-background-color: transparent; -fx-font-size: 18px;");

        //add to course title and button to box
        HBox courseHBox = new HBox(courseTitle, menuButton);

        //set ID for later access/deletion
        courseHBox.setId("course-" + course.getId());

        courseHBox.setSpacing(10);
        courseHBox.setPrefWidth(200);
        courseHBox.setPrefHeight(150);
        courseHBox.setAlignment(Pos.BOTTOM_CENTER);
        courseHBox.setPadding(new Insets(20, 0 , 20 , 20));
        courseHBox.setStyle("-fx-background-color: " + course.getStyleHex() + "; -fx-border-radius: 10; -fx-background-radius: 10;");
        

        //add context menu on button
        ContextMenu menu = createContextMenu(course);
        menuButton.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                menu.show(menuButton, event.getScreenX(), event.getSceneY());
            }
        });

        // add to UI container 
        coursesContainer.getChildren().add(courseHBox);


    }

    public void addAllCoursesToHbox(List<Course> courses){
        if(courses == null || courses.isEmpty()){
            noCourseLabel.setText("Add course to get started!");
            return;
        }
        for(Course course : courses){
            addCourseToHBox(course);
        }
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

        // Node node = coursesContainer.lookup("#course-" + course.getCourseId());
        // //remove from DB
        // courseDAO.deleteCourse(userSession.getUserId(), course.getCourseId());
        // //remove coursebox
        // coursesContainer.getChildren().remove(node);
       
        // //delete course calendar
        // calendarManager.deleteCalendar(course);

    }



    private void setUpCalendars(){

        //configure size
        CalendarViewConfigurator.configureSize(calendarDetailedDayView, 600, 8);
        
        //install entry context menu
        // CalendarEntryContextMenu.install(calendarDetailedDayView, eventDAO, userSession);
        // CalendarEntryContextMenu.install(calendarWeekView, eventDAO, userSession);

        //diasbale interactive calendar 
        CalendarViewConfigurator.configureReadOnly(calendarDetailedDayView);
        

        //fill calendar 
        calendarDetailedDayView.getCalendarSources().setAll(calendarManager.getCalendarSource());

        //run time-lines
        CalendarViewConfigurator.startClockUpdater(calendarDetailedDayView);

    }

    public void addNewCourseForm(){

        //load and show couse form
        try {
            
            // load the FXML
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/FXML/courseForm.fxml"));
            Parent formNode = loader.load();
            formNode.getStylesheets().add(getClass().getResource("/stylesheets/" + ThemeManager.getCurrentTheme() +"/form.css").toExternalForm());


            // get access to form's controller
            AddCourseFormController addCourseFormController = loader.getController();
            addCourseFormController.setHomePageController(this);

            //callback to update UI
            addCourseFormController.setOnCourseCreated(course -> {
                 //remove no course text
                if(courseNode != null && courseNode.isVisible()){
                    coursesContainer.getChildren().remove(courseNode);

                }

                if(!noCourseLabel.getText().equals("")){
                     noCourseLabel.setVisible(false);
                }
                //update course box
                addCourseToHBox(course);

                //get events for course
                List<Event> courseEvents = userStore.getEventsByCourseId(course.getId());

                //add course calendar
                calendarManager.addCourseCalendar(course);

                //load events
                calendarManager.loadEventsForCourse(course, courseEvents);


            });

            //wrap formNode in Popup
            Popup popup = new Popup();
            addCourseFormController.setPopup(popup);
            popup.getContent().add(formNode);
            popup.setAutoHide(false);
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
            popupNode.getStylesheets().add(getClass().getResource("/stylesheets/" + ThemeManager.getCurrentTheme() +"/form.css").toExternalForm());

            //get controller and pass homepage controller
            AddEventPopupController addEventPopupController = loader.getController();

            //set consumer
            addEventPopupController.setOnAssignmentCreated(assignment-> {
                Course course = userStore.getCourseById(assignment.getCourseId());
                Event event = userStore.getEventByAssignmentId(assignment.getId());
                calendarManager.addEvent(course, event);
            });

            //initialize popup and pass to controller
            addNewEventPopup = new Popup();
            addEventPopupController.setPopup(addNewEventPopup);

            //add eventpopup form node to popup
            addNewEventPopup.getContent().add(popupNode);

            //settings
            addNewEventPopup.setAutoHide(false);
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


    @FXML
    public void addNewTask(){

        //load and show couse form
        try {
            
            // load the FXML
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/FXML/taskForm.fxml"));
            Parent formNode = loader.load();
            formNode.getStylesheets().add(getClass().getResource("/stylesheets/" + ThemeManager.getCurrentTheme() + "/form.css").toExternalForm());

            // get access to task's controller
            AddTaskFormController addTaskFormController = loader.getController();

            //callback to update UI
            addTaskFormController.setOnTaskCreated(task -> {
              
                //update tasklist
                tasksController.addTaskToList(task);
            

            });
            
            //Pass TaskList Controller
            addTaskFormController.setTasksController(tasksController);

            //wrap formNode in Popup
            Popup popup = new Popup();
            addTaskFormController.setPopup(popup);
            popup.getContent().add(formNode);
            popup.setAutoHide(false);
            popup.setAutoFix(true); // repositions if near edge

            //show form on right click 
            Point2D point = addNewTaskBtn.localToScreen(0, addNewTaskBtn.getHeight());

            popup.show(addNewTaskBtn, point.getX(), point.getY());

            
        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }



    
    


}