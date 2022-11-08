package com.capstone.e_waste_manager;

public class LearnModel {
    String learnTitle, learnAuthor, learnBody, learnImage;

    public LearnModel(){}

    public LearnModel(String learnTitle, String learnAuthor, String learnBody, String learnImage) {
        this.learnTitle = learnTitle;
        this.learnAuthor = learnAuthor;
        this.learnBody = learnBody;
        this.learnImage = learnImage;
    }

    public String getLearnTitle() {
        return learnTitle;
    }

    public void setLearnTitle(String learnTitle) {
        this.learnTitle = learnTitle;
    }

    public String getLearnAuthor() {
        return learnAuthor;
    }

    public void setLearnAuthor(String learnAuthor) {
        this.learnAuthor = learnAuthor;
    }

    public String getLearnBody() {
        return learnBody;
    }

    public void setLearnBody(String learnBody) {
        this.learnBody = learnBody;
    }

    public String getLearnImage() {
        return learnImage;
    }

    public void setLearnImage(String learnImage) {
        this.learnImage = learnImage;
    }
}
