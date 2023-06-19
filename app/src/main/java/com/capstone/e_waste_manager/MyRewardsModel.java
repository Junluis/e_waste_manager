package com.capstone.e_waste_manager;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;

public class MyRewardsModel implements Serializable {
    public String code;
    public String rewardId;
    public String docId;
    public String rewardUid;
    public Boolean revealed;


    public MyRewardsModel(){}

    public MyRewardsModel(String code, String rewardId, String docId, String rewardUid, Boolean revealed) {
        this.code = code;
        this.rewardId = rewardId;
        this.docId = docId;
        this.rewardUid = rewardUid;
        this.revealed = revealed;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getRevealed() {
        return revealed;
    }

    public void setRevealed(Boolean revealed) {
        this.revealed = revealed;
    }

    public String getRewardId() {
        return rewardId;
    }

    public void setRewardId(String rewardId) {
        this.rewardId = rewardId;
    }

    public String getRewardUid() {return rewardUid;}

    public void setRewardUid(String rewardUid) {
        this.rewardUid = rewardUid;
    }

    @DocumentId
    public String getDocId() {return docId;}

    @DocumentId
    public void setDocId(String docId) {this.docId = docId;}

}
