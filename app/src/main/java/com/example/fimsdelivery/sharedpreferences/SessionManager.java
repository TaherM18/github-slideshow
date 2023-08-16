package com.example.fimsdelivery.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    Context context;
    SharedPreferences userSession;
    SharedPreferences.Editor editor;

    public static final String SESSION_NAME = "userSession";
    public static final String KEY_FULLNAME = "fullName";
    public static final String KEY_CONTACT = "contact";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_LICENSE = "licenseNo";
    public static final String KEY_VEHICLE = "vehicleNo";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PASS = "pass";
    public static final String KEY_ID = "id";
    private static final String IS_LOGIN = "IsLoggedIn";

    public SessionManager(Context context) {
        this.context = context;
        userSession = context.getSharedPreferences(SESSION_NAME, Context.MODE_PRIVATE);
        editor = userSession.edit();
    }

    public void createLoginSession(String fullName, String contact, String email, String license, String vehicle, String address, String pass) {
        editor.putString(KEY_FULLNAME, fullName);
        editor.putString(KEY_CONTACT, contact);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_LICENSE, license);
        editor.putString(KEY_VEHICLE, vehicle);
        editor.putString(KEY_ADDRESS, address);
        editor.putString(KEY_PASS, pass);

        editor.commit();
    }

    public HashMap<String,String> getSessionData() {
        HashMap<String,String> userData = new HashMap<>();
        userData.put(KEY_FULLNAME, userSession.getString(KEY_FULLNAME, null));
        userData.put(KEY_CONTACT, userSession.getString(KEY_CONTACT, null));
        userData.put(KEY_EMAIL, userSession.getString(KEY_EMAIL, null));
        userData.put(KEY_LICENSE, userSession.getString(KEY_LICENSE, null));
        userData.put(KEY_VEHICLE, userSession.getString(KEY_VEHICLE, null));
        userData.put(KEY_ADDRESS, userSession.getString(KEY_ADDRESS, null));
        userData.put(KEY_PASS, userSession.getString(KEY_PASS, null));

        return userData;
    }

    public void putUserId(int userId) {
        editor.putInt(KEY_ID, userId);
        editor.putBoolean(IS_LOGIN, true);
        editor.commit();
    }

    public int getUserIdFromSession() {
        int userId = userSession.getInt(KEY_ID, 0);
        return userId;
    }

    public boolean checkLogin() {
        if (userSession.getBoolean(IS_LOGIN, false)) {
            return true;
        }
        else {
            return false;
        }
    }

    public void logoutFromSession() {
        editor.clear();
        editor.commit();
    }

}
