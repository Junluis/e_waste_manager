package com.capstone.e_waste_manager.model;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.Date;

public class ReplyModel implements Serializable {
    public String replyAuthor;
    public String replyBody;
    public String replydocId;
    public String replyAuthorUid;
    public String replyChip;
    public String replyPostOrigin;
    public Date replyPostDate;


    public ReplyModel(){}

    public ReplyModel(String replyAuthor, String replyBody, String replyAuthorUid, Date replyPostDate, String replydocId, String replyChip, String replyPostOrigin) {
        this.replyAuthor = replyAuthor;
        this.replyBody = replyBody;
        this.replyAuthorUid = replyAuthorUid;
        this.replyPostDate = replyPostDate;
        this.replydocId = replydocId;
        this.replyChip = replyChip;
        this.replyPostOrigin = replyPostOrigin;
    }

    public String getReplyAuthor() { return replyAuthor; }

    public void setReplyAuthor(String replyAuthor) {
        this.replyAuthor = replyAuthor;
    }

    public String getReplyBody() {return replyBody;}

    public void setReplyBody(String replyBody) {
        this.replyBody = replyBody;
    }

    @DocumentId
    public String getReplydocId() {return replydocId;}

    @DocumentId
    public void setReplydocId(String replydocId) {this.replydocId = replydocId;}

    public Date getReplyPostDate() { return replyPostDate; }

    public void setReplyPostDate(Date replyPostDate) {this.replyPostDate = replyPostDate; }

    public String getReplyAuthorUid() { return replyAuthorUid; }

    public void setHomeAuthorUid(String replyAuthorUid) {this.replyAuthorUid = replyAuthorUid; }

    public String getReplyChip() { return replyChip; }

    public void setReplyChip(String replyChip) {this.replyChip = replyChip; }

    public String getReplyPostOrigin() { return replyPostOrigin; }

    public void setReplyPostOrigin(String replyPostOrigin) {this.replyPostOrigin = replyPostOrigin; }

    @Override
    public String toString(){
        return "ReplyModel{" +
                ", replyAuthor="+replyAuthor+ '\'' +
                ", replyBody="+replyBody+ '\'' +
                ", replyAuthorUid="+replyAuthorUid+ '\'' +
                ", replyPostDate="+replyPostDate +
                ", replydocId="+replydocId+ '\'' +
                ", replyChip="+replyChip+ '\'' +
                ", replyPostOrigin="+replyPostOrigin+ '\'' +
                '}';
    }
}
