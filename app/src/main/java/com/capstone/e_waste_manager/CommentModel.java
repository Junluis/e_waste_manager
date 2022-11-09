package com.capstone.e_waste_manager;

public class CommentModel {
    String commentBody, commentAuthor;

    public CommentModel(){}

    public CommentModel(String commentBody, String commentAuthor) {
        this.commentBody = commentBody;
        this.commentAuthor = commentAuthor;
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
