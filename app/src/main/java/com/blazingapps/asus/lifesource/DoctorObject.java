package com.blazingapps.asus.lifesource;

public class DoctorObject {
    public String name;
    public String speciality;
    public String phoneno;
    public String time;

    public DoctorObject(String name, String speciality, String available, String s) {
        this.name = name;
        this.speciality = speciality;
        this.phoneno = available;
        this.time=s;
    }

    public String getName() {
        return name;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void getPhoneno(String available) {
        this.phoneno = available;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
