package com.capstone.e_waste_manager.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.Date;

public class DisposalModel implements Serializable {
    public String address;
    public String barangay;
    public GeoPoint maplocation;
    public String docId;


    public DisposalModel(){}

    public DisposalModel(String address, String barangay, GeoPoint maplocation) {
        this.address = address;
        this.barangay = barangay;
        this.maplocation = maplocation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBarangay() { return barangay; }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    @DocumentId
    public String getDocId() {return docId;}

    @DocumentId
    public void setDocId(String docId) {this.docId = docId;}

    public GeoPoint getMaplocation() { return maplocation; }

    public void setMaplocation(GeoPoint maplocation) { this.maplocation = maplocation; }



}
