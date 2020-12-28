package com.smartechbraintechnologies.medillah.Models;

public class ModelAddress {

    private String addressID;
    private String addressType;
    private String addressStatus;
    private String address;
    private String addressDeliveryStatus;

    public ModelAddress() {
    }

    public ModelAddress(String addressID, String addressType, String addressStatus, String address, String addressDeliveryStatus) {
        this.addressID = addressID;
        this.addressType = addressType;
        this.addressStatus = addressStatus;
        this.address = address;
        this.addressDeliveryStatus = addressDeliveryStatus;
    }

    public String getAddressID() {
        return addressID;
    }

    public String getAddressType() {
        return addressType;
    }

    public String getAddressStatus() {
        return addressStatus;
    }

    public String getAddress() {
        return address;
    }

    public String getAddressDeliveryStatus() {
        return addressDeliveryStatus;
    }
}
