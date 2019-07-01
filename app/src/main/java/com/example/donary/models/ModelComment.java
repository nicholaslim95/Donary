package com.example.donary.models;

public class ModelComment {

    private String comment;
    private String commenter;

    public ModelComment(String comment, String commenter) {
        this.comment = comment;
        this.commenter = commenter;
    }

    public ModelComment() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommenter() {
        return commenter;
    }

    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }
}
