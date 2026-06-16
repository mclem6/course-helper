package com.coursehelper.frontend;


import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.DayViewBase;
import com.coursehelper.frontend.model.Course;
import com.coursehelper.frontend.model.Event;


public class CalendarManager{

    private CalendarSource calendarSource;
    private static CalendarManager instance;
    private UserStore userStore;
    private List<Course> courses;

    public static CalendarManager getInstance() {
        if (instance == null) {
            instance = new CalendarManager();
        }
        return instance;
    }


    public void initialize(){
        userStore = UserStore.getInstance();

        courses = userStore.getCourses();
        Map<Long, List<Event>> eventsByCourse = userStore.getEvents();
        calendarSource.getCalendars().clear();
        
        if (courses != null){
            //for each course
            for (Course course : courses){ 
                //add entries to calendar
                addCourseCalendar(course);  
            }   

            //load all persisted events into shared calendar source
            for (Course course : courses) {
                List<Event> courseEvents = eventsByCourse.get(course.getId());
                if (courseEvents != null) {
                    loadEventsForCourse(course, courseEvents);
                }
            }
        }
    }

    //constructor 
    private CalendarManager(){
        calendarSource = new CalendarSource("My Calendar");
    }



    //populate calendar for given view
    public void populateCalendar(DayViewBase view){

        if (courses != null){
            //for each course
            for (Course course : courses){ 
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
        Calendar<Integer> courseCal = new Calendar<>(course.getName());
        //add calendar to calendars list
        calendarSource.getCalendars().add(courseCal);


        // set color, user select color for course in course form
        courseCal.setStyle(Calendar.Style.valueOf(course.getStyle())); 


    }


    public void loadEventsForCourse(Course course, List<Event> events){
        if (events == null || events.isEmpty()) {
            return;
        }
        for (Event event : events){
            addEvent(course, event);
        }
    }



    public void addEvent(Course course, Event event){
        //create entry 
        Entry<Object> entry = new Entry<>(event.getTitle());

        //set time
        entry.setInterval(event.getStartLocalDateTime(),event.getEndLocalDateTime());

        //check if event is recurring
        if(event.isRecurring()){

            List<String> lectureDays = event.getLectureDays();
            if (!lectureDays.isEmpty()) {

                java.time.LocalDate untilDate = course.getEndDate();
                if (untilDate == null && userStore.getSettings() != null) {
                    untilDate = userStore.getSettings().getEndDate();
                }

                if (untilDate != null) {
                    String until = untilDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                    entry.setRecurrenceRule("RRULE:FREQ=WEEKLY;BYDAY=" +
                        String.join(",", lectureDays) + ";UNTIL=" + until);
                } else {
                    entry.setRecurrenceRule("RRULE:FREQ=WEEKLY;BYDAY=" +
                        String.join(",", lectureDays));
                } 
            }

        }

        //find calendar
        Calendar<Integer> courseCal = findCourseCalendar(course);

        //add entry
        courseCal.addEntry(entry);

        //set entry id to event id
        entry.setUserObject(event.getEventId());
    }

    public void deleteEntry(Entry<String> entry){

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
        for (Calendar<Integer> cal : calendarSource.getCalendars()){
            if(course.getName().equals(cal.getName())){
                return cal;
            }
        }
        return null;
    }


    public void setCourses(List<Course> courses){
        this.courses = courses;
    }

    public List<Course> getCourses(){
        return courses;
    }

}
