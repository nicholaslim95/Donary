package com.example.donary;

import java.util.Date;

public class Event {
    private String eventName;
    private String eventDetails;
    private String eventLocation;
    private Date eventStartDate;
    private Date eventEndDate;
    private int noOfParticipants;

    public Event(){

    }

    public Event(String eventName, String eventDetails, String eventLocation, Date eventStartDate, Date eventEndDate, int noOfParticipants) {
        this.eventName = eventName;
        this.eventDetails = eventDetails;
        this.eventLocation = eventLocation;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.noOfParticipants = noOfParticipants;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
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

    public int getNoOfParticipants() {
        return noOfParticipants;
    }

    public void setNoOfParticipants(int noOfParticipants) {
        this.noOfParticipants = noOfParticipants;
    }
}
