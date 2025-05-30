package com.coursehelper;

//singleton to save user info for session

public class UserSession {

    private static UserSession instance; 

    private int userId;
    private String userName;
    private String username;

    private UserSession(int userId, String userName, String username){
        this.userId = userId;
        this.userName = userName;
        this.username = username;
    }

    public static void init(int userId, String name, String username){
        if(instance == null) {
            instance = new UserSession(userId, name, username);
        }
    }

    public static UserSession getInstance() {
        return instance;
    }

    public static void clear() {
        instance = null;
    }

    public int getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getUsername() { return username; }

    
}
