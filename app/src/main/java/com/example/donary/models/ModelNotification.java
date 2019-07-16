package com.example.donary.models;

public class ModelNotification {

    private  String userid, text, postid, ispost;

    public ModelNotification(String userid, String text, String postid, String ispost) {
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.ispost = ispost;
    }

    public ModelNotification() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String isIspost() {
        return ispost;
    }

    public void setIspost(String ispost) {
        this.ispost = ispost;
    }
}
