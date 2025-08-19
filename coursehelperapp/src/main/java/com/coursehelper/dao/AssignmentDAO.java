package com.coursehelper.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import com.coursehelper.Database;

public class AssignmentDAO {


    public static String SOURCE_TYPE = "assignment";
    public static String ASSIGNMENT_HOMEWORK = "homework";
    public static String ASSIGNMENT_PROJECT = "project";
    public static String ASSIGNMENT_OTHER= "other";

    static AssignmentDAO instance;
    

    private AssignmentDAO(){
        createEventTableIfNotExists();
    }

    public static AssignmentDAO getInstance(){
        if (instance == null) {
            instance = new AssignmentDAO();
        }
        return instance;
    }

    private void createEventTableIfNotExists(){

        final String sql = "CREATE TABLE IF NOT EXISTS assignments (" +
             "assignment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
             "user_id INTEGER NOT NULL, " +
             "course_id INTEGER NOT NULL, " +
             "title TEXT, " +
             "event_type TEXT NOT NULL, " +
             "due_date DATE, " +
             "due_time TEXT NOT NULL, " +
             "is_completed BOOLEAN Default 0," +
             "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
             "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE," +
             "FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE" +
             ");";

         try(Connection conn = Database.getConnection()){
            //create user table if doesn't exist
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
                System.out.println("Assignments table initialized");
        } catch(SQLException e){
            System.out.println("Database error: " + e.getMessage());
        }


    }

    //add event to database 
    public int addAssignment(int user_id, String title, String event_type, int course_id, LocalDate due_date, String due_time){

        //connect to database
        try ( Connection conn = Database.getConnection();
        ){
            if(conn != null){

                //prepare string to insert event
                PreparedStatement psmt = conn.prepareStatement("INSERT INTO assignments (user_id, title, event_type, course_id, due_date, due_time) VALUES(?, ?, ?, ?, ?, ?)");

                psmt.setInt(1, user_id);  
                psmt.setString(2, title);      
                psmt.setString(3, event_type);  
                psmt.setInt(4, course_id);    
                psmt.setDate(5, Date.valueOf(due_date));  
                psmt.setString(6, due_time);    
                

                //insert into event database
                if(psmt.executeUpdate() == 1){
                    System.out.println("assignment added");

                    //return course_id
                    ResultSet rs = psmt.getGeneratedKeys();
                    if (rs.next()) {
                        return rs.getInt(1); 
                    }

                } else {
                    System.out.println("assignment not added");
                }


            }

        }
        catch (SQLException e){
            System.out.println("Database error: " + e.getMessage());
        }

        return -1;

    }

    // //get event for course
    // public List<Event> getEventsByCourse(int userId, int courseId){

    //     //query database for event beloning to user for specified course 
    //     //connect to databse
    //     try(Connection conn = Database.getConnection();
    //     ){
    //         if(conn != null){
    //             //prepare statement for query
    //             PreparedStatement psmt = conn.prepareStatement("SELECT * FROM events WHERE user_id = ? AND course_id = ? ");
    //             psmt.setInt(1, userId);
    //             psmt.setInt(2, courseId);

    //             //execute query
    //             ResultSet rs = psmt.executeQuery();

    //             //init list for return
    //             List<Event> courseEvents = new ArrayList<>();
            
    //             while(rs.next()){
    //                 courseEvents.add(new Event(rs.getInt("course_id"), rs.getString("title"), rs.getString("event_type"), rs.getDate("start_date").toLocalDate(),
    //                                             rs.getString("start_time"), rs.getString("end_time"), rs.getString("days")));
    //             }

    //             return courseEvents;
    //         }

    //     }
    //     catch (SQLException e){
    //         System.out.println("Database error: " + e.getMessage());
    //     }

    //     return null;
        
    // }


    //delete event

    //edit event



}
    

