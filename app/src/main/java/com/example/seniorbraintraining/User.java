package com.example.seniorbraintraining;

import java.util.ArrayList;

public class User {

    private  String name;
    private String email;
    private String password;
    private int unlockedLevels;

    public User(){

    }

    public User(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
        this.unlockedLevels = 1;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword(){
        return password;
    }

    public int getUnlockedLevels(){
        return unlockedLevels;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setUnlockedLevels(int unlockedLevels){
        this.unlockedLevels =  unlockedLevels;
    }


    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
