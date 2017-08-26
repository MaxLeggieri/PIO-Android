package com.livelife.pioalert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Max on 16/01/2017.
 */

public class Cart {

    public int companyId,sellingMethod;
    public double subTotal;
    public double shippingTotal;
    public String companyName,shippingAddress,companyLogo;
    public ArrayList<Product> products;

    Cart(){}

    Cart(JSONObject obj) {

        products = new ArrayList<>();

        try {
            companyId = obj.getInt("idcom");
            sellingMethod = obj.getInt("sellingMethod");
            subTotal = obj.getDouble("totPriceSellVatIncluded");
            shippingTotal = 0;
            companyName = obj.getString("brandname");
            companyLogo = obj.getString("companyLogo");

            JSONArray jarr = obj.getJSONArray("products");

            for (int i=0; i < jarr.length(); i++) {

                JSONObject o = jarr.getJSONObject(i);
                Product p = new Product(o);
                products.add(p);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
