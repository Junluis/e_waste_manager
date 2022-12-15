package com.capstone.e_waste_manager.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.Date;

public class DisposalModel implements Serializable {
    public String address, disposaldesc, donationdesc;
    public String barangay;
    public GeoPoint maplocation;
    public String docId;
    public String markerUid;
    public String markername;
    public Boolean hasImage, collect_donation, collect_ewaste;


    public DisposalModel(){}

    public DisposalModel(String disposaldesc, String donationdesc, String address, String barangay, GeoPoint maplocation, String markerUid, String markername, Boolean hasImage, Boolean collect_donation, Boolean collect_ewaste) {
        this.address = address;
        this.barangay = barangay;
        this.maplocation = maplocation;
        this.markerUid = markerUid;
        this.markername = markername;
        this.hasImage = hasImage;
        this.collect_donation = collect_donation;
        this.collect_ewaste = collect_ewaste;
        this.disposaldesc = disposaldesc;
        this.donationdesc = donationdesc;
    }

    public String getDisposaldesc() {
        return disposaldesc;
    }

    public void setDisposaldesc(String disposaldesc) {
        this.disposaldesc = disposaldesc;
    }

    public String getDonationdesc() {
        return donationdesc;
    }

    public void setDonationdesc(String donationdesc) {
        this.donationdesc = donationdesc;
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

    public Boolean getHasImage() { return hasImage; }

    public void setHasImage(Boolean hasImage) {
        this.hasImage = hasImage;
    }

    public Boolean getCollect_donation() { return collect_donation; }

    public void setCollect_donation(Boolean collect_donation) {
        this.collect_donation = collect_donation;
    }

    public Boolean getCollect_ewaste() { return collect_ewaste; }

    public void setCollect_ewaste(Boolean collect_ewaste) {
        this.collect_ewaste = collect_ewaste;
    }



}
