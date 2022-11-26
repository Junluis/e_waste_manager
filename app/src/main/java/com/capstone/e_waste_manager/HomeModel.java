package com.capstone.e_waste_manager;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class HomeModel implements Serializable {
    public String homeTitle;
    public String homeAuthor;
    public String homeBody;
    public String docId;
    public String homeAuthorUid;
    public Date homePostDate;


    public HomeModel(){}

    public HomeModel(String homeTitle, String homeAuthor, String homeBody, String homeAuthorUid, Date homePostDate, String docId) {
        this.homeTitle = homeTitle;
        this.homeAuthor = homeAuthor;
        this.homeBody = homeBody;
        this.homeAuthorUid = homeAuthorUid;
        this.homePostDate = homePostDate;
        this.docId = docId;
    }

    public String getHomeTitle() {
        return homeTitle;
    }

    public void setHomeTitle(String homeTitle) {
        this.homeTitle = homeTitle;
    }

    public String getHomeAuthor() { return homeAuthor; }

    public void setHomeAuthor(String homeAuthor) {
        this.homeAuthor = homeAuthor;
    }

    public String getHomeBody() {return homeBody;}

    public void setHomeBody(String homeBody) {
        this.homeBody = homeBody;
    }

    @DocumentId
    public String getDocId() {return docId;}

    @DocumentId
    public void setDocId(String docId) {this.docId = docId;}

    public Date getHomePostDate() { return homePostDate; }

    public void setHomePostDate(Date homePostDate) {this.homePostDate = homePostDate; }

    public String getHomeAuthorUid() { return homeAuthorUid; }

    public void setHomeAuthorUid(String homeAuthorUid) {this.homeAuthorUid = homeAuthorUid; }

    @Override
    public String toString(){
        return "HomeModel{" +
                "homeTitle='"+homeTitle+ '\'' +
                ", homeAuthor="+homeAuthor+ '\'' +
                ", homeBody="+homeBody+ '\'' +
                ", homeAuthorUid="+homeAuthorUid+ '\'' +
                ", homePostDate="+homePostDate +
                ", docId="+docId+ '\'' +
                '}';
    }
}
