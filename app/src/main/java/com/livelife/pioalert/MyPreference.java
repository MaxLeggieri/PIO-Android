package com.livelife.pioalert;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Shoeb on 14/8/17.
 */

public class MyPreference {

    private static final String TAG = MyPreference.class.getSimpleName();
    private static SharedPreferences sharedPref;
    private static LoginResponse mLoginResponse;


    /**
     * Set Login response.
     *
     * @param mLoginResponse to save user data.
     */
    public static void setUserLoginData(LoginResponse mLoginResponse) {
        String json = getGsonParser().toJson(mLoginResponse);

        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString("LOGIN_USER_RESPONSE", json);
        editor.commit();
    }


    private static Gson gson;

    /**
     * Add specific parsing to gson
     *
     * @return new instance of {@link Gson}
     */
    public static Gson getGsonParser() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();
        }
        return gson;
    }

    /**
     * Get User Data.
     *
     * @return Logged In user data or null if blank.
     */
    public static LoginResponse getCurrentUser() {
        if (mLoginResponse != null) {
            return mLoginResponse;
        } else {
            SharedPreferences prefs = getSettings();
            String json = prefs.getString("LOGIN_USER_RESPONSE", "");
            if (json.isEmpty() || "null".equals(json)) {

                return null;
            } else {
                mLoginResponse = getGsonParser().fromJson(json, LoginResponse.class);

                return mLoginResponse;
            }
        }
    }

    public static void resetAllData(){
        getSettings().edit().clear().commit();
        mLoginResponse =null;
    }

    /**
     * Obtain preferences instance.
     *
     * @return base instance of app SharedPreferences.
     */
    public static SharedPreferences getSettings() {
        if (sharedPref == null) {
            sharedPref = PioAlert.getInstance().getSharedPreferences(PioAlert.PACKAGE_NAME, Context.MODE_PRIVATE);
        }
        return sharedPref;
    }
}
