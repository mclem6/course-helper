package com.coursehelper;


import java.util.ArrayList;
import java.util.List;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.DayViewBase;
import com.coursehelper.dao.EventDAO;
import com.coursehelper.model.Course;
import com.coursehelper.model.Event;

public class CalendarManager{

    private CalendarSource calendarSource;
    private List<Calendar<Integer>> calendars;
    EventDAO eventDAO;
    UserSession userSession;

    private static CalendarManager instance;

    public static CalendarManager getInstance() {
        if (instance == null) {
            instance = new CalendarManager();
        }
        return instance;
    }


    //constructor 
    private CalendarManager(){
        calendarSource = new CalendarSource("My Calendar");
        eventDAO = EventDAO.getInstance();
        userSession =  UserSession.getInstance();
        calendars = new ArrayList<>();
    }



    //populate calendar for given view
    public void populateCalendar(List<Course> user_courses, DayViewBase view){

        if (user_courses != null){
            //for each course
            for (Course course : user_courses){ 
                //add entries to calendar
                addCourseCalendar(course);  
            }   

        }
            
        //add to calendar source
        view.getCalendarSources().addAll(getCalendarSource());

    }
    


    //add course class days entyr to calendar 
    public void addCourseCalendar(Course course){

        if(findCourseCalendar(course) != null){
            return;
        }

        //create a calendar
        Calendar<Integer> courseCal = new Calendar<>(course.getCourseName());
        //add calendar to calendars list
        calendars.add(courseCal);
        
        //get events in event table belonging to user and course_id
        List<Event> courseEvents = eventDAO.getEventsByCourse(userSession.getUserId(), course.getCourseId());
         
        
        // set color, user select color for course in course form
        courseCal.setStyle(Calendar.Style.valueOf(course.getCourseStyle())); 

        //add to calendar source 
        calendarSource.getCalendars().addAll(courseCal);

        for (Event event : courseEvents){
            addEvent(course, event);
        }


    }

    public void addEvent(Course course, Event event){

        //create entry 
        Entry<Object> entry = new Entry<>(event.getTitle());
        //set time
        entry.setInterval(event.getStartLocalDateTime(),event.getEndLocalDateTime());
        //set User Object to retrieve event id
        entry.setUserObject(event.getEventId());

        //check if event is recurring
        if(event.isRecurring()){

            //check if lectures:
            if(event.getEventType().equals("lecture")){
                List<String> lecture_days_list = event.getLectureDays();
                //convert class days list to string
                String lecture_days_string = String.join(",", lecture_days_list);
                //set recurrence rule
                entry.setRecurrenceRule("RRULE:FREQ=WEEKLY;BYDAY=" + lecture_days_string);

            }

            //TODO:// not lecture but recurring (assignment, labs, etc..)
            else{

            }

        
        }



        //find calendar
        Calendar<Integer> courseCal = findCourseCalendar(course);


        //add entry
        courseCal.addEntry(entry);
        entry.setUserObject(event.getEventId());
        System.out.println(entry.getUserObject());
        

    }

    public void deleteEntry(Entry<String> entry){
        //TODO: delete single calendar entry

    }

    public void deleteCalendar(Course course){
        //find calendar
        Calendar<Integer> calToDelete = findCourseCalendar(course);

        //remove from souce
        calendarSource.getCalendars().remove(calToDelete);
       
    }


    public CalendarSource getCalendarSource(){
        return calendarSource;
    }

    public Calendar<Integer> findCourseCalendar(Course course){
        for (Calendar<Integer> cal : calendars){
            if(course.getCourseName().equals(cal.getName())){
                return cal;
            }
        }
        return null;
    }

}