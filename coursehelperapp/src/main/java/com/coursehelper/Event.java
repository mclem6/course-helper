package com.coursehelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Event {
    
    private String title, event_type, start_day, start_time, end_time , classDays;
    LocalDate start_date;
    int courseId;
    
    public Event(int courseID, String title, String event_type, LocalDate start_date, String start_time, String end_time , String classDays){
        this.courseId = courseId;
        this.title = title;
        this.event_type = event_type;
        this.start_date = start_date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.classDays = classDays;
    }

    public List<String> getClassDays(){

        List<String> class_day_list = new ArrayList<>();

        //seperate days by comma
        String[] days = classDays.split(",");
        for (String day : days) {
            day = day.trim();
            switch (day) {
                case "Monday":
                    class_day_list.add("MO");
                    break;
                case "Tuesday":
                    class_day_list.add("TU");
                    break;
                case "Wednesday":
                    class_day_list.add("WE");
                    break;
                case "Thursday":
                    class_day_list.add("TH");
                    break;
                case "Friday":
                    class_day_list.add("FR");
                    break;
                case "Saturday":
                    class_day_list.add("SA");
                    break;
                case "Sunday":
                    class_day_list.add("SU");
                    break;
                default:
                    break;
            }
        }

        return class_day_list;
        
    }

    public String getEventType(){
        return event_type;
    }

    public String getTitle(){
        return title;
    }

    //convert start_date and start_time to LocalDateTime
    public LocalDateTime getStartLocalDateTime(){

        //create LocalTime 
        String[] hour_min = start_time.split(":");
        int hour = Integer.parseInt(hour_min[0]);
        int min = Integer.parseInt(hour_min[1].substring(0,2));
        String meridiem = hour_min[1].substring(2);

        
        if (meridiem.equals("AM")){
            if(hour == 12) hour = 0;
        } else {
            if (hour != 12) hour += 12;
        }

        LocalTime time = LocalTime.of(hour, min);

        //retunr LocalDateTime
        return LocalDateTime.of(this.start_date, time);

        
    }

    //convert start_date and end_time to LocalDateTime
    public LocalDateTime getEndLocalDateTime(){

        //create LocalTime 
        String[] hour_min = end_time.split(":");
        int hour = Integer.parseInt(hour_min[0]);
        int min = Integer.parseInt(hour_min[1].substring(0,2));
        String meridiem = hour_min[1].substring(2);

        if (meridiem.equals("AM")){
            if(hour == 12) hour = 0;
        } else {
            if (hour != 12) hour += 12;
        }

        LocalTime time = LocalTime.of(hour, min);

        //return LocalDateTime
        return LocalDateTime.of(this.start_date, time);

    }




   


    
}
