package com.blazingapps.asus.lifesource;

public class HospitalObject {
    String name;
    String address;
    String phone;
    String mobile;
    String fax;
    String email;
    float latitude;
    float longitude;
    String uid;

    public HospitalObject(String name, String address, String phone, String mobile, String fax, String email, float latitude, float longitude, String uid) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.mobile = mobile;
        this.fax = fax;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getMobile() {
        return mobile;
    }

    public String getFax() {
        return fax;
    }

    public String getEmail() {
        return email;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getUid() {
        return uid;
    }
}
