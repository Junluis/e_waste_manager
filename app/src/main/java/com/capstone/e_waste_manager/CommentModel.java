package com.capstone.e_waste_manager;

public class CommentModel {
    String commentBody, commentAuthor, commentTime;

    public CommentModel(){}

    public CommentModel(String commentBody, String commentAuthor, String commentTime) {
        this.commentBody = commentBody;
        this.commentAuthor = commentAuthor;
        this.commentTime = commentTime;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public String getCommentAuthor() {
        return commentAuthor;
    }

    public void setCommentAuthor(String commentAuthor) {
        this.commentAuthor = commentAuthor;
    }
}
