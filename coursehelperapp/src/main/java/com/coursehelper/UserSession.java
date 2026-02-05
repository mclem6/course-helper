package com.coursehelper;

import com.coursehelper.model.User;

//singleton to save user info for session

public class UserSession {

    private static UserSession instance; 

    private final User user;

    private UserSession(User user){
        this.user = user;
    }

    public static void init(User user){
        if(instance == null) {
            instance = new UserSession(user);
        }
    }

    public static UserSession getInstance() {
         if (instance == null) {
            throw new IllegalStateException("UserSession not initialised—call init() first");
        }
        return instance;
    }

    public static void clear() {
        instance = null;
    }

    public int getUserId() { return user.id; }
    public String getUserName() { return user.name; }
    public String getUsername() { return user.username; }

    
}
