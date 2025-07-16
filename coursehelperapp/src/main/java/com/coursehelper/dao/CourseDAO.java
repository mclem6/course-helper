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

import com.coursehelper.Course;
import com.coursehelper.Database;

public class CourseDAO {

    public static int COURSE_ADD_SUCCESSFULL = 1;
    public static int COURSE_ADD_ERROR = -1;
    public static List<Course> NO_COURSES_FOUND = null;

    private static CourseDAO instance;


    private CourseDAO(){
        createCourseTableIfNotExists();
    }


    public static void init(){
        if(instance == null) {
            instance = new CourseDAO();
        }
    }

    public static CourseDAO getInstance() {
         if (instance == null) {
            throw new IllegalStateException("CourseDAO not initialised—call init() first");
        }
        return instance;
    }

    public static void clear() {
        instance = null;
    }

    private void createCourseTableIfNotExists(){

        final String sql = "CREATE TABLE IF NOT EXISTS courses (course_id INTEGER PRIMARY KEY AUTOINCREMENT, course_name TEXT, semester TEXT NOT NULL, year INTEGER, start_date DATE, start_time TEXT NOT NULL, end_time TEXT, class_days STRING, user_id INT NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";

         try(Connection conn = Database.getConnection()){
            //create user table if doesn't exist
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            System.out.println("courses table initialized");
        } catch(SQLException e){
            System.out.println("Database error: " + e.getMessage());
        }

    }

    //add course // returns course ID
    public int addCourse(String course_name, String semester, int year, LocalDate start_date, String start_time, String end_time, String class_days, int user_id){

        //create sql query string
        String sql = "INSERT INTO courses (course_name, semester, year, start_date, start_time, end_time, class_days, user_id) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        //connect to database
         try(Connection conn = Database.getConnection()){
            
            //check if connection is not null
            if(conn != null){
                //prepare statement for inserting to courses table
                PreparedStatement psmt = conn.prepareStatement(sql);

                psmt.setString(1, course_name); 
                psmt.setString(2, semester); 
                psmt.setInt(3, year); 
                psmt.setDate(4, Date.valueOf(start_date)); 
                psmt.setString(5, start_time); 
                psmt.setString(6, end_time); 
                psmt.setString(7, class_days); 
                psmt.setInt(8, user_id); 
                
                //execute statement and check if successful
                if(psmt.executeUpdate() == 1){
                    System.out.println("course added");

                    //return course_id
                    ResultSet rs = psmt.getGeneratedKeys();
                    if (rs.next()) {
                        return rs.getInt(1); 
}



                } else {
                    System.out.println("error adding course");
                }

            }

        } catch(SQLException e){
            System.out.println("Database error: " + e.getMessage());
        }

        return COURSE_ADD_ERROR;
    

    }


    //edit course

    //delete course

    //get user's courses by id
    public List<Course> getCoursesByUser(int user_id){

        //create sql query string
        String sql = "SELECT * FROM courses WHERE user_id = ?";
        //connect to database
        try(Connection conn = Database.getConnection()){
            
            //check if connection is not null
            if(conn != null){
                //prepare statement for query database
                PreparedStatement psmt = conn.prepareStatement(sql);
                psmt.setInt(1, user_id); 
                
                //execute statement and check if successful
                ResultSet rs = psmt.executeQuery();

                //create course list to return 
                List <Course> courses_ids_list = new ArrayList<>();

                //go through all rows
                while(rs.next()){
                    //create course object
                    Course course = new Course(rs.getInt("course_id"), rs.getString("course_name"));
                    courses_ids_list.add(course);
                }
                
                return courses_ids_list;
        
            }

        } catch(SQLException e){
            System.out.println("Database error: " + e.getMessage());
        }


        return NO_COURSES_FOUND;
        
    }



    //archive course?


    
}
