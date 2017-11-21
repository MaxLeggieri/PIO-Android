package com.livelife.pioalert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Max on 08/01/2017.
 */

public class Company implements Serializable{

    public int cid;
    public String officialName,brandName,phone,email,image,description;
    public ArrayList<com.livelife.pioalert.Location> locations = new ArrayList<>();
    public ArrayList<CategoryProductModal> comcatList = new ArrayList<>();
    public String userRating = "0";
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
            if(json.has("comcat")) {
                JSONArray comcat = json.getJSONArray("comcat");
                CategoryProductModal mCategoryProductModalFree = new CategoryProductModal("tutti",null,true);
                comcatList.add(mCategoryProductModalFree);
                for (int i = 0; i < comcat.length(); i++) {
                    JSONObject obj = comcat.getJSONObject(i);
                    boolean isSelected  = false;
                /*    if (i==0){
                        isSelected = true;
                    }else{
                        isSelected = false;
                    }*/

                    CategoryProductModal bean = new CategoryProductModal(obj.getString("name"),obj.getString("idcat"),isSelected);
                    comcatList.add(bean);
                }
            }



            if (json.has("myrating")) {
                try {
                    JSONObject mJsonObject1 = json.getJSONObject("myrating");
                    if (mJsonObject1.has("rating")){
                        userRating  =mJsonObject1.getString("rating");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
