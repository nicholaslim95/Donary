package com.example.donary.models;

public class ModelPost {

    String donateid, posImage, donater, title, description, condition, time, status, quantity; //all postTitle were originally title

    public ModelPost() {
    }

    public ModelPost(String donateid, String posImage, String donater, String title, String description, String condition, String time, String status, String quantity) {
        this.donateid = donateid;
        this.posImage = posImage;
        this.donater = donater;
        this.title = title;
        this.description = description;
        this.condition = condition;
        this.time = time;
        this.status = status;
        this.quantity = quantity;
    }

    public String getDonateid() {
        return donateid;
    }

    public void setDonateid(String donateid) {
        this.donateid = donateid;
    }

    public String getPosImage() {
        return posImage;
    }

    public void setPosImage(String posImage) {
        this.posImage = posImage;
    }

    public String getDonater() {
        return donater;
    }

    public void setDonater(String donater) {
        this.donater = donater;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
