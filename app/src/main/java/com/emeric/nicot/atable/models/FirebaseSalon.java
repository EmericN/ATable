package com.emeric.nicot.atable.models;


public class FirebaseSalon {

    private String salon;

    public FirebaseSalon() {
    }

    public FirebaseSalon(String salon) {
        this.salon = salon;
    }

    public String getSalon() {
        return salon;
    }

    public int size() {
        return salon.length();
    }

}

