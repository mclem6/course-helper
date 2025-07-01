package com.coursehelper;

import java.util.List;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.coursehelper.dao.EventDAO;

public class CalendarManager{

    private CalendarSource calendarSource;
    private List<Calendar> calendars;
    EventDAO eventDAO;
    UserSession userSession;


    //constructor 
    public CalendarManager(){
        calendarSource = new CalendarSource("My Calendar");
        eventDAO = EventDAO.getInstance();
        userSession =  UserSession.getInstance();
    }

    public void addEntry(Calendar courseCal, Course course){

        //add entries to calendar
                //get events in event table belonging to user and course_id
                List<Event> courseEvents = eventDAO.getEventsByCourse(userSession.getUserId(), course.getCourseId());

                for (Event event : courseEvents){

                    //create entry 
                    Entry<String> entry = new Entry<>(event.getTitle());
                    //set time
                    entry.setInterval(event.getStartLocalDateTime(),event.getEndLocalDateTime());

                    //check if event is class, add frequency
                    if(event.getEventType() == "class"){
                        //get class days
                        List class_days_list = event.getClassDays();
                        String class_days_string = String.join(",", class_days_list);
                        entry.setRecurrenceRule("RRULE:FREQ=WEEKLY;BYDAY=" + class_days_string);


                    }

                    //add entry
                    courseCal.addEntry(entry);



                }


    }


    public CalendarSource getCalendarSource(){
        return calendarSource;
    }
    



}