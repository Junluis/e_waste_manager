package com.capstone.e_waste_manager;

public class HomeModel {
    String homeTitle;
    String homeAuthor;
    String homeBody;
    String docId;

    public HomeModel(){}

    public HomeModel(String homeTitle, String homeAuthor, String homeBody, String docId) {
        this.homeTitle = homeTitle;
        this.homeAuthor = homeAuthor;
        this.homeBody = homeBody;
        this.docId = docId;
    }

    public String getHomeTitle() {
        return homeTitle;
    }

    public void setHomeTitle(String homeTitle) {
        this.homeTitle = homeTitle;
    }

    public String getHomeAuthor() {
        return homeAuthor;
    }

    public void setHomeAuthor(String homeAuthor) {
        this.homeAuthor = homeAuthor;
    }

    public String getHomeBody() {
        return homeBody;
    }

    public void setHomeBody(String homeBody) {
        this.homeBody = homeBody;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}
