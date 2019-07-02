package com.example.donary.models;

public class ModelChat {
    String message, receiver, sender, timestamp;
    boolean isSean;

    public ModelChat(){

    }

    public ModelChat(String message, String receiver, String sender, String timestamp, boolean isSean) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.timestamp = timestamp;
        this.isSean = isSean;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSean() {
        return isSean;
    }

    public void setSean(boolean sean) {
        isSean = sean;
    }
}
