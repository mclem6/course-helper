package com.coursehelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//class for connecting to local database
public class Database{

    final static String DATABASE_PATH = "jdbc:sqlite:courseHelper.db";

    //get connections to DB
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(DATABASE_PATH);
    }
   

}
