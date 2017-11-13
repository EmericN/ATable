package com.emeric.nicot.atable.models;


public class FirebaseSalonAdmin {

    private String salon;
    private String id;

    public FirebaseSalonAdmin() {
    }

    public FirebaseSalonAdmin(String salon, String id) {
        this.salon = salon;
        this.id = id;
    }

    public String getSalon() {
        return salon;
    }

    public String getSalonId() {
        return id;
    }

    public int size() {
        return salon.length();
    }


}
