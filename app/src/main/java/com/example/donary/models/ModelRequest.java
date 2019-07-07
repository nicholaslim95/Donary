package com.example.donary.models;

public class ModelRequest {

    String donateid, requester, time, reason, status;

    public ModelRequest(){

    }

    public ModelRequest(String donateid, String requester, String time, String reason, String status) {
        this.donateid = donateid;
        this.requester = requester;
        this.time = time;
        this.reason = reason;
        this.status = status;
    }

    public String getDonateid() {
        return donateid;
    }

    public void setDonateid(String donateid) {
        this.donateid = donateid;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
