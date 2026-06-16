package com.coursehelper.frontend.controllers;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.coursehelper.frontend.UserStore;
import com.coursehelper.frontend.model.Course;
import com.coursehelper.frontend.model.Task;
import com.coursehelper.frontend.service.TaskService;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class TasksController {

    @FXML private VBox taskList;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM d");

    public void initialize() {
        UserStore userStore = UserStore.getInstance();
        Map<Long, List<Task>> tasksByCourse = userStore.getTasks();
        if (tasksByCourse != null) {
            tasksByCourse.values().forEach(tasks -> tasks.forEach(this::addTaskToList));
        }
    }

    public void addTaskToList(Task task) {
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(Boolean.TRUE.equals(task.getCompleted()));
        checkBox.setStyle("-fx-scale-x: 0.75; -fx-scale-y: 0.75;");

        Text taskTitle = new Text(task.getTitle());
        taskTitle.getStyleClass().add("task-title");
        taskTitle.setStrikethrough(Boolean.TRUE.equals(task.getCompleted()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button deleteBtn = new Button("×");
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: red; -fx-cursor: hand; -fx-padding: 0 2 0 2;");

        HBox titleRow = new HBox(8, checkBox, taskTitle, spacer, deleteBtn);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label dueDate = new Label(task.getDueDate() != null
            ? task.getDueDate().format(DATE_FMT) : "");
        dueDate.setStyle("-fx-font-size: 10px;");

        String courseColor = UserStore.getInstance().getCourses().stream()
            .filter(c -> c.getId().equals(task.getCourseId()))
            .map(Course::getStyleHex)
            .findFirst()
            .orElse("#888888");

        VBox item = new VBox(4, titleRow, dueDate, new Separator());
        VBox.setMargin(dueDate, new Insets(0, 0, 0, 28));
        item.getStyleClass().add("task-row");
        item.setStyle("-fx-border-color: transparent transparent transparent " + courseColor
            + "; -fx-border-width: 0 0 0 4; -fx-padding: 4 0 4 8;");

        checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            taskTitle.setStrikethrough(isSelected);
            task.setCompleted(isSelected);

            new Thread(() -> {
                try {
                    TaskService.getInstance().updateCompletion(task.getId(), isSelected);
                    if (isSelected) {
                        UserStore.getInstance().getTasks().values().forEach(list -> list.remove(task));
                        Platform.runLater(() -> fadeAndRemove(item));
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        checkBox.setSelected(!isSelected);
                        task.setCompleted(!isSelected);
                        taskTitle.setStrikethrough(!isSelected);
                    });
                }
            }).start();
        });

        deleteBtn.setOnAction(e -> {
            new Thread(() -> {
                try {
                    TaskService.getInstance().deleteTask(task.getId());
                    UserStore.getInstance().getTasks().values().forEach(list -> list.remove(task));
                    Platform.runLater(() -> fadeAndRemove(item));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        });

        taskList.getChildren().add(item);
    }

    private void fadeAndRemove(Node item) {
        FadeTransition fade = new FadeTransition(Duration.millis(1000), item);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> taskList.getChildren().remove(item));
        fade.play();
    }
}
