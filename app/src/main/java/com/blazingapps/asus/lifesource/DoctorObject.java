package com.blazingapps.asus.lifesource;

public class DoctorObject {
    public String name;
    public String speciality;
    public String phoneno;

    public DoctorObject(String name, String speciality, String available) {
        this.name = name;
        this.speciality = speciality;
        this.phoneno = available;
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
}
