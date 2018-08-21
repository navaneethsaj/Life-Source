package com.blazingapps.asus.lifesource;

public class RespDonorObj {
    String name;
    String mobile;
    String bloodgroup;
    String distance;

    public RespDonorObj(String name, String mobile, String bloodgroup, String distance) {
        this.name = name;
        this.mobile = mobile;
        this.bloodgroup = bloodgroup;
        this.distance = distance;
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
