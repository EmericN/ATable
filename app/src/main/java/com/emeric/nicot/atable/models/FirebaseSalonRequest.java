package com.emeric.nicot.atable.models;

/**
 * Created by Nicot Emeric on 23/01/2018.
 */

public class FirebaseSalonRequest {

    private String salon;
    private String id;

    public FirebaseSalonRequest() {
    }

    public FirebaseSalonRequest(String salon, String id) {
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
