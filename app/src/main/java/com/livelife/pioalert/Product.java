package com.livelife.pioalert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Max on 04/01/2017.
 */

public class Product implements Serializable {

    public int pid;
    public String calendarType = "";
    public String fromtime = "";
    public String totime = "";
    public String workingDays = "";
    public String name;
    public String descShort;
    public String descLong;
    public String price;
    public String initialPrice;
    public String priceUnit;
    public String discountPercent;
    public String image;
    public int available;
    public int quantity;
    public String prodCat;
    public String saveAmount;
    public int sellingMethod;
    public String userRating = "0";

    public String brandName, companylogo, brandLocation = "";
    public int idCom;

    public int numReviews = 0;
    public double avrReviews = 0.0;

    public ArrayList<String> comcat = new ArrayList<>();
    public ArrayList<String> comcatId = new ArrayList<>();


    Product() {}

    Product(JSONObject jsonObject) {

        try {


            if(jsonObject.has("comcat")) {
                JSONArray comcatJson = jsonObject.getJSONArray("comcat");
                for (int i = 0; i < comcatJson.length(); i++) {
                    String id = comcatJson.getString(i);
                    comcat.add(id);
                }
            }

            if(jsonObject.has("comcatId")) {
                JSONArray comcatIdJson = jsonObject.getJSONArray("comcatId");
                for (int i = 0; i < comcatIdJson.length(); i++) {
                    String id = comcatIdJson.getString(i);
                    comcatId.add(id);
                }
            }

            if (jsonObject.has("rate")) {
                JSONObject rate = jsonObject.getJSONObject("rate");
                numReviews = rate.getInt("votes");
                avrReviews = (float) rate.getDouble("rating_avg");
            }

            if (jsonObject.has("calendarType")){
                this.calendarType = jsonObject.getString("calendarType");
            }
            if (jsonObject.has("totime")){
                this.totime = jsonObject.getString("totime");
            }
            if (jsonObject.has("fromtime")){
                this.fromtime = jsonObject.getString("fromtime");
            }
            if (jsonObject.has("workingDays")){
                this.workingDays = jsonObject.getString("workingDays");
            }
            this.pid = jsonObject.getInt("idp");
            if(jsonObject.has("sellingMethod")) {
                this.sellingMethod = jsonObject.getInt("sellingMethod");
            }
            this.name = jsonObject.getString("name");
            this.descShort = jsonObject.getString("descriptionShort");
            if(jsonObject.has("description")) {
                this.descLong = jsonObject.getString("description");
            }
            this.price = jsonObject.getString("priceSellVatIncluded");
            if(jsonObject.has("priceOff")) {
                this.initialPrice = jsonObject.getString("priceOff");
            }

            if(jsonObject.has("catText")) {
                this.prodCat = jsonObject.getString("catText").replace("&raquo;", "•");
            }

            if(this.initialPrice == null) {
                this.initialPrice = "0";
            }

            if (jsonObject.has("priceUnit")){
                this.priceUnit = jsonObject.getString("priceUnit");
            }

            if (jsonObject.has("scontoPercent")) {
                this.discountPercent = jsonObject.getString("scontoPercent");
            }
            this.image = jsonObject.getString("imgpath");
            if(jsonObject.has("quantity")) {
                this.quantity = jsonObject.getInt("quantity");
            }
            this.available = jsonObject.getInt("quantityAvailable");

            if(jsonObject.has("brandname")) {
                this.brandName = jsonObject.getString("brandname");
            }

            if(jsonObject.has("companylogo")) {
                this.companylogo = jsonObject.getString("companylogo");
            }

            if(jsonObject.has("idcom")) {
                this.idCom = jsonObject.getInt("idcom");
            }

            double save = Double.parseDouble(initialPrice) - Double.parseDouble(price);


            if (save > 0) {
                saveAmount = " -€ "+String.format("%.2f", save);
            }

            if (jsonObject.has("where")) {
                brandLocation = jsonObject.getJSONObject("where").getString("addressloc");
            }

            if (jsonObject.has("myrating")) {
                try {
                    JSONObject mJsonObject1 = jsonObject.getJSONObject("myrating");
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
