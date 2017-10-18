package com.emeric.nicot.atable.models;


public class FirebaseSalonAdmin {

    private String salonAdmin;

    public FirebaseSalonAdmin() {
    }

    public FirebaseSalonAdmin(String salonAdmin) {
        this.salonAdmin = salonAdmin;
    }

    public String getSalon() {
        return salonAdmin;
    }

    public int size() {
        return salonAdmin.length();
    }

}
