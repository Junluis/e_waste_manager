package com.capstone.e_waste_manager;

import com.google.firebase.firestore.DocumentId;

public class LearnModel {
    String learnTitle, learnAuthor, learnBody, url, filepdf, learnSubTitle, docId;

    public LearnModel(){}

    public LearnModel(String learnTitle, String learnAuthor, String learnBody, String url, String learnSubTitle, String filepdf) {
        this.learnTitle = learnTitle;
        this.learnAuthor = learnAuthor;
        this.learnBody = learnBody;
        this.url = url;
        this.filepdf = filepdf;
        this.learnSubTitle = learnSubTitle;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilepdf() {
        return filepdf;
    }

    public void setFilepdf(String filepdf) {
        this.filepdf = filepdf;
    }

    public String getLearnSubTitle() {
        return learnSubTitle;
    }

    public void setLearnSubTitle(String learnSubTitle) {
        this.learnSubTitle = learnSubTitle;
    }

    @DocumentId
    public String getDocId() {return docId;}

    @DocumentId
    public void setDocId(String docId) {this.docId = docId;}
}
