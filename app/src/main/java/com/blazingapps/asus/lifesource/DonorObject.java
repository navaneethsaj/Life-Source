package com.blazingapps.asus.lifesource;

public class DonorObject {
    String name;
    String bloodgroup;
    String contactno;
    String address;
    float latitude;
    float longitude;

    boolean available = true;

    boolean sun = true;
    boolean mon = true;
    boolean tue = true;
    boolean wed = true;
    boolean thu = true;
    boolean fri = true;
    boolean sat = true;

    int fromhour=8;
    int tohour=20;
    int frommin = 00;
    int tomin = 00;

    public DonorObject(String name, String bloodgroup, String contactno, String address, float latitude, float longitude) {
        this.name = name;
        this.bloodgroup = bloodgroup;
        this.contactno = contactno;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getFromhour() {
        return fromhour;
    }

    public void setFromhour(int fromhour) {
        this.fromhour = fromhour;
    }

    public int getTohour() {
        return tohour;
    }

    public void setTohour(int tohour) {
        this.tohour = tohour;
    }

    public int getFrommin() {
        return frommin;
    }

    public void setFrommin(int frommin) {
        this.frommin = frommin;
    }

    public int getTomin() {
        return tomin;
    }

    public void setTomin(int tomin) {
        this.tomin = tomin;
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }


    public boolean isSun() {
        return sun;
    }

    public void setSun(boolean sun) {
        this.sun = sun;
    }

    public boolean isMon() {
        return mon;
    }

    public void setMon(boolean mon) {
        this.mon = mon;
    }

    public boolean isTue() {
        return tue;
    }

    public void setTue(boolean tue) {
        this.tue = tue;
    }

    public boolean isWed() {
        return wed;
    }

    public void setWed(boolean wed) {
        this.wed = wed;
    }

    public boolean isThu() {
        return thu;
    }

    public void setThu(boolean thu) {
        this.thu = thu;
    }

    public boolean isFri() {
        return fri;
    }

    public void setFri(boolean fri) {
        this.fri = fri;
    }

    public boolean isSat() {
        return sat;
    }

    public void setSat(boolean sat) {
        this.sat = sat;
    }
}
