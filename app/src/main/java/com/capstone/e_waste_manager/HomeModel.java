package com.capstone.e_waste_manager;

public class HomeModel {
    String homeTitle, homeAuthor, homeBody;

    public HomeModel(){}

    public HomeModel(String homeTitle, String homeAuthor, String homeBody) {
        this.homeTitle = homeTitle;
        this.homeAuthor = homeAuthor;
        this.homeBody = homeBody;
    }

    public String getHomeTitle() {
        return homeTitle;
    }

    public String getHomeAuthor() {
        return homeAuthor;
    }

    public String getHomeBody() {
        return homeBody;
    }
}
