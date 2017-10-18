package com.emeric.nicot.atable.models;


/**
 * Created by Nicot Emeric on 19/09/2017.
 */

public class FirebaseSalonMembre {

    private String salonMembre;

    public FirebaseSalonMembre() {
    }

    public FirebaseSalonMembre(String salonAdmin) {
        this.salonMembre = salonAdmin;
    }

    public String getSalon() {
        return salonMembre;
    }

    public int size() {
        return salonMembre.length();
    }
}

