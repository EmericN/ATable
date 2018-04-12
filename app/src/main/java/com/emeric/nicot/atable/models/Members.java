package com.emeric.nicot.atable.models;


import com.emeric.nicot.atable.MainActivity;

import java.util.HashMap;

public class Members {

    private HashMap<String, Boolean> members;

    public Members(){}

    public Members(HashMap<String, Boolean> members) {
        this.members = members;
    }

    public HashMap<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, Boolean> members) {
        this.members = members;
    }
}
