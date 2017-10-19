package com.livelife.pioalert;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Max on 20/05/2017.
 */

public class PioUser {

    public interface PioUserListener {
        void onUserPositionChanged(Location loc);
    }

    public ArrayList<PioUserListener> listeners = new ArrayList<>();


    private static final String tag = PioUser.class.getSimpleName();

    static class SingletonHolder {
        static final PioUser INSTANCE = new PioUser();
    }

    protected PioUser() {
        // Exists only to defeat instantiation.
    }

    public static synchronized PioUser getInstance() {
        return SingletonHolder.INSTANCE;
    }


    Integer [] userCats = new Integer[0];


    int uid = 0;
    boolean logged = false;
    boolean profiled = false;
    boolean consent = false;
    boolean consentGeo = false;
    boolean consentData = false;
    boolean localized = false;
    long maxPromoDistance;

    String userEmail = "";
    String userName = "";
    String lastName = "";
    String userImagePath = "";


    public String shipName,shipSurname,shipAddress,shipZip,shipCity,shipSubCity,shipPhone,shipSummary;

    String promoCode = "";

    Location location;
    double userLat,userLon;

    private Context c;

    public void setContext(Context context) {
        this.c = context;

        updateUser();
    }

    public void addListener(PioUserListener l) {
        Log.v(tag,"Added listener: "+l.getClass().getSimpleName());
        listeners.add(l);

        Log.v(tag,"Got "+listeners.size()+" listeners");


    }

    public void removeListener(PioUserListener l) {
        listeners.remove(l);
    }

    public void updateUser() {
        uid = c.getSharedPreferences("PIO",0).getInt("uid",0);
        logged = c.getSharedPreferences("PIO",0).getBoolean("logged",false);
        profiled = c.getSharedPreferences("PIO",0).getBoolean("profiled",false);
        consent = c.getSharedPreferences("PIO",0).getBoolean("consent",false);
        consentGeo = c.getSharedPreferences("PIO",0).getBoolean("consentGeo",false);
        consentData = c.getSharedPreferences("PIO",0).getBoolean("consentData",false);
        localized = c.getSharedPreferences("PIO",0).getBoolean("localized",false);

        userImagePath = c.getSharedPreferences("PIO",0).getString("userImagePath","");
        userName = c.getSharedPreferences("PIO",0).getString("userName","");
        lastName = c.getSharedPreferences("PIO",0).getString("lastName","");
        userEmail = c.getSharedPreferences("PIO",0).getString("userEmail","");

        //maxPromoDistance = c.getSharedPreferences("PIO",0).getLong("maxPromoDistance",50000);
        shipName = c.getSharedPreferences("PIO",0).getString("shippingName",null);
        shipSurname = c.getSharedPreferences("PIO",0).getString("shippingSurname",null);
        shipAddress = c.getSharedPreferences("PIO",0).getString("shippingAddress",null);
        shipZip = c.getSharedPreferences("PIO",0).getString("shippingZip",null);
        shipCity = c.getSharedPreferences("PIO",0).getString("shippingCity",null);
        shipSubCity = c.getSharedPreferences("PIO",0).getString("shippingSubCity",null);
        shipPhone = c.getSharedPreferences("PIO",0).getString("shippingPhone",null);
        shipSummary = shipName+" "+shipSurname+"\n"+shipAddress+"\n"+shipZip+" "+shipCity+" ("+shipSubCity+")";
        promoCode = c.getSharedPreferences("PIO",0).getString("promoCode","");
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
        SharedPreferences.Editor editor = c.getSharedPreferences("PIO",0).edit();
        editor.putBoolean("logged", logged);
        editor.commit();

    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
        SharedPreferences.Editor editor = c.getSharedPreferences("PIO",0).edit();
        editor.putString("promoCode", promoCode);
        editor.commit();
    }

    public void setProfiled(boolean profiled) {
        this.profiled = profiled;
        SharedPreferences.Editor editor = c.getSharedPreferences("PIO",0).edit();
        editor.putBoolean("profiled", profiled);
        editor.commit();

    }

    public void setConsent(boolean consent) {
        this.consent = consent;
        SharedPreferences.Editor editor = c.getSharedPreferences("PIO",0).edit();
        editor.putBoolean("consent", consent);
        editor.commit();

    }

    public void setUid(int uid) {
        this.uid = uid;
        SharedPreferences.Editor editor = c.getSharedPreferences("PIO",0).edit();
        editor.putInt("uid", uid);
        editor.commit();
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        SharedPreferences.Editor editor = c.getSharedPreferences("PIO",0).edit();
        editor.putString("userEmail", userEmail);
        editor.commit();
    }

    public void setUserNameAndImage(String userName, String lastName, String userImagePath) {
        this.userName = userName;
        this.userImagePath = userImagePath;
        this.lastName = lastName;

        SharedPreferences.Editor editor = c.getSharedPreferences("PIO",0).edit();
        editor.putString("userName", userName);
        editor.putString("lastName", lastName);
        editor.putString("userImagePath", userImagePath);
        editor.commit();
    }


    Location lastLocation;

    public void setUserLocation(Location loc, Context bgc) {

        if (loc == null) return;

        Log.v(tag,"setUserLocation, got "+listeners.size()+" listeners");

        this.location = loc;
        userLat = loc.getLatitude();
        userLon = loc.getLongitude();

        lastLocation = loc;


        SharedPreferences.Editor editor = bgc.getSharedPreferences("PIO",Context.MODE_PRIVATE).edit();
        //if (editor == null) return;

        editor.putFloat("userLat", (float) userLat);
        editor.putFloat("userLon", (float) userLon);
        editor.commit();

        for (PioUserListener l:listeners) {
            Log.v(tag,"Dispatched to: "+l.getClass().getSimpleName());
            l.onUserPositionChanged(this.location);
        }
    }

    public void setMaxPromoDistance(long maxPromoDistance) {
        this.maxPromoDistance = maxPromoDistance;

        SharedPreferences.Editor editor = c.getSharedPreferences("PIO",0).edit();
        editor.putLong("maxPromoDistance", maxPromoDistance);
        editor.commit();
    }

    public void setShippingAddress(JSONObject shippingData) {


        try {

            SharedPreferences.Editor editor = c.getSharedPreferences("PIO",0).edit();
            editor.putString("shippingName", shippingData.getString("first_name"));
            editor.putString("shippingSurname", shippingData.getString("last_name"));
            editor.putString("shippingAddress", shippingData.getString("address"));
            editor.putString("shippingZip", shippingData.getString("zip"));
            editor.putString("shippingCity", shippingData.getString("town"));
            editor.putString("shippingSubCity", shippingData.getString("province"));
            editor.putString("shippingPhone", shippingData.getString("tel"));
            editor.apply();

            updateUser();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
