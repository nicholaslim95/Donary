package com.example.donary.models;

import java.util.Date;

public class ModelEventPost {
    private String eventId;
    private String eventName;
    private String eventDetail;
    private String eventLocation;
    private int noOfParticipants;
    private Date eventStartDate;
    private Date eventEndDate;
    private String eventPoster;
    private String eventStatus;
    public ModelEventPost(){

    }

    public ModelEventPost(String eventId, String eventName, String eventDetail, String eventLocation, int noOfParticipants, Date eventStartDate, Date eventEndDate, String eventPoster) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDetail = eventDetail;
        this.eventLocation = eventLocation;
        this.noOfParticipants = noOfParticipants;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.eventPoster = eventPoster;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDetail() {
        return eventDetail;
    }

    public void setEventDetail(String eventDetail) {
        this.eventDetail = eventDetail;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public int getNoOfParticipants() {
        return noOfParticipants;
    }

    public void setNoOfParticipants(int noOfParticipants) {
        this.noOfParticipants = noOfParticipants;
    }

    public Date getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(Date eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public Date getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(Date eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public String getEventPoster() {
        return eventPoster;
    }

    public void setEventPoster(String eventPoster) {
        this.eventPoster = eventPoster;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }
}
