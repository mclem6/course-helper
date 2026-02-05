package com.coursehelper.controllers;


import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.calendarfx.view.DetailedDayView;
import com.calendarfx.view.DetailedWeekView;
import com.coursehelper.App;
import com.coursehelper.CalendarManager;
import com.coursehelper.UserSession;
import com.coursehelper.dao.CourseDAO;
import com.coursehelper.dao.EventDAO;
import com.coursehelper.menu.CalendarEntryContextMenu;
import com.coursehelper.model.Course;
import com.coursehelper.model.Quote;
import com.coursehelper.services.QuoteService;
import com.coursehelper.skin.SidebarCalendarWeekViewSkin;
import com.coursehelper.theme.ThemeManager;
import com.coursehelper.ui.CalendarViewConfigurator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;


public class HomePageController {

    @FXML
    BorderPane root;

    @FXML
    ToggleButton themeToggle;

    @FXML
    FlowPane coursesContainer;

    @FXML
    Label quoteLabel;

    @FXML
    Label authorLabel;


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
    DetailedWeekView calendarWeekView;

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

    private static QuoteService quoteService;


    

    public void initialize(){

        //get DAO instances 
        courseDAO = CourseDAO.getInstance();
        eventDAO = EventDAO.getInstance();
        calendarManager = CalendarManager.getInstance();


        //init quote service and load quote 
        quoteService = new QuoteService();
        loadQuote();


        
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


        //set up theme toggle button
        boolean isDarkMode = ThemeManager.isDarkMode();

        themeToggle.setSelected(isDarkMode);
        themeToggle.setText(isDarkMode ? "LightMode" : "DarkMode");

    }

    @FXML
    public void loadQuote(){
        Quote quote = quoteService.getNextQuote();
        quoteLabel.setText(quote.getText());
        authorLabel.setText(quote.getAuthor());
    }

    @FXML
    private void onToggleTheme(){
        String newTheme = themeToggle.isSelected() ? "DarkMode" : "LightMode";
        ThemeManager.setTheme(root, newTheme);
        ThemeManager.saveThemePreference(newTheme);
        themeToggle.setText(!themeToggle.isSelected() ? "DarkMode" : "LightMode");

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

        //configure size
        CalendarViewConfigurator.configureSize(calendarDetailedDayView, 600, 8);
        CalendarViewConfigurator.configureSize(calendarWeekView, 200, 4);
        
        //install entry context menu
        CalendarEntryContextMenu.install(calendarDetailedDayView, eventDAO, userSession);
        CalendarEntryContextMenu.install(calendarWeekView, eventDAO, userSession);

        //diasbale interactive calendar 
        CalendarViewConfigurator.configureReadOnly(calendarDetailedDayView);
        CalendarViewConfigurator.configureReadOnly(calendarWeekView);

        //set skin
        calendarWeekView.setSkin(new SidebarCalendarWeekViewSkin(calendarWeekView));

    
        

        //populate calendars for views
        calendarManager.populateCalendar(user_courses, calendarDetailedDayView);
        calendarManager.populateCalendar(user_courses, calendarWeekView);

        //run time-lines
        CalendarViewConfigurator.startClockUpdater(calendarDetailedDayView);
        CalendarViewConfigurator.startClockUpdater(calendarWeekView);


        


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
            popupNode.getStylesheets().add(getClass().getResource("/stylesheets/"+ ThemeManager.getCurrentTheme() +"/newEventPopup.css").toExternalForm());


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
            formNode.getStylesheets().add(getClass().getResource("/stylesheets/" + ThemeManager.getCurrentTheme() + "/form.css").toExternalForm());



            // get access to task's controller
            AddTaskFormController addTaskFormController = loader.getController();
            
            //Pass TaskList Controller
            addTaskFormController.setTaskListController(taskListController);

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