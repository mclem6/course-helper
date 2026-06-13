package com.coursehelper.frontend.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.calendarfx.view.DetailedWeekView;
import com.coursehelper.frontend.CalendarManager;
import com.coursehelper.frontend.UserStore;
import com.coursehelper.frontend.model.Assignment;
import com.coursehelper.frontend.model.Course;
import com.coursehelper.frontend.model.Quote;
import com.coursehelper.frontend.service.AssignmentService;
import com.coursehelper.frontend.service.QuoteService;
import com.coursehelper.frontend.skin.SidebarCalendarWeekViewSkin;
import com.coursehelper.frontend.ui.CalendarViewConfigurator;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class SidebarController {

    @FXML private VBox root;
    @FXML private Label quoteLabel;
    @FXML private Label authorLabel;
    @FXML private DetailedWeekView calendarWeekView;
    @FXML private VBox pastDuePanel;
    @FXML private VBox pastDueList;
    @FXML private VBox dueSoonList;


    private MainLayoutController main;
    private CalendarManager calendarManager;
    private QuoteService quoteService;
    private UserStore userStore;
    private AssignmentService assignmentService;
    

    public void initialize(){
        calendarManager = CalendarManager.getInstance();
        quoteService = new QuoteService();
        userStore = UserStore.getInstance();
        assignmentService = AssignmentService.getInstance();

        setUpCalendars();
        loadQuote();
        loadPastDue();
        loadDueDates();

        userStore.getUpcomingAssignments().addListener(
            (ListChangeListener<Assignment>) change -> Platform.runLater(() -> {
                loadPastDue();
                loadDueDates();
            })
        );

    }


    public void setMain(MainLayoutController main){
        this.main = main;
    }

    private void loadPastDue() {
        Map<Long, List<Assignment>> assignmentsByCourse = userStore.getAssignments();

        if (assignmentsByCourse == null || assignmentsByCourse.isEmpty()) {
            pastDuePanel.setVisible(false);
            pastDuePanel.setManaged(false);
            return;
        }

        List<Assignment> overdue = assignmentsByCourse.entrySet().stream()
            .flatMap(entry -> entry.getValue().stream())
            .filter(a -> a.getDueDate() != null)
            .filter(a -> a.getDueDate().isBefore(LocalDate.now()))
            .filter(a -> "INCOMPLETE".equals(a.getStatus()))
            .sorted(Comparator.comparing(Assignment::getDueDate))
            .collect(Collectors.toList());

        if (overdue.isEmpty()) {
            pastDuePanel.setVisible(false);
            pastDuePanel.setManaged(false);
            return;
        }

        pastDueList.getChildren().clear();
        for (Assignment assignment : overdue) {
            Course course = userStore.getCourseById(assignment.getCourseId());
            pastDueList.getChildren().add(buildAssignmentRow(assignment, course));
        }
        pastDuePanel.setVisible(true);
        pastDuePanel.setManaged(true);
    }

    private void loadDueDates(){

        Map<Long, List<Assignment>> assignmentsByCourse = userStore.getAssignments();

        if (assignmentsByCourse == null || assignmentsByCourse.isEmpty()){
            showEmptyState();
            return;
        }

        List<Assignment> allAssignments= assignmentsByCourse.entrySet().stream()                
            .flatMap(entry -> entry.getValue().stream())
            .filter(a -> a.getDueDate() != null)
            .filter(a -> !a.getDueDate().isBefore(LocalDate.now()))
            .filter(a -> a.getStatus().equals("INCOMPLETE"))
            .sorted(Comparator.comparing(Assignment::getDueDate))
            .limit(5)
            .collect(Collectors.toList());


            if (allAssignments.isEmpty()) {
                showEmptyState();
                return;
            }

            dueSoonList.getChildren().clear();
            for (Assignment assignment : allAssignments){
                Course course = userStore.getCourseById(assignment.getCourseId());
                dueSoonList.getChildren().add(buildAssignmentRow(assignment, course));
            
            }

    }

    private void showEmptyState(){
        Label empty = new Label("You're all caught up!");
        empty.setWrapText(true);
        empty.getStyleClass().add("empty-state-label");
        dueSoonList.getChildren().setAll(empty);
    }

    private HBox buildAssignmentRow(Assignment assignment, Course course) {
        String courseColor = course.getStyleHex();
        Circle circle = new Circle(5, Paint.valueOf(courseColor));

        String dateDisplay;
        if (assignment.getDueDate().equals(LocalDate.now())) {
            dateDisplay = "Today";
        } else if (assignment.getDueDate().equals(LocalDate.now().plusDays(1))) {
            dateDisplay = "Tomorrow";
        } else {
            dateDisplay = assignment.getDueDate()
                .format(DateTimeFormatter.ofPattern("MM/d"));
        }

        Label titleDate = new Label(assignment.getTitle() + " - " + dateDisplay);

        CheckBox checkBox = new CheckBox();
        checkBox.setOnAction(e -> {
            checkBox.setDisable(true);
            new Thread(() -> {
                try {
                    assignmentService.markComplete(assignment.getId());
                    userStore.markAssignmentComplete(assignment.getId());
                    Platform.runLater(() -> {
                        loadPastDue();
                        loadDueDates();
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> checkBox.setDisable(false));
                }
            }).start();
        });

        HBox row = new HBox(8, checkBox, circle, titleDate);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }



    public void refreshDueSoon(){
        loadPastDue();
        loadDueDates();
    }


    private void setUpCalendars(){

        //configure size
        CalendarViewConfigurator.configureSize(calendarWeekView, 200, 4);
        
        //install entry context menu
        // CalendarEntryContextMenu.install(calendarDetailedDayView, eventDAO, userSession);
        // CalendarEntryContextMenu.install(calendarWeekView, eventDAO, userSession);

        //diasbale interactive calendar 
        CalendarViewConfigurator.configureReadOnly(calendarWeekView);

        //set skin
        calendarWeekView.setSkin(new SidebarCalendarWeekViewSkin(calendarWeekView));

        //populate calendars for views
        calendarWeekView.getCalendarSources().setAll(calendarManager.getCalendarSource());

        //run time-lines
        CalendarViewConfigurator.startClockUpdater(calendarWeekView);


    }

    @FXML
    public void loadQuote(){
        new Thread(() -> {
            Quote quote = quoteService.getNextQuote();
            Platform.runLater(() -> {
                quoteLabel.setText(quote.getText());
                authorLabel.setText(quote.getAuthor());
            });
        }).start();
    }

    

}
