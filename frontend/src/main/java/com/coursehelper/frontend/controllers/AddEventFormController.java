package com.coursehelper.frontend.controllers;

import java.time.LocalDate;
import java.util.List;

import org.controlsfx.control.CheckComboBox;

import com.coursehelper.frontend.CalendarManager;
import com.coursehelper.frontend.UserStore;
import com.coursehelper.frontend.dto.AddEventRequestDto;
import com.coursehelper.frontend.dto.EventResponseDto;
import com.coursehelper.frontend.model.Course;
import com.coursehelper.frontend.model.Event;
import com.coursehelper.frontend.service.EventService;
import com.coursehelper.frontend.util.TimeUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.util.Callback;

public class AddEventFormController {

    @FXML private TextField event_title;
    @FXML private ComboBox<String> course_combo;
    @FXML private ComboBox<String> sourceType;
    @FXML private DatePicker date_picker;
    @FXML private ComboBox<String> start_time_combo;
    @FXML private ComboBox<String> end_time_combo;
    @FXML private CheckBox recurring_checkbox;
    @FXML private ComboBox<String> recurrence_combo;
    @FXML private HBox recurrence_hbox;
    @FXML private HBox end_date_hbox;
    @FXML private CheckComboBox<String> repeat_days_checkComboBox;
    @FXML private DatePicker end_date_picker;
    @FXML private Button add_event_button;
    @FXML private Label error_label;

    private Popup popup;
    private UserStore userStore;
    private EventService eventService;
    private CalendarManager calendarManager;

    public void initialize() {
        userStore = UserStore.getInstance();
        eventService = EventService.getInstance();
        calendarManager = CalendarManager.getInstance();

        List<Course> courses = userStore.getCourses();
        for (Course course : courses) {
            course_combo.getItems().add(course.getName());
        }

        recurrence_combo.getItems().addAll("Weekly", "Custom");
        repeat_days_checkComboBox.getItems().addAll(
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

        start_time_combo.setItems(TimeUtils.timeOptions());
        end_time_combo.setItems(TimeUtils.timeOptions());
    }

    public void setPopup(Popup popup) {
        this.popup = popup;
    }

    @FXML
    public void addEvent() {
        String title = event_title.getText().trim();
        String eventType = sourceType.getValue();
        String courseName = course_combo.getValue();
        LocalDate date = date_picker.getValue();
        String startTime = start_time_combo.getValue();
        String endTime = end_time_combo.getValue();
        boolean recurring = recurring_checkbox.isSelected();
        String recurringDays = recurring
            ? String.join(",", repeat_days_checkComboBox.getCheckModel().getCheckedItems())
            : "";

        if (title.isEmpty() || eventType == null || courseName == null
                || date == null || startTime == null || endTime == null) {
            showError("Please fill in all required fields.");
            return;
        }

        if (!TimeUtils.isStartBeforeEnd(startTime, endTime)) {
            showError("Start time must be before end time.");
            return;
        }

        if (recurring && repeat_days_checkComboBox.getCheckModel().getCheckedItems().isEmpty()) {
            showError("Please select at least one repeat day.");
            return;
        }

        if (recurring) {
            LocalDate semesterEnd = userStore.getSettings() != null ? userStore.getSettings().getEndDate() : null;
            LocalDate endDate = end_date_picker.getValue();
            if (semesterEnd != null && endDate != null && endDate.isAfter(semesterEnd)) {
                showError("End date cannot be past the semester end date.");
                return;
            }
        }

        Course course = userStore.getCourses().stream()
            .filter(c -> c.getName().equals(courseName))
            .findFirst().orElse(null);
        if (course == null) return;

        add_event_button.setDisable(true);

        AddEventRequestDto request = new AddEventRequestDto(
            course.getId(), title, eventType, date, startTime, endTime, recurring, recurringDays);

        new Thread(() -> {
            try {
                EventResponseDto dto = eventService.addEvent(request);
                Event event = new Event(dto.getId(), dto.getUserId(), dto.getCourseId(),
                    dto.getTitle(), dto.getEventType(), dto.getAssignmentId(),
                    dto.getStartDate(), dto.getStartTime(), dto.getEndTime(),
                    dto.getIsRecurring(), dto.getRecurringDays());

                userStore.addEvent(event, course.getId());
                calendarManager.addEvent(course, event);

                Platform.runLater(() -> popup.hide());
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Failed to add event. Please try again.");
                    add_event_button.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    public void cancelEvent() {
        popup.hide();
    }

    @FXML
    public void handleRecurringToggle() {
        boolean checked = recurring_checkbox.isSelected();
        recurrence_combo.setVisible(checked);
        recurrence_hbox.setVisible(checked);
        recurrence_hbox.setManaged(checked);
        end_date_hbox.setVisible(checked);
        end_date_hbox.setManaged(checked);
        if (checked) {
            var settings = userStore.getSettings();
            LocalDate semesterEnd = settings != null ? settings.getEndDate() : null;
            end_date_picker.setValue(semesterEnd);
            if (semesterEnd != null) {
                LocalDate cap = semesterEnd;
                Callback<DatePicker, DateCell> factory = dp -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && item.isAfter(cap)) {
                            setDisable(true);
                            setStyle("-fx-background-color: #e0e0e0;");
                        }
                    }
                };
                end_date_picker.setDayCellFactory(factory);
            }
        } else {
            end_date_picker.setValue(null);
            end_date_picker.setDayCellFactory(null);
        }
    }

    @FXML
    public void handleRecurrenceSelection() {
        recurrence_hbox.setVisible(true);
        recurrence_hbox.setManaged(true);
    }

    private void showError(String message) {
        if (error_label != null) {
            error_label.setText(message);
            error_label.setVisible(true);
        }
    }
}
