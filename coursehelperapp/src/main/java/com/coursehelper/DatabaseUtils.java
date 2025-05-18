package com.coursehelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;




public class DatabaseUtils{


    final static String DATABASE_PATH = "jdbc:sqlite:courseHelper.db";
    final static int USER_DOESNOTEXIST = 0;
    final static int USER_EXISTS = 1;
    final static int ERROR = -1;


    //register user
    public static int registerUser(String username, String password){


        //connect to datase, create if doesn't exist
        try(Connection conn = DriverManager.getConnection(DATABASE_PATH)){

            //connection successful
            if (conn != null){

                //create user table if doesn't exist
                Statement stmt = conn.createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)");
                

                //check if user exists
                if(is_username_taken(username) == 1){
                    //username is taken 
                    return USER_EXISTS;
                }

                //insert user
                PreparedStatement psmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES(?, ?)");
                psmt.setString(1, username);            
                psmt.setString(2, password);  
                
                if(psmt.executeUpdate() == 1){
                    System.out.println("user registered");
                    //return id;
                } else {
                    System.out.println("user cannot be registered");
                }



    
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return ERROR;
        
 

        
    }

    //check if username is taken
    public static int is_username_taken(String username){

        try( Connection conn = DriverManager.getConnection(DATABASE_PATH);
        PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM users WHERE username = ? ");
       ){
       if (conn != null){

           stmt.setString(1, username);

           ResultSet rs = stmt.executeQuery();

           if(rs.next()){
               System.out.println("username taken.");
               return USER_EXISTS;
           } else {
               System.out.println("username is available.");
               return USER_DOESNOTEXIST;
           }

           
       }


        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return ERROR;

    }


    
    public static int findUser(String username, String password){


        try( Connection conn = DriverManager.getConnection(DATABASE_PATH);
             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM users WHERE username = ? AND password = ? LIMIT 1");
            ){
            if (conn != null){

                stmt.setString(1, username);
                stmt.setString(2, password);


                ResultSet rs = stmt.executeQuery();

                if(rs.next()){
                    System.out.println("User exists in the database.");
                    System.out.println("user id: " + rs.getInt("id") );
                    return USER_EXISTS;
                } else {
                    System.out.println("User not found.");
                    return USER_DOESNOTEXIST;
                }

                
            }


        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return ERROR;

    }


    
    public static int get_userId(String username, String password){

        //check if user exists, return user id if yes, otherwise return error
        if(findUser(username, password) == USER_EXISTS){
            //query for user's id
            try( Connection conn = DriverManager.getConnection(DATABASE_PATH);
                 PreparedStatement stmt = conn.prepareStatement("SELECT id FROM users WHERE username = ? AND password = ? LIMIT 1");
           ){

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                int userId = rs.getInt("id");
                System.out.println("user id is " + userId);
                return userId;
            } 
            else {
                System.out.println("User not found.");
            }

           } catch(SQLException e){
                System.out.println("Database error: " + e.getMessage());
           }

        }
        

        return ERROR;
    }


}
