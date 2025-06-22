package com.coursehelper.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.coursehelper.Course;
import com.coursehelper.Database;

public class CourseDAO {

    public static int COURSE_ADD_SUCCESSFULL = 1;
    public static int COURSE_ADD_ERROR = -1;
    public static List<Course> NO_COURSES_FOUND = null;


    public CourseDAO(){

        createCourseTableIfNotExists();

    }

    private void createCourseTableIfNotExists(){

        String sql = "CREATE TABLE IF NOT EXISTS courses ( id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, semester TEXT NOT NULL, year INTEGER, start_time TEXT NOT NULL, end_time TEXT, class_days STRING, user_id INT NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";

         try(Connection conn = Database.getConnection()){
            //create user table if doesn't exist
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            System.out.println("courses table initialized");
        } catch(SQLException e){
            System.out.println("Database error: " + e.getMessage());
        }




    }

    //add course
    public int add_course(String name, String semester, int year, String start_time, String end_time, String class_days, int user_id){

        //create sql query string
        String sql = "INSERT INTO courses (name, semester, year, start_time, end_time, class_days, user_id) VALUES(?, ?, ?, ?, ?, ?, ?)";

        //connect to database
         try(Connection conn = Database.getConnection()){
            
            //check if connection is not null
            if(conn != null){
                //prepare statement for inserting to courses table
                PreparedStatement psmt = conn.prepareStatement(sql);

                psmt.setString(1, name); 
                psmt.setString(2, semester); 
                psmt.setInt(3, year); 
                psmt.setString(4, start_time); 
                psmt.setString(5, end_time); 
                psmt.setString(6, class_days); 
                psmt.setInt(7, user_id); 
                
                //execute statement and check if successful
                if(psmt.executeUpdate() == 1){
                    System.out.println("course added");
                    return COURSE_ADD_SUCCESSFULL;
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

    //get user's courses' ids 
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

                //check if courses were found
                if(rs != null){
                    System.out.println("courses found");

                    //create course list to return 
                    List <Course> courses_ids_list = new ArrayList<>();

                    //go through all rows
                    while(rs.next()){
                        //create course object
                        Course course = new Course(rs.getInt("course_id"), rs.getString("course_name"));
                        courses_ids_list.add(course);
                    }
                    
                    return courses_ids_list;

                } else {
                    System.out.println("no courses found");
                    return NO_COURSES_FOUND;
           }

            }

        } catch(SQLException e){
            System.out.println("Database error: " + e.getMessage());
        }


        return NO_COURSES_FOUND;
        
    }

    //archive course?


    
}
