package com.capstone.e_waste_manager.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;


public class ProfilePostModel {
    public String homeTitle;
    public String homeAuthor;
    public String homeBody;
    public String docId;
    public String homeAuthorUid;
    public Timestamp homePostDate;


    public ProfilePostModel(){}

    public ProfilePostModel(String homeTitle, String homeAuthor, String homeBody, String homeAuthorUid, Timestamp homePostDate) {
        this.homeTitle = homeTitle;
        this.homeAuthor = homeAuthor;
        this.homeBody = homeBody;
        this.homeAuthorUid = homeAuthorUid;
        this.homePostDate = homePostDate;
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

    public String getHomeAuthorUid() { return homeAuthorUid; }

    public void setHomeAuthorUid(String homeAuthorUid) {this.homeAuthorUid = homeAuthorUid; }

    public Timestamp getHomePostDate() { return homePostDate; }

    public void setHomePostDate(Timestamp homePostDate) {this.homePostDate = homePostDate; }

}
