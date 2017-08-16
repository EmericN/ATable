package com.emeric.nicot.atable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Nicot Emeric on 05/07/2017.
 */

public class SessionManagement {

    //SharedPref
    SharedPreferences pref;
    //Editor
    SharedPreferences.Editor editor;
    //Context
    Context  _context;

    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "ATable";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    // User name (make variable public to access from outside)
    public static final String KEY_PASSWORD = "password";

    // Constructor
    public SessionManagement(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String password, String email){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_PASSWORD, password);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // return user
        return user;
    }
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login MainActivity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new MainActivity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login MainActivity
            _context.startActivity(i);
        }
    }
        public void logoutUser(){
            // Clearing all data from Shared Preferences
            editor.clear();
            editor.commit();

            // After logout redirect user to Loing MainActivity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new MainActivity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login MainActivity
            _context.startActivity(i);
        }


        // Get Login State
        public boolean isLoggedIn(){
            return pref.getBoolean(IS_LOGIN, false);
        }
}
