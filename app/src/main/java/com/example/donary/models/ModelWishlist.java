package com.example.donary.models;

public class ModelWishlist {

    String wishlistid, posImage, requester, title, description, time, status; //all postTitle were originally title

    public ModelWishlist() {
    }

    public ModelWishlist(String wishlistid, String posImage, String requester, String title, String description, String time, String status) {
        this.wishlistid = wishlistid;
        this.posImage = posImage;
        this.requester = requester;
        this.title = title;
        this.description = description;
        this.time = time;
        this.status = status;
    }

    public String getWishlistid() {
        return wishlistid;
    }

    public void setWishlistid(String wishlistid) {
        this.wishlistid = wishlistid;
    }

    public String getPosImage() {
        return posImage;
    }

    public void setPosImage(String posImage) {
        this.posImage = posImage;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
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
}
