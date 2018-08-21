package com.blazingapps.asus.lifesource;

public class DonorObject {
    String name;
    String bloodgroup;
    String contactno;
    String address;

    float latitude;
    float longitude;

    public DonorObject(String name, String bloodgroup, String contactno, String address, float latitude, float longitude) {
        this.name = name;
        this.bloodgroup = bloodgroup;
        this.contactno = contactno;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public String getContactno() {
        return contactno;
    }

    public String getAddress() {
        return address;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
}
