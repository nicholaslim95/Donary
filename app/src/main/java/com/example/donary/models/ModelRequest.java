package com.example.donary.models;

public class ModelRequest {

    String requester, time;

    public ModelRequest(){

    }

    public ModelRequest(String requester, String time) {
        this.requester = requester;
        this.time = time;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
