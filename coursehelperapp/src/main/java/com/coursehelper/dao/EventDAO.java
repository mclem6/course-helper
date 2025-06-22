package com.coursehelper.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.coursehelper.Database;

public class EventDAO{



    public EventDAO(){

        createEventTableIfNotExists();

    }

    private void createEventTableIfNotExists(){

        String sql = "CREATE TABLE IF NOT EXISTS events ( id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INT NOT NULL, title TEXT, event_type TEXT NOT NULL, start_time DATETIME NOT NULL, end_time DATETIME, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";

         try(Connection conn = Database.getConnection()){
            //create user table if doesn't exist
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch(SQLException e){
            System.out.println("Database error: " + e.getMessage());
        }




    }

    //add event to course

    //get event for course

    //delete event

    //edit event









}