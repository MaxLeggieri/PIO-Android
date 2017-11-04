package com.livelife.pioalert;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Max on 08/01/2017.
 */

public class Location implements Serializable {

    public int idLoc;
    public String name,address,distanceHuman;
    public Double lat,lng;

    public int numReviews = 0;
    public double avrReviews = 0.0;

    Location (JSONObject object) {

        try {

            idLoc = object.getInt("idlocation");
            name = object.getString("name");
            address = object.getString("address");
            distanceHuman = "a "+object.getString("distance")+" da te";
            lat = object.getDouble("lat");
            lng = object.getDouble("lng");
            if (object.has("rate")) {
                JSONObject rate = object.getJSONObject("rate");
                numReviews = rate.getInt("votes");
                avrReviews = (float) rate.getDouble("rating_avg");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
