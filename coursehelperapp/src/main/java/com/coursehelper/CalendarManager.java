package com.coursehelper;

import java.util.ArrayList;
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
        calendars = new ArrayList<>();
    }

    public void addCourseCalendar(Course course){

        //create a calendar
        Calendar<String> courseCal = new Calendar<>(course.getCourseName());
        //add calendar to calendars list
        calendars.add(courseCal);
        
        //get events in event table belonging to user and course_id
        List<Event> courseEvents = eventDAO.getEventsByCourse(userSession.getUserId(), course.getCourseId());

        for (Event event : courseEvents){

            //create entry 
            Entry<String> entry = new Entry<>(event.getTitle());
            //set time
            entry.setInterval(event.getStartLocalDateTime(),event.getEndLocalDateTime());

            //check if event is class, add frequency
            if(event.getEventType().equals("class")){
                //get class days
                List<String> class_days_list = event.getClassDays();
                //convert class days list to string
                String class_days_string = String.join(",", class_days_list);
                //set recurrence rule
                entry.setRecurrenceRule("RRULE:FREQ=WEEKLY;BYDAY=" + class_days_string);
            
            }

            //add entry
            courseCal.addEntry(entry);



        }

        // public void addEvent(){


        // }

        // set color, user select color for course in course form
        courseCal.setStyle(Calendar.Style.valueOf(course.getCourseStyle())); 

        //add to calendar source 
        calendarSource.getCalendars().addAll(courseCal);


    }

    public void deleteEntry(Entry entry){
        //TODO: delete single calendar entry

    }

    public void deleteCalendar(Course course){
        //find calendar
        Calendar calToDelete = findCourseCalendar(course);

        //remove from souce
        calendarSource.getCalendars().remove(calToDelete);
       
    }


    public CalendarSource getCalendarSource(){
        return calendarSource;
    }

    public Calendar findCourseCalendar(Course course){
        for (Calendar cal : calendars){
            if(course.getCourseName().equals(cal.getName())){
                return cal;
            }
        }
        return null;

    }
    



}