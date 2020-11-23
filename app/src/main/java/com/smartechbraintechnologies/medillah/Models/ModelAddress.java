package com.smartechbraintechnologies.medillah.Models;

public class ModelAddress {

    private int icon;
    private String addressType;
    private String addressStatus;
    private String address;
    private String addressDeliveryStatus;

    public ModelAddress() {
    }

    public ModelAddress(int icon, String addressType, String addressStatus, String address, String addressDeliveryStatus) {
        this.icon = icon;
        this.addressType = addressType;
        this.addressStatus = addressStatus;
        this.address = address;
        this.addressDeliveryStatus = addressDeliveryStatus;
    }

    public int getIcon() {
        return icon;
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
