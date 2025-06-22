package com.coursehelper.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mindrot.jbcrypt.BCrypt; // Add this import

import com.coursehelper.Database;
import com.coursehelper.User;

//class for accessing database (Data Access Object)
public class UserDAO {

    public static final int USER_DOES_NOT_EXIST = 0;
    public static final int USER_EXISTS = 1;
    public static final int USER_REGISTERED = 99;
    public static final int ERROR = -1;

    public UserDAO(){
        createUserTableIfNotExists();
    }

    private void createUserTableIfNotExists(){
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
                if(isUsernameTaken(username) == 1){
                    //username is taken 
                    return USER_EXISTS;
                }

                // Encrypt the password using BCrypt
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                
                // TODO: REMOVE THIS DEBUG OUTPUT BEFORE PRODUCTION - SECURITY RISK
                System.out.println("Original password: " + password);
                System.out.println("Encrypted password: " + hashedPassword);

                //insert user
                PreparedStatement psmt = conn.prepareStatement("INSERT INTO users (username, password, name) VALUES(?, ?, ?)");
                psmt.setString(1, username);            
                psmt.setString(2, hashedPassword);  // Store encrypted password
                psmt.setString(3, username);  //TODO : fix after adding create account page
                
                if(psmt.executeUpdate() == 1){
                    System.out.println("user registered");
                    return USER_REGISTERED;
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
    public int isUsernameTaken(String username){
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
               return USER_DOES_NOT_EXIST;
           }
       }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return ERROR;
    }

    public int authUser(String username, String password){

        
        try( Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
            ){
            if (conn != null){

                //find user
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();

                //if user exists check password
                if(rs.next()){
                    String storedHashedPassword = rs.getString("password");
                    
                    // TODO: REMOVE THIS DEBUG OUTPUT BEFORE PRODUCTION - SECURITY RISK
                    System.out.println("Entered password: " + password);
                    System.out.println("Stored hashed password: " + storedHashedPassword);
                    
                    // Verify password using BCrypt
                    if(BCrypt.checkpw(password, storedHashedPassword)){
                        System.out.println("User exists in the database.");
                        System.out.println("user id: " + rs.getInt("id") );
                        return USER_EXISTS;
                    } 
                } 
                
                //user is not found
                else {
                    System.out.println("User not found.");
                    return USER_DOES_NOT_EXIST;
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return ERROR;
    }

    public User getUserByCredentials(String username, String password){
        
        try( Connection conn = Database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? LIMIT 1");
        ){
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
        if(rs.next()){
                //create User object and pass args
                return new User( rs.getInt("id"), rs.getString("name"), rs.getString("username"));
        } 
        else {
                return null;
        }
        } catch(SQLException e){
                System.out.println("Database error: " + e.getMessage());
        }
        
        return null;
        
    }
}