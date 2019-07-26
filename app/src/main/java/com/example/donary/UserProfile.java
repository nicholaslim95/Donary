package com.example.donary;

public class UserProfile {
    public String userEmail;
    public String userName;
    public String uid;
    public String writeSomethingAboutYourself;
    public UserProfile(){

    }
    public UserProfile(String userName, String uid) {
        this.userName = userName;
        this.uid = uid;
        this.writeSomethingAboutYourself="Write something about yourself";
    }

    /*public UserProfile (String userEmail, String userName) {
        this.userEmail = userEmail;
        this.userName = userName;
    }*/

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

    public String getWriteSomethingAboutYourself() {return writeSomethingAboutYourself;}

    public void setWriteSomethingAboutYourself(String writeSomethingAboutYourself) {this.writeSomethingAboutYourself = writeSomethingAboutYourself;}
}
