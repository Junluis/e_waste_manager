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
    public String markerUid;
    public String markername;


    public DisposalModel(){}

    public DisposalModel(String address, String barangay, GeoPoint maplocation, String markerUid, String markername) {
        this.address = address;
        this.barangay = barangay;
        this.maplocation = maplocation;
        this.markerUid = markerUid;
        this.markername = markername;
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

    public String getMarkername() { return markername; }

    public void setMarkername(String markername) {
        this.markername = markername;
    }

    public String getMarkerUid() { return markerUid; }

    public void setMarkerUid(String markerUid) {
        this.markerUid = markerUid;
    }



}
