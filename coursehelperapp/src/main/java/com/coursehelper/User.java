package com.coursehelper;

//model class for holding user data

public class User {

    int id;
    String name;
    String username;
    
    public User(int id, String name, String username){
        this.id = id;
        this.name = name;
        this.username = username;

    }

    public int getId(){ return id;}
    public String getName() { return name;}
    public String getUsername() { return username;}
    
}
