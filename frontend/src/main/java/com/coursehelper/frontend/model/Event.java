package com.coursehelper.frontend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Event {
    
    private Long eventId;
    private Long userId;
    private Long courseId;
    private String title;
    private String eventType;
    private Long assignmentId;
    private String startTime;
    private String endTime;
    private String recurringDays;
    private LocalDate startDate;
    private Boolean isRecurring;


    
    public Event(Long eventId, Long userId, Long courseId, String title, String eventType, Long assignmentId, LocalDate startDate, String startTime, String endTime , Boolean isRecurring, String recurringDays){
        this.eventId = eventId;
        this.userId = userId;
        this.courseId = courseId;
        this.title = title;
        this.eventType = eventType;
        this.assignmentId = assignmentId;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isRecurring = isRecurring;
        this.recurringDays = recurringDays;

    }
    

    public List<String> getLectureDays(){

        List<String> lecture_day_list = new ArrayList<>();

        //seperate days by comma
        String[] days = recurringDays.split(",");
        for (String day : days) {
            day = day.trim();
            switch (day) {
                case "Monday":
                    lecture_day_list.add("MO");
                    break;
                case "Tuesday":
                    lecture_day_list.add("TU");
                    break;
                case "Wednesday":
                    lecture_day_list.add("WE");
                    break;
                case "Thursday":
                    lecture_day_list.add("TH");
                    break;
                case "Friday":
                    lecture_day_list.add("FR");
                    break;
                case "Saturday":
                    lecture_day_list.add("SA");
                    break;
                case "Sunday":
                    lecture_day_list.add("SU");
                    break;
                default:
                    break;
            }
        }

        return lecture_day_list;
        
    }

    public String getEventType(){
        return eventType;
    }

    public String getTitle(){
        return title;
    }

    public Boolean isRecurring(){
        return isRecurring;
    }

    //convert start_date and start_time to LocalDateTime
    public LocalDateTime getStartLocalDateTime(){

        //create LocalTime 
        String[] hour_min = startTime.split(":");
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
        return LocalDateTime.of(this.startDate, time);

        
    }

    //convert start_date and end_time to LocalDateTime
    public LocalDateTime getEndLocalDateTime(){

        //create LocalTime 
        String[] hour_min = endTime.split(":");
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
        return LocalDateTime.of(this.startDate, time);

    }

    public Long getEventId(){
        return this.eventId;
    }

    public Long getUserId(){
        return this.userId;
    }
    
    public Long getCourseId(){
        return this.courseId;
    }

    public Long getAssignmentId(){
        return this.assignmentId;
    }

   

    
}
