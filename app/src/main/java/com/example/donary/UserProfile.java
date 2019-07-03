package com.example.donary;

public class UserProfile {
    public String userEmail;
    public String userName;
    public String userID;

    public UserProfile(){

    }
    public UserProfile( String userEmail, String userName, String userID) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.userID = userID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() { return userID; }

    public void setUserID(String userID) { this.userID = userID; }
}
