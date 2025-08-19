package com.coursehelper.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.coursehelper.Database;
import com.coursehelper.Event;

//DAO for access event table in database 
public class EventDAO{

    public static String EVENT_LECTURE = "lecture";
    public static String EVENT_LAB = "lab";
    public static String EVENT_DISCUSSION = "discussion";
    public static String EVENT_HOMEWORK = "homework";
    public static String EVENT_CUSTOM = "custom";
    public static int EVENT_DELETE_SUCCESSFUL = 1;
    public static int EVENT_DELETE_ERROR = -1;



    static EventDAO instance;
    

    private EventDAO(){
        createEventTableIfNotExists();
    }

    public static EventDAO getInstance(){
        if (instance == null) {
            instance = new EventDAO();
        }
        return instance;
    }

    private void createEventTableIfNotExists(){

        final String sql = "CREATE TABLE IF NOT EXISTS events (" +
             "event_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
             "user_id INTEGER NOT NULL, " +
             "course_id INTEGER NOT NULL, " +
             "title TEXT, " +
             "event_type TEXT NOT NULL, " +
             "start_date DATE, " +
             "start_time TEXT NOT NULL, " +
             "end_time TEXT, " +
             "recurring BOOLEAN Default 0, " +
             "days TEXT, " +
             "source_type TEXT NOT NULL, " +
             "source_id INTEGER, " +
             "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
             "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
             ");";

         try(Connection conn = Database.getConnection()){
            //create user table if doesn't exist
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
                System.out.println("events table initialized");
        } catch(SQLException e){
            System.out.println("Database error: " + e.getMessage());
        }




    }

    //add event to database 
    public int addEvent(int user_id, String title, String event_type, int course_id, LocalDate start_date, String start_time, String end_time, Boolean recurring, String days, String source_type, int source_id){

        //connect to database
        try ( Connection conn = Database.getConnection();
        ){
            if(conn != null){

                //prepare string to insert event
                PreparedStatement psmt = conn.prepareStatement("INSERT INTO events (user_id, title, event_type, course_id, start_date, start_time, end_time, recurring, days, source_type, source_id) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

                psmt.setInt(1, user_id);  
                psmt.setString(2, title);      
                psmt.setString(3, event_type);  
                psmt.setInt(4, course_id);    
                psmt.setDate(5, Date.valueOf(start_date));  
                psmt.setString(6, start_time);    
                psmt.setString(7, end_time);    
                psmt.setBoolean(8, recurring);
                psmt.setString(9, days);   
                psmt.setString(10, source_type);  
                psmt.setInt(11, source_id);    

                //insert into event database
                if(psmt.executeUpdate() == 1){
                    System.out.println("event added");
                    ResultSet rs = psmt.getGeneratedKeys();
                    return rs.getInt(1);
                } else {
                    System.out.println("event not added");
                    return -1;
                }


            }

        }
        catch (SQLException e){
            System.out.println("Database error: " + e.getMessage());
        }

        return -1;

    }

    //get event for course
    public List<Event> getEventsByCourse(int userId, int courseId){

        //query database for event beloning to user for specified course 
        //connect to databse
        try(Connection conn = Database.getConnection();
        ){
            if(conn != null){
                //prepare statement for query
                PreparedStatement psmt = conn.prepareStatement("SELECT * FROM events WHERE user_id = ? AND course_id = ? ");
                psmt.setInt(1, userId);
                psmt.setInt(2, courseId);

                //execute query
                ResultSet rs = psmt.executeQuery();

                //init list for return
                List<Event> courseEvents = new ArrayList<>();
            
                while(rs.next()){
                    courseEvents.add(new Event(rs.getInt("event_id"), rs.getInt("course_id"), rs.getString("title"), rs.getString("event_type"), rs.getDate("start_date").toLocalDate(),
                                                rs.getString("start_time"), rs.getString("end_time"), rs.getBoolean("recurring"), rs.getString("days")));
                }

                return courseEvents;
            }

        }
        catch (SQLException e){
            System.out.println("Database error: " + e.getMessage());
        }

        return null;
        
    }


    //delete event

    public int deleteEvent(int user_id, int event_id) {
        String sql = "DELETE FROM events WHERE user_id = ? AND event_id = ?";

        try (Connection conn = Database.getConnection()) {
            if (conn != null) {
                PreparedStatement psmt = conn.prepareStatement(sql);
                psmt.setInt(1, user_id);
                psmt.setInt(2, event_id);

                int rowsDeleted = psmt.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("event deleted");
                    return EVENT_DELETE_SUCCESSFUL;
                } else {
                    System.out.println("no event found to delete");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return EVENT_DELETE_ERROR;
    }

    //edit event









}