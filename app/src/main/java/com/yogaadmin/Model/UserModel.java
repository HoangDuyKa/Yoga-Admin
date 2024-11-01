package com.yogaadmin.Model;

public class UserModel {
    private String name,email,password,profile,role,userId;
    public UserModel() {
    }

    public UserModel(String userId,String name, String email, String password, String profile,String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    // Getters và Setters

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
