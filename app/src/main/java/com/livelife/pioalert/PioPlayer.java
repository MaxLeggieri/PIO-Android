package com.livelife.pioalert;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Max on 26/06/2017.
 */

public class PioPlayer {

    String name,imageFullPath;
    int score;
    int rank;
    int uid;
    String coderef;
    public boolean currentUser = false;

    PioPlayer(JSONObject object) {

        try {
            name = object.getString("uname");
            imageFullPath = object.getString("upic");
            score = object.getInt("score");
            rank = object.getInt("pos");
            uid = object.getInt("uid");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    PioPlayer() {}

}
