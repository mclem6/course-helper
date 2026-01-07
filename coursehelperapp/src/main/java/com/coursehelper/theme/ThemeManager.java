package com.coursehelper.theme;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.coursehelper.Database;

import javafx.scene.Parent;


public class ThemeManager {
 
    private static final String LOCAL_THEME_FILE = System.getProperty("user.home") + "/.coursehelper/theme.txt";
    private static String currentTheme = "LightMode"; 
    private static int currentUserId = -1;

    //list of all stylesheets
    private static final String[] STYLESHEETS = {
        "homePage.css",
        "calendarPage.css",
        "coursesPage.css",
        "tasksPage.css",
        "navigation.css"
    };

    //for access page theme control 
    public static void initializeGuest() {
        currentUserId = -1;
        loadLocalThemePreference();

    }

    public static void initialize(int userId){
        currentUserId = userId;
        loadThemePreference(userId);
    }

    // Load from local file for access screen
    public static void loadLocalThemePreference() {
        try {
            if (Files.exists(Paths.get(LOCAL_THEME_FILE))) {
                String theme = new String(Files.readAllBytes(Paths.get(LOCAL_THEME_FILE))).trim();
                currentTheme = theme;
                System.out.println(currentTheme);
            }
        } catch (IOException e) {
            System.out.println("Error loading local theme: " + e.getMessage());
        }
    }

    //Save to local file preference in access screen
    public static void saveLocalThemePreference(String theme){
        try {
            //create directory if doesnt exist
            Files.createDirectories(Paths.get(System.getProperty("user.home"), ".coursehelper"));
            //write to file
            Files.write(Paths.get(LOCAL_THEME_FILE), theme.getBytes());
            currentTheme = theme;
        } catch (Exception e) {
            System.out.println("Error saving local theme: " + e.getMessage());
        }

    }

    //load user's preference
    public static void loadThemePreference(int userId){
        //connect to database and get user's theme
         try(Connection conn = Database.getConnection()){
            PreparedStatement psmt = conn.prepareStatement("SELECT theme FROM users WHERE id = ?");
            psmt.setInt(1, userId); 
            ResultSet rs = psmt.executeQuery();

            if(rs.next()){
                currentTheme = rs.getString("theme");
            }  


        } catch(SQLException e){
            System.out.println("Database error: " + e.getMessage());
        }
    }

    //save user's preference 
    public static void saveThemePreference(String theme){

        currentTheme = theme;

        //connect to database and set user's theme
         try(Connection conn = Database.getConnection()){
            PreparedStatement psmt = conn.prepareStatement("UPDATE users SET theme = ? WHERE id = ?");
            psmt.setString(1, theme);
            psmt.setInt(2, currentUserId); 
            psmt.executeUpdate();

        } catch(SQLException e){
            System.out.println("Database error: " + e.getMessage());
        }
    }

    //set theme on all sheets  
    public static void setTheme(Parent parent, String theme) {

        if (parent == null || theme == null) {
            return;
        }
        
        currentTheme = theme;
        parent.getStylesheets().clear();

        //get each sheet's path and set new theme 
        for (String stylesheet : STYLESHEETS) {
            String path = getThemeStyleSheet(theme, stylesheet);
            if (path != null) {
                parent.getStylesheets().add(path);
            }
        }

    }

    public static void setAccessScreenTheme(Parent parent, String theme) {
        currentTheme = theme;
        parent.getStylesheets().clear();
        String path = getThemeStyleSheet(theme, "accessPage.css");
        if (path != null) {
            parent.getStylesheets().add(path);
        }
    }


    //return css path for stylesheet
    public static String getThemeStyleSheet(String theme, String stylesheetName){
        try {
            String path = "/stylesheets/" + theme + "/" + stylesheetName;
            return ThemeManager.class.getResource(path).toExternalForm();
            
        } catch (Exception e) {
            System.out.println("Stylesheet not found: " + stylesheetName);
            return null;
        }
    
    }

    //toggle between light/dark
    public static String toggleTheme (Parent parent){
        String newTheme = currentTheme.equals("LightMode") ? "DarkMode" : "LightMode";
        setTheme(parent, newTheme);
        return newTheme;
    }

    //getter for currentTheme
    public static String getCurrentTheme() {
        return currentTheme;
    }

    public static void setCurrentTheme(String newTheme){
        currentTheme = newTheme;
    }

    public static boolean isDarkMode(){
        return currentTheme.equals("DarkMode");
    }


 
}


