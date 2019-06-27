package com.example.donary.models;

public class ModelRequest {

    String requester, time, reason;

    public ModelRequest(){

    }

    public ModelRequest(String requester, String time, String reason) {
        this.requester = requester;
        this.time = time;
        this.reason = reason;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
