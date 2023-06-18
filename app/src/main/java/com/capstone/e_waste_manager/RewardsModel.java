package com.capstone.e_waste_manager;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.Date;

public class RewardsModel implements Serializable {
    public String details;
    public String title;
    public Integer points;
    public String docId;
    public String prefix;


    public RewardsModel(){}

    public RewardsModel(String details, Integer points, String title, String docId, String prefix) {
        this.details = details;
        this.points = points;
        this.title = title;
        this.docId = docId;
        this.prefix = prefix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Integer getPoints() { return points; }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getDetails() {return details;}

    public void setDetails(String details) {
        this.details = details;
    }

    @DocumentId
    public String getDocId() {return docId;}

    @DocumentId
    public void setDocId(String docId) {this.docId = docId;}

}
