package com.capstone.e_waste_manager;

import static com.google.firebase.firestore.DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.util.Date;

public class CommentModel {
    String commentBody, commentAuthor, docId, commentUid;
    Date commentPostDate;

    public CommentModel(){}

    public CommentModel(String commentBody, String commentAuthor, Date commentPostDate, String commentUid) {
        this.commentBody = commentBody;
        this.commentAuthor = commentAuthor;
        this.commentPostDate = commentPostDate;
        this.commentUid = commentUid;
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

    public String getCommentUid() {
        return commentUid;
    }

    public void setCommentUid(String commentUid) {
        this.commentUid = commentUid;
    }

    @DocumentId
    public String getDocId() {return docId;}

    @DocumentId
    public void setDocId(String docId) {this.docId = docId;}

    public Date getCommentPostDate() { return commentPostDate; }

    public void setCommentPostDate(Date commentPostDate) { this.commentPostDate = commentPostDate; }
}
