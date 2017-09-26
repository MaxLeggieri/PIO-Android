package com.livelife.pioalert;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Max on 22/07/16.
 */
public class Promo {

    int pid;
    int brandId;
    String desc;
    String relatedProductsIds = "";
    String imagePath;
    String prodName;
    String prodSpecs;
    String title;
    String viewedCount;
    String brandName;
    String address = "";
    String catHuman;
    String distanceHuman;
    String youtube;
    String youtubeImg;
    String link;
    String attachment;
    String cimage;
    String couponCode;
    String expiration;
    ArrayList<String> gallery;

    int usedCoupon;
    Boolean liked;
    double lat;
    double lng;

    Promo(){}

    Promo(JSONObject jsonObject, boolean minified) {




        try {
            pid = jsonObject.getInt("idad");
            brandId = jsonObject.getInt("idcom");
            desc = jsonObject.getString("description");
            imagePath = jsonObject.getString("image");
            if (jsonObject.has("relatedProductsIds")){
                relatedProductsIds = jsonObject.getString("relatedProductsIds");
            }
            prodName = jsonObject.getString("products");
            title = jsonObject.getString("title");
            distanceHuman = jsonObject.getString("distanceHuman");
            expiration = jsonObject.getString("expiration_it");
            int interesteduser = Integer.parseInt(jsonObject.getString("interesteduser"));
            if(interesteduser > 0) {
                liked = true;
            } else {
                liked = false;
            }

            /*
            SimpleDateFormat sdf = new SimpleDateFormat("d MMM y", Locale.getDefault());
            expiration = sdf.format(jsonObject.getLong("expiration")*1000);
             */




            if (!minified) {

                prodSpecs = jsonObject.getString("catText");
                viewedCount = jsonObject.getString("views");
                brandName = jsonObject.getString("brandname");
                address = jsonObject.getString("address");
                catHuman = jsonObject.getString("catText");
                youtube = jsonObject.getString("youtube");
                if (jsonObject.has("youtubeImg")) {
                    youtubeImg = jsonObject.getString("youtubeImg");
                }
                link = jsonObject.getString("link");
                attachment = jsonObject.getString("attachment");
                cimage = jsonObject.getString("companylogo");
                couponCode = jsonObject.getString("couponcode");

                if (jsonObject.get("usedCoupon") instanceof Boolean) {
                    boolean uc = jsonObject.getBoolean("usedCoupon");
                    if (uc) {
                        usedCoupon = 0;
                    }
                } else {
                    usedCoupon = jsonObject.getInt("usedCoupon");
                }
                lat = Double.parseDouble(jsonObject.getString("lat"));
                lng = Double.parseDouble(jsonObject.getString("lng"));

                if (jsonObject.has("gallery")) {
                    String imgs = jsonObject.getString("gallery");
                    //gallery = (ArrayList<String>) Arrays.asList(imgs);

                    if (!imgs.equals("")) {
                        String[] gArr = imgs.split(",");
                        gallery = new ArrayList<>();
                        for (String i:gArr) {
                            gallery.add("http://pioalert.com"+i);
                        }

                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
