package com.livelife.pioalert;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Max on 15/06/16.
 */
public class Category {

    public int level;
    public int cid;
    public String name;
    public String imgPath;
    public JSONObject jsonObject;
    public boolean selected = false;

    Category() {}

    Category(JSONObject jsonObject) {

        try {
            this.cid = jsonObject.getInt("id");
            this.level = 0;
            this.name = jsonObject.getString("name");
            this.imgPath = jsonObject.getString("icon");
            this.jsonObject = jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
