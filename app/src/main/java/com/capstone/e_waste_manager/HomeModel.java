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
    public Boolean hasImage;
    public String url;


    public HomeModel(){}

    public HomeModel(String homeTitle, String homeAuthor, String homeBody, String homeAuthorUid, Date homePostDate, String docId, Boolean hasImage, String url) {
        this.homeTitle = homeTitle;
        this.homeAuthor = homeAuthor;
        this.homeBody = homeBody;
        this.homeAuthorUid = homeAuthorUid;
        this.homePostDate = homePostDate;
        this.docId = docId;
        this.hasImage = hasImage;
        this.url = url;
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

    public Boolean getHasImage() { return hasImage; }

    public void setHasImage(Boolean hasImage) {this.hasImage = hasImage; }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

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
