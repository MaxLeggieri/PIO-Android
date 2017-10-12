package com.livelife.pioalert;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Max on 20/05/2017.
 */

public class WebApi {

    private static final String tag = WebApi.class.getSimpleName();

    static class SingletonHolder {
        static final WebApi INSTANCE = new WebApi();
    }

    protected WebApi() {
        // Exists only to defeat instantiation.
    }

    public static synchronized WebApi getInstance() {
        return SingletonHolder.INSTANCE;
    }

    static private MainActivity mainActivity;

    public void setMainActivity(MainActivity ma) {
        this.mainActivity = ma;
    }

    public String deviceToken = "";
    public String apiAddress = "https://www.pioalert.com/api/";

    //public ArrayList<Category> allCategories;
    public ArrayList<Category> allPromoCats;
    public ArrayList<Category> allProductCats;
    public ArrayList<Category> allCompanyCats;

    /*
    public void setAllCategories() {
        allCategories = getAllCategories();
    }
    */


    boolean connectionStarted = false;
    boolean connectionAvailable = false;
    Handler  connectionChecker = new Handler();
    Runnable connRunnable = new Runnable() {
        @Override
        public void run() {
            connectionAvailable = isNetworkAvailable();
            connectionChecker.postDelayed(connRunnable,7000);
        }
    };


    static private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)  mainActivity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private JSONObject getGeneralJsonObjectFromUrl(final String path) throws ExecutionException, InterruptedException {

        if (!connectionStarted) {
            //connectionChecker.postDelayed(connRunnable,7000);
        }

        if (!connectionAvailable) {
            //return new JSONObject();
        }

        return new GetGeneralAsyncTask().execute(path).get();

    }

    private JSONObject getJsonObjectFromUrl(final String path) throws ExecutionException, InterruptedException {

        if (!connectionStarted) {
            //connectionChecker.postDelayed(connRunnable,7000);
        }

        if (!connectionAvailable) {
            //return new JSONObject();
        }

        return new GetAsyncTask().execute(path).get();

    }

    private void getJsonObjectFromUrlAsync(final String path) throws ExecutionException, InterruptedException {

        new GetAsyncTask().execute(path);

    }

    public class GetAsyncTask extends AsyncTask<String,Void,JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {


            URL url = null;
            String path = strings[0];
            try {
                url = new URL(path);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setUseCaches(false);
                urlConnection.setAllowUserInteraction(false);

                // Check the connection status
                if(urlConnection.getResponseCode() == 200)
                {

                    Log.v(tag,"Server connected...");
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    // Read the BufferedInputStream
                    BufferedReader r = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        sb.append(line);
                    }

                    JSONObject result = new JSONObject(sb.toString()).getJSONObject("response");
                    return result;
                }


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return errorObject();

        }
    }

    public class GetGeneralAsyncTask extends AsyncTask<String,Void,JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {


            URL url = null;
            String path = strings[0];
            try {
                url = new URL(path);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setUseCaches(false);
                urlConnection.setAllowUserInteraction(false);

                // Check the connection status
                if(urlConnection.getResponseCode() == 200)
                {

                    Log.v(tag,"Server connected...");
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    // Read the BufferedInputStream
                    BufferedReader r = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        sb.append(line);
                    }

                    JSONObject result = new JSONObject(sb.toString());
                    return result;
                }


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return errorObject();

        }
    }

    public JSONObject errorObject() {
        JSONObject error = new JSONObject();
        try {
            error.put("response",false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return error;
    }

    //Context fbContext;
    private void postJsonObjectFromUrl(final String path, String json, Context c, String coderef) throws ExecutionException, InterruptedException {

        //fbContext = c;
        new PostAsyncTask().execute(path,json,coderef);

    }

    public class PostAsyncTask extends AsyncTask<String,Void,JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {


            JSONObject jsonParam = new JSONObject();

            URL url;
            HttpURLConnection urlConnection;
            BufferedReader reader;
            String path = strings[0];
            String json = strings[1];
            String coderef = strings[2];
            try {


                jsonParam.put("method", "sendFbUserData");
                jsonParam.put("device_token", deviceToken);
                jsonParam.put("coderef",coderef);
                jsonParam.put("fbUserData", new JSONObject(json));


                url = new URL(path);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");


                //set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(jsonParam.toString());
                // json data
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();
                //input stream
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }

                JSONObject result = new JSONObject(buffer.toString()).getJSONObject("response");
                PioUser.getInstance().setUid(result.getInt("uid"));
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new JSONObject();

        }
    }

    /*

    Methods

     */


    public JSONObject sendGoogleData(GoogleSignInAccount account,String coderef) {


        String query = apiAddress+"?method=sendGoogleUserData";


        try {
            query += "&device_token="+deviceToken;
            query += "&displayName="+URLEncoder.encode(account.getGivenName(), "UTF-8");
            query += "&email=" + URLEncoder.encode(account.getEmail(), "UTF-8");
            query += "&id="+account.getId();
            query += "&idToken="+account.getIdToken();
            query += "&image="+account.getPhotoUrl().getPath();
            query += "&serverAuthCode="+account.getServerAuthCode();
            query += "&coderef="+coderef;

            Log.v(tag,"Calling: "+query);

            return getJsonObjectFromUrl(query);
        } catch (UnsupportedEncodingException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new JSONObject();
        }





    }


    public void sendFacebookData(String json, Context c, String coderef) {


        try {
            postJsonObjectFromUrl(apiAddress,json,c,coderef);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //return new JSONObject();

    }


    public ArrayList<Category> getAllCategories() {


        ArrayList<Category> cats = new ArrayList<>();
        String query = apiAddress+"?method=categories_all";

        try {

            Log.v(tag,"Calling: "+query);

            JSONObject object = getJsonObjectFromUrl(query);
            JSONArray data = object.getJSONArray("data");


            //Log.v(tag,"Cat num: "+data.length());

            for(int i=0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);

                Category cat = new Category(jsonObject);
                cats.add(cat);
            }

            return cats;

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cats;

    }

    public boolean setUserCategories(String cats) {

        String query = apiAddress+"?method=ucategory_on";


        try {
            query += "&device_token="+deviceToken;
            query += "&idcat="+cats;
            query += "&uid="+PioUser.getInstance().uid;


            Log.v(tag,"Calling: "+query);

            JSONObject object = getJsonObjectFromUrl(query);
            Log.v(tag,object.toString(2));

            if (object.getBoolean("response")) {
                return true;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public JSONObject signupMissing() {


        String query = apiAddress+"?method=signupMissing";

        try {
            query += "&device_token="+deviceToken;
            query += "&uid="+PioUser.getInstance().uid;

            Log.e(tag,"Calling: "+query);

            return getJsonObjectFromUrl(query);


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;

    }


    public JSONObject home(int page, String searchTerm, int idcat) {

        String query = apiAddress+"?method=home";

        try {
            query += "&device_token="+deviceToken;
            query += "&uid="+PioUser.getInstance().uid;
            query += "&page="+page;
            query += "&lat="+PioUser.getInstance().location.getLatitude();
            query += "&lng="+PioUser.getInstance().location.getLongitude();
            if (searchTerm != null) {
                query += "&idcat="+idcat;
                query += "&search="+URLEncoder.encode(searchTerm, "UTF-8");
            } else {
                query += "&idcat=favorite";
            }

            Log.e(tag,"Calling: "+query);
            return getJsonObjectFromUrl(query);

        } catch (UnsupportedEncodingException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return errorObject();

    }

    public JSONArray autosuggest(String text) {

        String query = apiAddress+"?method=autosuggest";

        try {
            query += "&device_token="+deviceToken;
            query += "&uid="+PioUser.getInstance().uid;
            query += "&terms="+URLEncoder.encode(text,"UTF-8");

            Log.e(tag,"Calling: "+query);

            JSONObject response = getJsonObjectFromUrl(query);

            return response.getJSONArray("data");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return new JSONArray();
    }


    public Promo getPromoById(int promoId) {

        if (promoId==0) return new Promo();

        String query = apiAddress+"?method=getAdById";

        query += "&device_token="+deviceToken;
        query += "&uid="+PioUser.getInstance().uid;
        query += "&ids="+promoId;
        query += "&rec=1";
        query += "&lat="+PioUser.getInstance().location.getLatitude();
        query += "&lng="+PioUser.getInstance().location.getLongitude();

        Log.e(tag,"Calling: "+query);

        try {
            JSONObject response = getJsonObjectFromUrl(query);
            JSONArray prodJson = response.getJSONObject("data").getJSONObject("ads").getJSONArray("d");

            return new Promo((JSONObject) prodJson.get(0),false);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new Promo();

    }

    public JSONObject useCoupon(String couponCode, int promoId) {


        String query = apiAddress+"?method=useCoupon";

        try {
            query += "&uid=" + PioUser.getInstance().uid;
            query += "&idad=" + promoId;
            query += "&couponcode=" + couponCode;

            Log.e(tag,"Calling: "+query);

            return getJsonObjectFromUrl(query);
        } catch ( InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new JSONObject();
        }





    }


    /*
    public void sendBeaconData(Beacon beacon, Context context) {
        String query = apiAddress+"?method=sendBeaconData";

        if (deviceToken.equals("")) {
            Log.e(tag,"No DEVICE TOKEN");
            return;
        }

        if (PioUser.getInstance().uid == 0) {
            Log.e(tag,"No UID");
            return;
        }

        query += "&device_token="+deviceToken;
        query += "&uid="+PioUser.getInstance().uid;
        query += "&uuid="+beacon.getId1().toUuid().toString();
        query += "&idmajor="+beacon.getId2().toInt();
        query += "&idminor="+beacon.getId3().toInt();
        query += "&accuracy="+beacon.getDistance();

        //Log.e(tag,"Calling: "+query);

        try {
            getJsonObjectFromUrl(query);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }
    */

    public void tokenHandler(String notificationToken) {

        String query = apiAddress+"?method=tokenHandler";
        try {
            query += "&device_token=" + deviceToken;
            query += "&notification_token=" + notificationToken;
            query += "&uid=" + PioUser.getInstance().uid;
            query += "&dev=" + URLEncoder.encode(String.format("%s %s|%s", Build.BRAND, android.os.Build.PRODUCT, android.os.Build.VERSION.RELEASE), "UTF-8");
            query += "&os=android";

            Log.e(tag,"Calling: "+query);

            getJsonObjectFromUrl(query);
        } catch (UnsupportedEncodingException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    public void likeAd(final Boolean like, final int promoId) {


        String m = like?"like":"unlike";
        String query = apiAddress+"?method="+m;
        try {
            query += "&uid=" + PioUser.getInstance().uid;
            query += "&idad=" + promoId;
            query += "&lat=" + PioUser.getInstance().location.getLatitude();
            query += "&lng=" + PioUser.getInstance().location.getLongitude();

            Log.e(tag,"Calling: "+query);

            getJsonObjectFromUrlAsync(query);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


    }

    public ArrayList<Product> getAllProductsByIds(String ids){
        ArrayList<Product> products = new ArrayList<>();

        String query = apiAddress+"?method=product";
        try {
            query += "&idproduct="+ids;
            query += "&lat=" + PioUser.getInstance().location.getLatitude();
            query += "&lng=" + PioUser.getInstance().location.getLongitude();

            Log.e(tag,"Calling: "+query);

            JSONObject response = getJsonObjectFromUrl(query);
            JSONObject dataObj = response.getJSONObject("data");

            if(!dataObj.has("products")) {
                return products;
            }

            JSONArray d = dataObj.getJSONArray("products");



            for(int i=0; i < d.length(); i++) {
                JSONObject jsonObject = d.getJSONObject(i);

                Product p = new Product(jsonObject);
                products.add(p);

            }


            return products;


        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }


        return products;
    }

    public Product getProductById(int idp) {

        String query = apiAddress+"?method=product";
        try {
            query += "&device_token=" + deviceToken;
            query += "&uid=" + PioUser.getInstance().uid;
            query += "&idproduct="+idp;
            query += "&lat=" + PioUser.getInstance().location.getLatitude();
            query += "&lng=" + PioUser.getInstance().location.getLongitude();

            Log.e(tag,"Calling: "+query);

            JSONObject response = getJsonObjectFromUrl(query);
            JSONArray prodJson = response.getJSONObject("data").getJSONArray("products");

            return new Product((JSONObject) prodJson.get(0));

        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }

        return new Product();

    }

    public ArrayList<Company> getCompanies(int page, String cats, String partner) {

        ArrayList<Company> companies = new ArrayList<>();

        String query = apiAddress+"?method=companies";
        query += "&device_token=" + deviceToken;
        query += "&uid=" + PioUser.getInstance().uid;
        query += "&rec=20";
        query += "&page="+page;
        query += "&idcat="+cats;
        query += "&lat=" + PioUser.getInstance().location.getLatitude();
        query += "&lng=" + PioUser.getInstance().location.getLongitude();
        if (partner != null) {
            query += "&mode=distance";
            query += "&partner="+partner;
        }

        Log.e(tag,"Calling: "+query);


        try {
            JSONObject response = getJsonObjectFromUrl(query);
            JSONObject data = response.getJSONObject("data");
            if (data.has("products")) {
                JSONArray p = data.getJSONArray("products");

                for(int i=0; i < p.length(); i++) {
                    JSONObject jsonObject = p.getJSONObject(i);

                    Company c = new Company(jsonObject);
                    companies.add(c);

                }

                return companies;
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return companies;

    }

    public Company getCompanyById(int idc) {

        String query = apiAddress+"?method=companies";
        try {
            query += "&device_token=" + deviceToken;
            query += "&uid=" + PioUser.getInstance().uid;
            query += "&rec=1";
            query += "&idcom="+idc;
            query += "&lat=" + PioUser.getInstance().location.getLatitude();
            query += "&lng=" + PioUser.getInstance().location.getLongitude();

            Log.e(tag,"Calling: "+query);

            JSONObject response = getJsonObjectFromUrl(query);
            JSONObject data = response.getJSONObject("data");
            if (data.has("products")) {
                JSONArray prodJson = data.getJSONArray("products");
                return new Company((JSONObject) prodJson.get(0));
            }




        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }

        return new Company();

    }


    public ArrayList<Promo> companyAds(int idCom) {

        ArrayList<Promo> promos = new ArrayList<>();

        String query = apiAddress+"?method=companyAdsDist";
        try {
            query += "&device_token=" + deviceToken;
            query += "&uid=" + PioUser.getInstance().uid;
            query += "&idcom="+idCom;
            query += "&lat=" + PioUser.getInstance().location.getLatitude();
            query += "&lng=" + PioUser.getInstance().location.getLongitude();

            Log.e(tag,"Calling: "+query);

            JSONObject response = getJsonObjectFromUrl(query);

            if(response.has("data")) {

                JSONArray d = response.getJSONObject("data").getJSONObject("ads").getJSONArray("d");


                for(int i=0; i < d.length(); i++) {
                    JSONObject jsonObject = d.getJSONObject(i);

                    Promo p = new Promo(jsonObject,true);
                    promos.add(p);

                }

                return promos;
            }



        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }

        return promos;

    }


    public ArrayList<Product> companyProducts(int idCom) {
        ArrayList<Product> products = new ArrayList<>();

        String query = apiAddress+"?method=companyProducts";
        try {
            query += "&device_token=" + deviceToken;
            query += "&uid=" + PioUser.getInstance().uid;
            query += "&idcom="+idCom;
            query += "&ord=lastin";
            query += "&lat=" + PioUser.getInstance().location.getLatitude();
            query += "&lng=" + PioUser.getInstance().location.getLongitude();

            Log.e(tag,"Calling: "+query);

            JSONObject response = getJsonObjectFromUrl(query);
            JSONObject dataObj = response.getJSONObject("data");

            if(!dataObj.has("products")) {
                return products;
            }

            JSONArray d = dataObj.getJSONArray("products");



            for(int i=0; i < d.length(); i++) {
                JSONObject jsonObject = d.getJSONObject(i);

                Product p = new Product(jsonObject);
                products.add(p);

            }


            return products;


        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }

        return products;
    }

    public ArrayList<Promo> getAds2user(int page, String cat) {

        ArrayList<Promo> promos = new ArrayList<>();

        String query = apiAddress+"?method=ads2user";
        query += "&device_token=" + deviceToken;
        query += "&uid=" + PioUser.getInstance().uid;
        query += "&lat=" + PioUser.getInstance().location.getLatitude();
        query += "&lng=" + PioUser.getInstance().location.getLongitude();
        query += "&catlev=3";
        query += "&idcategory="+cat;
        query += "&maxdist="+PioUser.getInstance().maxPromoDistance;
        query += "&page="+page;

        query += "&rec=20";


        Log.e(tag,"Calling: "+query);

        try {
            JSONObject response = getJsonObjectFromUrl(query);
            JSONObject ads = response.getJSONObject("data").getJSONObject("ads");

            if(!ads.has("d")) {
                return promos;
            }

            JSONArray d = ads.getJSONArray("d");

            for(int i=0; i < d.length(); i++) {
                JSONObject jsonObject = d.getJSONObject(i);

                Promo p = new Promo(jsonObject,true);
                promos.add(p);

            }


            return promos;
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }


        return promos;

    }


    public ArrayList<Product> productsByCats(String cats, int page) {

        ArrayList<Product> prods = new ArrayList<>();

        String query  = apiAddress+"?method=productsByCats";
        query += "&device_token=" + deviceToken;
        query += "&uid=" + PioUser.getInstance().uid;
        query += "&lat=" + PioUser.getInstance().location.getLatitude();
        query += "&lng=" + PioUser.getInstance().location.getLongitude();
        query += "&rec=20";
        query += "&page=" + page;
        query += "&idcats=" + cats;

        Log.e(tag,"Calling: "+query);


        try {
            JSONObject response = getJsonObjectFromUrl(query);
            JSONObject data = response.getJSONObject("data");

            if(!data.has("products")) {
                return prods;
            }

            JSONArray d = data.getJSONArray("products");

            for(int i=0; i < d.length(); i++) {
                JSONObject jsonObject = d.getJSONObject(i);

                Product p = new Product(jsonObject);
                prods.add(p);

            }


            return prods;
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }


        return prods;



    }


    public Cart basketShow(int idCom) {

        Cart cart = new Cart();

        String query  = apiAddress+"?method=basketShow";
        query += "&device_token=" + deviceToken;
        query += "&uid=" + PioUser.getInstance().uid;
        query += "&idcom="+idCom;

        Log.e(tag,"Calling: "+query);

        try {
            JSONObject response = getJsonObjectFromUrl(query);
            JSONObject d = response.getJSONObject("d");

            int totalresults = d.getJSONObject("O").getInt("totalresults");

            if(totalresults > 0) {

                JSONArray jarr = d.getJSONArray("data");

                JSONObject o = jarr.getJSONObject(0);

                return new Cart(o);

            } else {
                return cart;
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }


        return cart;

    }


    public boolean basketMove(int idProduct, int quantity) {

        String query  = apiAddress+"?method=basketMove";
        query += "&device_token=" + deviceToken;
        query += "&uid=" + PioUser.getInstance().uid;
        query += "&idp=" + idProduct;
        query += "&quantity=" + quantity;

        Log.e(tag,"Calling: "+query);

        try {
            JSONObject response = getJsonObjectFromUrl(query);
            return  response.getBoolean("response");
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }


        return false;
    }

    public boolean emailPrenotation(int idCom, String message) {

        String query  = apiAddress+"?method=basket2emailPrenotation";
        query += "&device_token=" + deviceToken;
        query += "&uid=" + PioUser.getInstance().uid;
        query += "&idcom=" + idCom;
        try {
            query += "&msg=" + URLEncoder.encode(message, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.e(tag,"Calling: "+query);

        try {
            JSONObject response = getJsonObjectFromUrl(query);
            return  response.getBoolean("response");
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return false;


    }


    public ArrayList<String> getUserCats() {

        ArrayList<String> userCats = new ArrayList<>();

        String query  = apiAddress+"?method=usercats";
        query += "&device_token=" + deviceToken;
        query += "&uid=" + PioUser.getInstance().uid;

        Log.e(tag,"Calling: "+query);

        try {
            JSONObject response = getJsonObjectFromUrl(query);
            JSONObject data = response.getJSONObject("data");
            JSONArray cats = data.getJSONArray("idcat");


            for (int i = 0; i < cats.length(); i++) {
                String cid = cats.getString(i);
                userCats.add(cid);
            }

            Log.e(tag,userCats.toString());

            return userCats;


        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return userCats;

    }

    public ArrayList<Cart> basketShowAll() {

        ArrayList<Cart> carts = new ArrayList<>();

        String query  = apiAddress+"?method=basketShow";
        query += "&device_token=" + deviceToken;
        query += "&uid=" + PioUser.getInstance().uid;

        Log.e(tag,"Calling: "+query);

        try {
            JSONObject response = getJsonObjectFromUrl(query);
            if (!response.has("d")) {
                return carts;
            }
            JSONObject d = response.getJSONObject("d");

            int totalresults = d.getJSONObject("O").getInt("totalresults");

            if(totalresults > 0) {

                JSONArray jarr = d.getJSONArray("data");

                for (int i = 0; i < jarr.length(); i++) {

                    JSONObject o = jarr.getJSONObject(i);
                    Cart cart = new Cart(o);

                    carts.add(cart);

                }

                return carts;

            }


        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }



        return carts;
    }

    public ArrayList<Promo> adsNotified() {

        ArrayList<Promo> p = new ArrayList<>();

        String query  = apiAddress+"?method=adsNotified";
        query += "&device_token=" + deviceToken;
        query += "&uid=" + PioUser.getInstance().uid;
        query += "&lat=" + PioUser.getInstance().location.getLatitude();
        query += "&lng=" + PioUser.getInstance().location.getLongitude();
        query += "&limit=3";

        Log.e(tag,"Calling: "+query);

        try {
            JSONObject response = getJsonObjectFromUrl(query);
            JSONObject ads = response.getJSONObject("data").getJSONObject("ads");
            if(!ads.has("d")) {
                return p;
            }

            JSONArray prodJson = ads.getJSONArray("d");

            for (int i = 0; i < prodJson.length(); i++) {
                Promo pp = new Promo(prodJson.getJSONObject(i),false);
                p.add(pp);
            }

            return p;

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return p;
    }

    public void notificationsRead(String ids, String timeref) {

        String query  = apiAddress+"?method=notificationsRead";
        query += "&device_token=" + deviceToken;
        query += "&uid=" + PioUser.getInstance().uid;
        query += "&lat=" + PioUser.getInstance().location.getLatitude();
        query += "&lng=" + PioUser.getInstance().location.getLongitude();
        query += "&ids=" + ids;
        query += "&timeref=" + timeref;


        Log.e(tag,"Calling: "+query);


        try {
            getJsonObjectFromUrl(query);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


    }

    public ArrayList<Order> orders() {

        ArrayList<Order> orders = new ArrayList<>();

        String query  = apiAddress+"?method=orders";
        query += "&device_token=" + deviceToken;
        query += "&uid=" + PioUser.getInstance().uid;

        Log.e(tag,"Calling: "+query);


        try {
            JSONObject response = getJsonObjectFromUrl(query);
            JSONObject dataObj = response.getJSONObject("data");

            int totalresults = dataObj.getJSONObject("O").getInt("totalresults");

            if(totalresults > 0) {

                JSONArray jarr = dataObj.getJSONArray("d");

                for (int i = 0; i < jarr.length(); i++) {

                    JSONObject o = jarr.getJSONObject(i);
                    Order ord = new Order(o);

                    orders.add(ord);

                }

                return orders;

            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return orders;

    }

    public ArrayList<PioPlace> getGooglePlaces() {

        ArrayList<PioPlace> result = new ArrayList<>();


        String params = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+PioUser.getInstance().location.getLatitude()+","+PioUser.getInstance().location.getLongitude();
        params += "&radius=5000";
        params += "&language=it";
        params += "&type=museum|art_gallery|church";
        params += "&key=AIzaSyDRV45yi1TJZDx3rCNe5S-9qmRy3AtonPI";

        Log.e(tag,"Calling: "+params);

        try {
            JSONObject response = getGeneralJsonObjectFromUrl(params);

            JSONArray results = response.getJSONArray("results");

            for (int i=0; i < results.length(); i++) {
                JSONObject obj = results.getJSONObject(i);

                PioPlace pp = new PioPlace(obj);
                result.add(pp);
            }

            return result;

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return  result;
    }

    public JSONObject getPoiDetails(String pid) {

        String params = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+pid;
        params += "&language=it";
        params += "&key=AIzaSyDRV45yi1TJZDx3rCNe5S-9qmRy3AtonPI";

        Log.e(tag,"Calling: "+params);

        try {
            JSONObject response = getGeneralJsonObjectFromUrl(params);

            JSONObject result = response.getJSONObject("result");

            return result;

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return new JSONObject();
    }

    public ArrayList<PioPlayer> ranking(int rec) {

        ArrayList<PioPlayer> players = new ArrayList<>();

        Date date = new Date();
        DateFormat monthFormat = new SimpleDateFormat("M");
        DateFormat yearFormat = new SimpleDateFormat("yyyy");

        String query  = apiAddress+"?method=usersParade";
        query += "&device_token=" + deviceToken;
        query += "&uid=" + PioUser.getInstance().uid;
        query += "&rec="+rec;
        query += "&month="+monthFormat.format(date);
        query += "&year="+yearFormat.format(date);

        Log.e(tag,"Calling: "+query);

        try {
            JSONObject response = getJsonObjectFromUrl(query);
            JSONObject data = response.getJSONObject("data");
            JSONArray pArray = data.getJSONArray("d");

            for (int i = 0; i < pArray.length(); i++) {
                JSONObject obj = pArray.getJSONObject(i);

                PioPlayer p = new PioPlayer(obj);

                if (p.uid == PioUser.getInstance().uid) p.currentUser = true;

                players.add(p);
            }



            PioPlayer you = new PioPlayer();
            you.name = PioUser.getInstance().userName;
            you.imageFullPath = PioUser.getInstance().userImagePath;

            JSONObject youObj = data.getJSONObject("you");
            you.score = youObj.getInt("score");
            you.rank = youObj.getInt("pos");
            you.currentUser = true;

            JSONObject youTotalObj = data.getJSONObject("youTotal");
            you.coderef = youTotalObj.getString("code");

            if (you.rank > rec || you.rank == 0) {
                players.add(you);
            }

            return players;

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return players;

    }

    PioPlayer currentPlayer;
    public PioPlayer userRanking() {


        Date date = new Date();
        DateFormat monthFormat = new SimpleDateFormat("M");
        DateFormat yearFormat = new SimpleDateFormat("yyyy");

        String query  = apiAddress+"?method=usersParade";
        query += "&device_token=" + deviceToken;
        query += "&uid=" + PioUser.getInstance().uid;
        query += "&rec=1";
        query += "&month="+monthFormat.format(date);
        query += "&year="+yearFormat.format(date);

        Log.e(tag,"Calling: "+query);

        try {
            JSONObject response = getJsonObjectFromUrl(query);
            JSONObject youObj = response.getJSONObject("data").getJSONObject("you");
            JSONObject youTotalObj = response.getJSONObject("data").getJSONObject("youTotal");
            PioPlayer you = new PioPlayer();
            you.name = PioUser.getInstance().userName;
            you.imageFullPath = PioUser.getInstance().userImagePath;

            you.score = youObj.getInt("score");
            you.rank = youObj.getInt("pos");
            you.currentUser = true;
            you.coderef = youTotalObj.getString("code");
            currentPlayer = you;

            return you;
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return new PioPlayer();

    }

    public ArrayList<String> claim() {
        ArrayList<String> c = new ArrayList<>();

        String query  = apiAddress+"?method=claim";

        Log.e(tag,"Calling: "+query);

        try {
            JSONObject response = getJsonObjectFromUrl(query);
            String claim = response.getString("claim");
            String claimText = response.getString("claimText");

            c.add(claim);
            c.add(claimText);

            return c;

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return c;
    }



    public JSONObject getDhlRate(Context context, int idCom) {

        HttpURLConnection c = null;
        try {

            String data  = URLEncoder.encode("method", "UTF-8") + "=" + URLEncoder.encode("getDhlRate", "UTF-8");
            data += "&" + URLEncoder.encode("uid", "UTF-8") + "=" + URLEncoder.encode(""+PioUser.getInstance().uid+"", "UTF-8");
            data += "&" + URLEncoder.encode("idcom", "UTF-8") + "=" + URLEncoder.encode(""+idCom+"", "UTF-8");
            data += "&" + URLEncoder.encode("device_token", "UTF-8") + "=" + URLEncoder.encode(deviceToken, "UTF-8");

            if(sandbox) {
                data += "&" + URLEncoder.encode("sandbox", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8");
            }

            String url = apiAddress+"?"+data;

            Log.e(tag,"Calling: "+url);

            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            //c.setConnectTimeout(timeout);
            //c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();

                    JSONObject response = new JSONObject(sb.toString()).getJSONObject("response");


                    return response;
            }

        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }



        return new JSONObject();
    }

    boolean sandbox = true;

    public JSONObject paypalTransNoShip(String paymentMethodNonce, String amount, int idCom) {
        return paypalTrans(paymentMethodNonce,amount,null,idCom);
    }
    public JSONObject paypalTrans(String paymentMethodNonce, String amount, String rateId, int idCom) {

        HttpURLConnection c = null;
        try {

            String data = "";
            if (rateId!=null) {
                data += URLEncoder.encode("method", "UTF-8") + "=" + URLEncoder.encode("payPalTrans", "UTF-8");
                data += "&" + URLEncoder.encode("id_rate", "UTF-8") + "=" + URLEncoder.encode(rateId, "UTF-8");
            } else {
                data += URLEncoder.encode("method", "UTF-8") + "=" + URLEncoder.encode("paypalTransNoShip", "UTF-8");
            }

            data += "&" + URLEncoder.encode("uid", "UTF-8") + "=" + URLEncoder.encode(""+PioUser.getInstance().uid+"", "UTF-8");
            data += "&" + URLEncoder.encode("idcom", "UTF-8") + "=" + URLEncoder.encode(""+idCom+"", "UTF-8");
            data += "&" + URLEncoder.encode("device_token", "UTF-8") + "=" + URLEncoder.encode(deviceToken, "UTF-8");

            data += "&" + URLEncoder.encode("payment_method_nonce", "UTF-8") + "=" + URLEncoder.encode(paymentMethodNonce, "UTF-8");
            data += "&" + URLEncoder.encode("amount", "UTF-8") + "=" + URLEncoder.encode(amount, "UTF-8");


            if(sandbox) {
                data += "&" + URLEncoder.encode("sandbox", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8");
            }

            String url = apiAddress+"?"+data;

            Log.e(tag,"Calling: "+url);

            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            //c.setConnectTimeout(timeout);
            //c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();

                    JSONObject response = new JSONObject(sb.toString()).getJSONObject("response");


                    return response;
            }

        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }



        return new JSONObject();
    }


    public void shippingAddressChange() {

        HttpURLConnection c = null;
        try {

            String data  = URLEncoder.encode("method", "UTF-8") + "=" + URLEncoder.encode("shippingAddressChange", "UTF-8");

            data += "&" + URLEncoder.encode("uid", "UTF-8") + "=" + URLEncoder.encode(""+PioUser.getInstance().uid+"", "UTF-8");
            data += "&" + URLEncoder.encode("lat", "UTF-8") + "=" + URLEncoder.encode(""+PioUser.getInstance().location.getLatitude()+"", "UTF-8");
            data += "&" + URLEncoder.encode("lng", "UTF-8") + "=" + URLEncoder.encode(""+PioUser.getInstance().location.getLongitude()+"", "UTF-8");
            data += "&" + URLEncoder.encode("device_token", "UTF-8") + "=" + URLEncoder.encode(deviceToken, "UTF-8");

            data += "&" + URLEncoder.encode("first_name", "UTF-8") + "=" + URLEncoder.encode(PioUser.getInstance().shipName, "UTF-8");
            data += "&" + URLEncoder.encode("last_name", "UTF-8") + "=" + URLEncoder.encode(PioUser.getInstance().shipSurname, "UTF-8");
            data += "&" + URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(PioUser.getInstance().shipAddress, "UTF-8");
            data += "&" + URLEncoder.encode("zip", "UTF-8") + "=" + URLEncoder.encode(PioUser.getInstance().shipZip, "UTF-8");
            data += "&" + URLEncoder.encode("town", "UTF-8") + "=" + URLEncoder.encode(PioUser.getInstance().shipCity, "UTF-8");
            data += "&" + URLEncoder.encode("province", "UTF-8") + "=" + URLEncoder.encode(PioUser.getInstance().shipSubCity, "UTF-8");
            data += "&" + URLEncoder.encode("tel", "UTF-8") + "=" + URLEncoder.encode(PioUser.getInstance().shipPhone, "UTF-8");

            String url = apiAddress+"?"+data;

            Log.e(tag,"Calling: "+url);

            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();

                    JSONObject response = new JSONObject(sb.toString()).getJSONObject("response");
                    Log.v(tag,"shippingAddressChange: "+response.toString(2));

            }

        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public HashMap<String,String> unreadNotifiedAds() {


        HashMap<String,String> unread = new HashMap<>();

        String query  = apiAddress+"?method=unreadNotifiedAds";
        query += "&device_token=" + deviceToken;
        query += "&uid=" + PioUser.getInstance().uid;

        Log.e(tag,"Calling: "+query);


        try {
            JSONObject response = getJsonObjectFromUrl(query);

            JSONObject unreadNotifiedAds = response.getJSONObject("unreadNotifiedAds");
            unread.put("idad",unreadNotifiedAds.getString("idads"));
            unread.put("howmany",unreadNotifiedAds.getString("howmany"));

            return unread;
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return unread;
    }
}
