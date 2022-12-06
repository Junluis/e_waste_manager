package com.capstone.e_waste_manager;

public class RequestModel {
    String reqName, reqAddress, reqNumber, reqDesc, reqDTI, reqSEC, reqUserMail, reqUserId;

    public RequestModel(){}

    public RequestModel(String reqName, String reqAddress, String reqNumber, String reqDesc, String reqDTI, String reqSEC, String reqUserMail, String reqUserId) {
        this.reqName = reqName;
        this.reqAddress = reqAddress;
        this.reqNumber = reqNumber;
        this.reqDesc = reqDesc;
        this.reqDTI = reqDTI;
        this.reqSEC = reqSEC;
        this.reqUserMail = reqUserMail;
        this.reqUserId = reqUserId;
    }

    public String getReqName() {
        return reqName;
    }

    public void setReqName(String reqName) {
        this.reqName = reqName;
    }

    public String getReqAddress() {
        return reqAddress;
    }

    public void setReqAddress(String reqAddress) {
        this.reqAddress = reqAddress;
    }

    public String getReqNumber() {
        return reqNumber;
    }

    public void setReqNumber(String reqNumber) {
        this.reqNumber = reqNumber;
    }

    public String getReqDesc() {
        return reqDesc;
    }

    public void setReqDesc(String reqDesc) {
        this.reqDesc = reqDesc;
    }

    public String getReqDTI() {
        return reqDTI;
    }

    public void setReqDTI(String reqDTI) {
        this.reqDTI = reqDTI;
    }

    public String getReqSEC() {
        return reqSEC;
    }

    public void setReqSEC(String reqSEC) {
        this.reqSEC = reqSEC;
    }

    public String getReqUserMail() {
        return reqUserMail;
    }

    public void setReqUserMail(String reqUserMail) {
        this.reqUserMail = reqUserMail;
    }

    public String getReqUserId() {
        return reqUserId;
    }

    public void setReqUserId(String reqUserId) {
        this.reqUserId = reqUserId;
    }
}
