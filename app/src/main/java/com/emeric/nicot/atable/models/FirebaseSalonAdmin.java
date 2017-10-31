package com.emeric.nicot.atable.models;


public class FirebaseSalonAdmin {

    private String salonAdmin;
    private String id;

    public FirebaseSalonAdmin() {
    }

    public FirebaseSalonAdmin(String salonAdmin, String id) {
        this.salonAdmin = salonAdmin;
        this.id = id;
    }

    public String getSalon() {
        return salonAdmin;
    }

    public String getSalonId() {
        return id;
    }

    public int size() {
        return salonAdmin.length();
    }


}
