package com.coursehelper.frontend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.coursehelper.frontend.model.Assignment;
import com.coursehelper.frontend.model.Course;
import com.coursehelper.frontend.model.Event;
import com.coursehelper.frontend.model.Task;
import com.coursehelper.frontend.model.UserSettings;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserStore {

    private static final UserStore instance = new UserStore();
    private List<Course> courses = new ArrayList<>();
    private Map<Long, List<Event>> eventsByCourse = new HashMap<>();
    private Map<Long, List<Assignment>> assignmentsByCourse = new HashMap<>();
    private Map<Long, List<Task>> tasksByCourse = new HashMap<>();
    private UserSettings settings;
    private final ObjectProperty<byte[]> profilePicture = new SimpleObjectProperty<>();


    private final ObservableList<Assignment> upcomingAssignments = 
        FXCollections.observableArrayList();


    public static UserStore getInstance() {
        return instance;
    }
    

    public void refreshCourses(List<Course> userCourses){
        setCourses(userCourses);
    }

    public void refreshEvents(Map<Long, List<Event>> userEvents){
        setEvents(userEvents);
    }

    public void addCourse(Course course){
        courses.add(course);
    }

    public void addAssignment(Assignment assignment, Long courseId){
        assignmentsByCourse.computeIfAbsent(courseId, id -> new ArrayList<>())
        .add(assignment);
        upcomingAssignments.add(assignment);
    }

    public void addTask(Task task, Long courseId){
        tasksByCourse.computeIfAbsent(courseId, id -> new ArrayList<>())
        .add(task);
    }

    public void addEvent(Event event, Long courseId){
        eventsByCourse.computeIfAbsent(courseId, id -> new ArrayList<>())
        .add(event);

    }

    public Long findCourseIdByName(String courseName){

        for (Course course : courses){
            if (course.getName().equals(courseName)){
                return course.getId();
            }
        }

        return null;
    }

    public void setCourses(List<Course> courses){
        this.courses = new ArrayList<>(courses);
    }

    public void setEvents(Map<Long, List<Event>> eventsByCourse){
        this.eventsByCourse = eventsByCourse;
    }

    public void setAssignments(Map<Long, List<Assignment>> assignmentsByCourse){
        this.assignmentsByCourse = new HashMap<>();
        
        if(assignmentsByCourse != null){
            this.assignmentsByCourse.putAll(assignmentsByCourse);
        }

        upcomingAssignments.clear();
        assignmentsByCourse.values().forEach(upcomingAssignments::addAll);
    }


    public void setTasks(Map<Long, List<Task>> tasksByCourse){
        this.tasksByCourse = new HashMap<>();
        
        if(tasksByCourse != null){
            this.tasksByCourse.putAll(tasksByCourse);
        }

    }

    public void setSettings(UserSettings settings){
        this.settings = settings;
    }

    public void setProfilePicture(byte[] bytes) {
         profilePicture.set(bytes);
    }

    public ObjectProperty<byte[]> profilePictureProperty() { 
        return profilePicture; 
    }

    public List<Course> getCourses() {
        return courses;
    }

    public Map<Long, List<Event>> getEvents(){
        return eventsByCourse;
    }


    public Map<Long, List<Assignment>> getAssignments(){
        return assignmentsByCourse;
    }

    public Map<Long, List<Task>> getTasks(){
        return tasksByCourse;
    }

    public ObservableList<Assignment> getUpcomingAssignments() {
        return upcomingAssignments;
    }

    public Course getCourseById(Long courseId){
        for (Course course : courses){
            if (course.getId().equals(courseId)){
                return course;
            }
        }

        return null;
    }

    public Event getEventById(Long eventId) {
        return eventsByCourse.values().stream()
            .flatMap(List::stream)
            .filter(e -> e.getEventId().equals(eventId))
            .findFirst()
            .orElse(null);
    }

    public Event getEventByAssignmentId(Long assignmentId) {
        return eventsByCourse.values().stream()
            .flatMap(List::stream)
            .filter(e -> assignmentId.equals(e.getAssignmentId()))
            .findFirst()
            .orElse(null);
    }

    public List<Event> getEventsByCourseId(Long courseId){
        return eventsByCourse.get(courseId);
    }

    // UserStore
    public void filterDataByCurrentCourses() {
        List<Long> currentCourseIds = courses.stream()
            .map(Course::getId)
            .toList();

        eventsByCourse = eventsByCourse.entrySet().stream()
            .filter(entry -> currentCourseIds.contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assignmentsByCourse = assignmentsByCourse.entrySet().stream()
            .filter(entry -> currentCourseIds.contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }



    public List<Assignment> getAssignmentsByCourseId(Long courseId){
        return assignmentsByCourse.get(courseId);
    }

    public void markAssignmentComplete(Long assignmentId) {
        assignmentsByCourse.values().forEach(list ->
            list.stream()
                .filter(a -> a.getId().equals(assignmentId))
                .findFirst()
                .ifPresent(a -> a.setStatus("COMPLETED"))
        );
        upcomingAssignments.stream()
            .filter(a -> a.getId().equals(assignmentId))
            .findFirst()
            .ifPresent(a -> a.setStatus("COMPLETED"));
    }

     public List<Task> getTaskssByCourseId(Long courseId){
        return tasksByCourse.get(courseId);
    }

    public UserSettings getSettings() { return settings; }

    public byte[] getProfilePicture() { return profilePicture.get(); }

    public void clear() {
        courses = new ArrayList<>();
        eventsByCourse = new HashMap<>();
        assignmentsByCourse = new HashMap<>();
        tasksByCourse = new HashMap<>();
        settings = null;
        profilePicture.set(null);
        upcomingAssignments.clear();
    }

}
