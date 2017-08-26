package com.livelife.pioalert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Max on 09/02/2017.
 */

public class Order {

    public int idOrder,idCom,deliveryTime,cutoffTime,timestamp,totalItems;
    public String brandName,brandLogo,subTotal,shipping,total,trackingNumber,shippingAddress,shippingIdentification,shippingConfirmation,createdHuman,deliveryTimeHuman,cutoffTimeHuman,companyEmail,companyModEmail;
    public ArrayList<Product> products = new ArrayList<>();

    Order(JSONObject obj) {
        try {
            idOrder = obj.getInt("idOrder");
            idCom = obj.getInt("idcom");

            companyEmail = obj.getString("companyEmail");
            companyModEmail = obj.getString("companyModEmail");
            brandName = obj.getString("brandname");
            brandLogo = obj.getString("brandlogo");
            subTotal = obj.getString("_price_basketTotalNoShip");
            shipping = obj.getString("_price_basketShip");
            total = obj.getString("_price_basketTotalWithShip");
            trackingNumber = obj.getString("trackingNumber");

            String name = obj.getString("first_name");
            String surname = obj.getString("last_name");
            String address = obj.getString("address");
            String town = obj.getString("town");
            String zip = obj.getString("zip");
            String province = obj.getString("province");
            String tel = obj.getString("tel");

            shippingAddress = name+" "+surname+"\n"+address+"\n"+zip+" "+town+" "+province+"\ntel: "+tel;
            shippingIdentification = obj.getString("ShipmentIdentificationNumber");
            shippingConfirmation = obj.getString("DispatchConfirmationNumber");

            deliveryTime = obj.getInt("DeliveryTime");
            cutoffTime = obj.getInt("CutoffTime");
            timestamp = obj.getInt("createdTimestamp");

            createdHuman = obj.getString("createdHuman");
            deliveryTimeHuman = obj.getString("DeliveryTimeHuman");
            cutoffTimeHuman = obj.getString("CutoffTimeHuman");

            JSONArray prodArray = obj.getJSONArray("products");
            totalItems = 0;
            for (int i=0; i < prodArray.length(); i++) {

                JSONObject pObj = prodArray.getJSONObject(i);
                Product p = new Product(pObj);
                totalItems = totalItems + p.quantity;
                products.add(p);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
