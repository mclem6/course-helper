package com.coursehelper.ui;

import java.time.LocalDate;
import java.time.LocalTime;

import com.calendarfx.view.DayViewBase;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class CalendarViewConfigurator {


    // DISABLE PopOver and drag-and-drop for now
    public static void configureReadOnly(DayViewBase view){

        //disable dragging/resizing
        view.setEntryEditPolicy(param -> false); 
        //diable context menu
        view.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
            event.consume();
        });
        //disable left click and double click?
        view.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if(e.getButton() == MouseButton.PRIMARY){
                e.consume();
            }
        });
        view.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if(e.getButton() == MouseButton.PRIMARY){
                e.consume();
            }
        });

        //disable hover
        view.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> e.consume());
        view.addEventFilter(MouseEvent.MOUSE_MOVED, e -> e.consume());
        view.addEventFilter(MouseEvent.MOUSE_EXITED, e -> e.consume());

    }

    public static void configureSize(DayViewBase view, double height, int hours){
        //customize DetailedDayView
        view.setPrefHeight(height);
        view.setVisibleHours(hours);
    }


    public static void startClockUpdater(DayViewBase view){
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(10), e -> {
                view.setTime(LocalTime.now());
                view.setToday(LocalDate.now());
            })
        );

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        view.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (oldScene != null) timeline.stop();
        });   
    
    }
    
}
