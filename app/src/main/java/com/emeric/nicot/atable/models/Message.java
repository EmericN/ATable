package com.emeric.nicot.atable.models;

import java.util.ArrayList;

/**
 * Created by Nicot Emeric on 12/12/2017.
 */

public class Message {

    private ArrayList<ChatMessage> listMessageData;

    public Message() {
        listMessageData = new ArrayList<>();
    }

    public ArrayList<ChatMessage> getListMessageData() {
        return listMessageData;
    }

}
