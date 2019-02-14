package com.blazingapps.asus.lifesource;

class AmbRespObject {
    String name;
    String distance;
    String contactno;
    String uid;
    double reqcount,serviced;

    public AmbRespObject(String name, String distance, String contactno, String uid, Double reqcount, Double serviced) {
        this.name = name;
        this.distance = distance;
        this.contactno = contactno;
        this.uid = uid;
        this.reqcount=reqcount;
        this.serviced=serviced;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String vehicleno) {
        this.distance = vehicleno;
    }

    public String getContactno() {
        return contactno;
    }

    public void setContactno(String contactno) {
        this.contactno = contactno;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getReqcount() {
        return reqcount;
    }

    public void setReqcount(double reqcount) {
        this.reqcount = reqcount;
    }

    public double getServiced() {
        return serviced;
    }

    public void setServiced(double serviced) {
        this.serviced = serviced;
    }
}
