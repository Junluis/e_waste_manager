package com.capstone.e_waste_manager.model;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.Date;

public class DonationModel implements Serializable {
    public String barangay, address, brand, condition, deviceAge, deviceType, pickupdate,
            donationUid, markerUid, method, model, number, status, donationDocId, docId;
    public Date donationDate;

    public DonationModel() {}

    public DonationModel(String barangay, String address, String brand, String condition, String deviceAge, String deviceType, String donationDocId,
                         String donationUid, String markerUid, String method, String model, String number, String status, Date donationDate, String pickupdate){
        this.barangay = barangay;
        this.address = address;
        this.brand = brand;
        this.condition = condition;
        this.deviceAge = deviceAge;
        this.deviceType = deviceType;
        this.donationUid = donationUid;
        this.markerUid = markerUid;
        this.method = method;
        this.model = model;
        this.number = number;
        this.status = status;
        this.donationDocId = donationDocId;
        this.donationDate = donationDate;
        this.pickupdate = pickupdate;
    }

    public String getBarangay() { return barangay; }

    public void setBarangay(String barangay) { this.barangay = barangay; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    @DocumentId
    public String getDocId() {return docId;}

    @DocumentId
    public void setDocId(String replydocId) {this.docId = replydocId;}

    public Date getDonationDate() { return donationDate; }

    public void setDonationDate(Date donationDate) {this.donationDate = donationDate; }

    public String getBrand() { return brand; }

    public void setBrand(String brand) { this.brand = brand; }

    public String getCondition() { return condition; }

    public void setCondition(String condition) { this.condition = condition; }

    public String getDeviceAge() { return deviceAge; }

    public void setDeviceAge(String deviceAge) { this.deviceAge = deviceAge; }

    public String getDeviceType() { return deviceType; }

    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public String getDonationUid() { return donationUid; }

    public void setDonationUid(String donationUid) { this.donationUid = donationUid; }

    public String getMarkerUid() { return markerUid; }

    public void setMarkerUid(String markerUid) { this.markerUid = markerUid; }

    public String getMethod() { return method; }

    public void setMethod(String method) { this.method = method; }

    public String getModel() { return model; }

    public void setModel(String model) { this.model = model; }

    public String getNumber() { return number; }

    public void setNumber(String number) { this.number = number; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getDonationDocId() { return donationDocId; }

    public void setDonationDocId(String donationDocId) { this.donationDocId = donationDocId; }

    public String getPickupdate() { return pickupdate; }

    public void setPickupdate(String pickupdate) { this.pickupdate = pickupdate; }
}
