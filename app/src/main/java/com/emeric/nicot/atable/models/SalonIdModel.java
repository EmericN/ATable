package com.emeric.nicot.atable.models;


public class SalonIdModel {

    private String userId;

    public SalonIdModel() {
    }

    public SalonIdModel(String userId) {
        this.userId = userId;
    }

    public String GetSalonId() {
        return userId;
    }


}
