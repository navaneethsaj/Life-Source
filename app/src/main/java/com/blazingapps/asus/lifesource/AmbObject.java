package com.blazingapps.asus.lifesource;

public class AmbObject {
    String name;
    String vehicleno;
    String contactno;
    float latitude;
    float longitude;

    public AmbObject(String name, String vehicleno, String contactno, float latitude, float longitude) {
        this.name = name;
        this.vehicleno = vehicleno;
        this.contactno = contactno;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVehicleno() {
        return vehicleno;
    }

    public void setVehicleno(String vehicleno) {
        this.vehicleno = vehicleno;
    }

    public String getContactno() {
        return contactno;
    }

    public void setContactno(String contactno) {
        this.contactno = contactno;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
