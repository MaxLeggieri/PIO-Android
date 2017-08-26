package com.livelife.pioalert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Max on 11/08/2017.
 */

public class PioPlace {

    String placeId,name,address,icon,photoReference;
    Double lat,lon;

    PioPlace(JSONObject json) {
        try {
            placeId = json.getString("place_id");
            name = json.getString("name");
            address = json.getString("vicinity");
            icon = json.getString("icon");

            if (json.has("photos")) {
                JSONArray photoArr = json.getJSONArray("photos");
                photoReference = photoArr.getJSONObject(0).getString("photo_reference");
            }

            JSONObject location = json.getJSONObject("geometry").getJSONObject("location");
            lat = Double.parseDouble(location.getString("lat"));
            lon = Double.parseDouble(location.getString("lng"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
