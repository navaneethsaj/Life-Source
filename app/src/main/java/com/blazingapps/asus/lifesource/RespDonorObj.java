package com.blazingapps.asus.lifesource;

public class RespDonorObj {
    String name;
    String mobile;
    String bloodgroup;
    String distance;
    String uid;
    double reqcount;
    double serviced;

    public RespDonorObj(String name, String mobile, String bloodgroup, String distance,String  uid, double reqcount, double serviced) {
        this.name = name;
        this.mobile = mobile;
        this.reqcount = reqcount;
        this.serviced = serviced;
        this.uid = uid;
        this.bloodgroup = bloodgroup;
        this.distance = distance;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setBloodgroup(String bloodgroup) {
        this.bloodgroup = bloodgroup;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public String getDistance() {
        return distance;
    }
}
