package com.example.donary.models;

public class ModelPost {

    String donateid,posImage, donater, Title;

    public ModelPost() {
    }

    public ModelPost(String donateid, String posImage, String donater, String title) {
        this.donateid = donateid;
        this.posImage = posImage;
        this.donater = donater;
        Title = title;
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
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
