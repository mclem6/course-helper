package com.coursehelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtils{

    //register user

    public static int registerUser(String username, String password){

        //address of database 
        String url = "jdbc:sqlite:courseHelper.db"

        //connect to datase, created if doesn't exist
        //returns connection object -- live connection to database
        try(Connection conn = DriverManager.getConnection(url)){
            if (conn != null){
                //create user table if doesn't exist
                Statement stmt = conn.createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, name TEXT)");
                System.out.println("Database created " + username + password + "entered in table");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        
    


    
        

    }


}
