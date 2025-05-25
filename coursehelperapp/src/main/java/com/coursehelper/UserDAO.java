package com.coursehelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


//class for accessing database (Data Access Object)
public class UserDAO {

    final int USER_DOESNOTEXIST = 0;
    final int USER_EXISTS = 1;
    final int ERROR = -1;

    public UserDAO(){
        
        try(Connection conn = Database.getConnection()){
            //create user table if doesn't exist
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, name TEXT)");
        } catch(SQLException e){
            System.out.println("Database error: " + e.getMessage());
        }

    }


    //register user
    public int registerUser(String username, String password){


        //connect to datase, create if doesn't exist
        try(Connection conn = Database.getConnection()){

            //connection successful
            if (conn != null){



                //check if user exists
                if(is_username_taken(username) == 1){
                    //username is taken 
                    return USER_EXISTS;
                }

                //insert user
                PreparedStatement psmt = conn.prepareStatement("INSERT INTO users (username, password, name) VALUES(?, ?, ?)");
                psmt.setString(1, username);            
                psmt.setString(2, password);  
                psmt.setString(3, "Jane Doe");  
                
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
    public int is_username_taken(String username){

        try( Connection conn = Database.getConnection();
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


    
    public int findUser(String username, String password){


        try( Connection conn = Database.getConnection();
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


    
    public int get_userId(String username, String password){

        //check if user exists, return user id if yes, otherwise return error
        if(findUser(username, password) == USER_EXISTS){
            //query for user's id
            try( Connection conn = Database.getConnection();
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

    public String get_userName(String username, String password){

         //check if user exists, return user id if yes, otherwise return error
        if(findUser(username, password) == USER_EXISTS){
            //query for user's name
            try( Connection conn = Database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT name FROM users WHERE username = ? AND password = ? LIMIT 1");
           ){

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                String userName = rs.getString("name");
                System.out.println("user name is " + userName);
                return userName;
            } 
            else {
                System.out.println("User not found.");
            }

           } catch(SQLException e){
                System.out.println("Database error: " + e.getMessage());
           }

        }

        //database
        return username; 
        

    }

    
}
