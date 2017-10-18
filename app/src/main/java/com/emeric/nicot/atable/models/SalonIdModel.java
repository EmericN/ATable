package com.emeric.nicot.atable.models;

/**
 * Created by Nicot Emeric on 01/10/2017.
 */

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
