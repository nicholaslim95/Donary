package com.example.donary;

public class UserProfile {
    public String userEmail;
    public String userName;
    public String uid;

    public UserProfile(){

    }
    public UserProfile( String userEmail, String userName, String uid) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.uid = uid;
    }

    public UserProfile (String userEmail, String userName) {
        this.userEmail = userEmail;
        this.userName = userName;
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

    public String getUserID() { return uid; }

    public void setUserID(String userID) { this.uid = userID; }
}
