package com.emeric.nicot.atable.models;

public interface AdapterCallbackNotif {
    void onMethodCallbackTick(String nomSalon, String salonId, String idDoc);
    void onMethodCallbackCross(String nomSalon, String idDoc);
}

