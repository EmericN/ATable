package com.emeric.nicot.atable.models;

public class FirebaseSalonRequest {

    private String salon;
    private String id;
    private String idDoc;


    public FirebaseSalonRequest(){

    }
    
    public FirebaseSalonRequest(String salon, String id, String idDoc) {
        this.salon = salon;
        this.id = id;
        this.idDoc = idDoc;

    }

    public String getSalon() {
        return salon;
    }

    public String getSalonId() {
        return id;
    }

    public String getIdDoc() {
        return idDoc;
    }

    public int size() {
        return salon.length();
    }


}
