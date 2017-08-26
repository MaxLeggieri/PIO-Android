package com.livelife.pioalert;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Max on 08/01/2017.
 */

public class Company {

    public int cid;
    public String officialName,brandName,phone,email,image,description;
    public ArrayList<com.livelife.pioalert.Location> locations = new ArrayList<>();

    Company(){}

    Company (JSONObject json) {

        try {
            cid = json.getInt("idcom");
            officialName = json.getString("officialname");
            brandName = json.getString("brandname");
            phone = json.getString("phone");
            email = json.getString("email");
            if(json.has("companylogo")) {
                image = json.getString("companylogo");
            }
            else if(json.has("image")) {
                image = json.getString("image");
            }
            description = json.getString("description");

            if(json.has("loc")) {
                JSONArray locs = json.getJSONArray("loc");
                for (int i = 0; i < locs.length(); i++) {
                    JSONObject obj = locs.getJSONObject(i);
                    com.livelife.pioalert.Location l = new com.livelife.pioalert.Location(obj);
                    locations.add(l);
                }
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
