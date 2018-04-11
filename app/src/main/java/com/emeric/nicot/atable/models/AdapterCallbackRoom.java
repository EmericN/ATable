package com.emeric.nicot.atable.models;

import java.util.ArrayList;

public interface AdapterCallbackRoom {
    void onMethodCallbackQuickSticker(String nomSalon, String salonId);
    void onMethodCallbackEnterRoom(ArrayList<FirebaseSalon> salon, int position);
}
