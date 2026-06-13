package com.coursehelper.frontend.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TimeUtils {

    public static ObservableList<String> timeOptions() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mma");
        ObservableList<String> timeOptions = FXCollections.observableArrayList();
        LocalTime time = LocalTime.MIDNIGHT;
        while (!time.equals(LocalTime.MIDNIGHT) || timeOptions.isEmpty()) {
            timeOptions.add(time.format(formatter));
            time = time.plusMinutes(15);
        }
        return timeOptions;
    }

    public static ObservableList<Integer> yearOptions(){

        ObservableList<Integer> yearOptions = FXCollections.observableArrayList();
        int currYear = LocalDate.now().getYear();
        yearOptions.addAll(currYear - 1, currYear, currYear + 1);

        return yearOptions;

    }


    public static boolean isStartBeforeEnd(String startTime, String endTime) {
        LocalTime start = parseTime(startTime);
        LocalTime end = parseTime(endTime);
        
        if (start == null || end == null) return false;
        
        return start.isBefore(end);
    }


    public static LocalTime parseTime(String time) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mma");
            return LocalTime.parse(time.toUpperCase(), formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
}
