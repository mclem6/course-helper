package com.coursehelper.frontend;


import com.coursehelper.frontend.model.User;

// //singleton to save user info for session

public class UserSession {

    private static String accessToken;
    private static User user;
    private static String pendingGreeting;


    public static void setSession(String token, User u){
        accessToken = token;
        user = u;
    }

    public static String getToken(){
        return accessToken;
    }

    public static User getUser(){
        return user;
    }

    public static void setPendingGreeting(String greeting) { pendingGreeting = greeting; }
    public static String getPendingGreeting() { return pendingGreeting; }

    public static void clear(){
        accessToken = null;
        user = null;
        pendingGreeting = null;
    }

    public static boolean isLoggedIn(){
        return accessToken != null;
    }


    
}
