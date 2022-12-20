package com.capstone.e_waste_manager;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.Date;

public class RequestModel implements Serializable {

    public String reqAddress;
    public String reqBarangay;
    public String reqDesc;
    public String reqName;
    public String reqNumber;
    public String reqUserId;
    public String docId;
    public Date reqDate;


    public RequestModel(){}

    public RequestModel(String reqAddress, String reqBarangay,String reqDesc,String reqName,String reqNumber,String reqUserId, Date reqDate) {
        this.reqAddress = reqAddress;
        this.reqBarangay = reqBarangay;
        this.reqDesc = reqDesc;
        this.reqName = reqName;
        this.reqNumber = reqNumber;
        this.reqUserId = reqUserId;
        this.reqDate = reqDate;
    }

    @DocumentId
    public String getDocId() {return docId;}

    @DocumentId
    public void setDocId(String docId) {this.docId = docId;}

    public String getReqAddress() {
        return reqAddress;
    }

    public void setReqAddress(String reqAddress) {
        this.reqAddress = reqAddress;
    }

    public String getReqBarangay() {
        return reqBarangay;
    }

    public void setReqBarangay(String reqBarangay) {
        this.reqBarangay = reqBarangay;
    }

    public String getReqDesc() {
        return reqDesc;
    }

    public void setReqDesc(String reqDesc) {
        this.reqDesc = reqDesc;
    }

    public String getReqName() {
        return reqName;
    }

    public void setReqName(String reqName) {
        this.reqName = reqName;
    }

    public String getreqNumber() {
        return reqNumber;
    }

    public void setReqNumber(String reqNumber) {
        this.reqNumber = reqNumber;
    }

    public String getReqUserId() {
        return reqUserId;
    }

    public void setReqUserId(String reqUserId) {
        this.reqUserId = reqUserId;
    }

    public Date getReqDate() {
        return reqDate;
    }

    public void setReqDate(Date reqDate) {
        this.reqDate = reqDate;
    }


}
