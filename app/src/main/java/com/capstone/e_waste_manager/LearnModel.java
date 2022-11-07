package com.capstone.e_waste_manager;

public class LearnModel {
    String learnTitle, learnAuthor, learnBody;

    public LearnModel(){}

    public LearnModel(String learntitle, String learnAuthor, String learnBody) {
        this.learnTitle = learntitle;
        this.learnAuthor = learnAuthor;
        this.learnBody = learnBody;
    }

    public String getLearnTitle() {
        return learnTitle;
    }

    public void setLearnTitle(String learntitle) {
        this.learnTitle = learntitle;
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
}
