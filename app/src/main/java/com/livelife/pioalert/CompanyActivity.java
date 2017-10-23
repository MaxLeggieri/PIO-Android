package com.livelife.pioalert;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CompanyActivity extends AppCompatActivity {

    TextView comNameTextView, shopNameTextView, shopDistanceTextView, comDesc, phoneNumber, locationAddress;
    LinearLayout phoneButton;
    Company com;
    ImageButton backButton;
    ImageView comImage;
    Button infoButton;

    MapView mapView;
    GoogleMap map;

    RecyclerView companyPromos;
    RecyclerView companyProducts;
    PromoRecyclerView promoAdapter;
    ProductRecyclerView prodAdapter;

    ArrayList<Promo> promoItems = new ArrayList<>();
    ArrayList<Product> productItems = new ArrayList<>();

    static final Integer CALL = 0x2;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        int comId = getIntent().getIntExtra("comId", 0);
        if (comId == 0) finish();

        com = WebApi.getInstance().getCompanyById(comId);

        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        comNameTextView = (TextView) findViewById(R.id.comNameTextView);
        comNameTextView.setText(com.brandName);

        final Location comLoc = com.locations.get(0);


        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.getUiSettings().setMyLocationButtonEnabled(false);

                MapsInitializer.initialize(CompanyActivity.this);

                // Updates the location and zoom of the MapView

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(comLoc.lat, comLoc.lng), 10);
                map.animateCamera(cameraUpdate);
                map.addMarker(new MarkerOptions()
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_geolocal_simple))
                        .anchor(0.5f, 1.0f)
                        .title(com.brandName)
                        .snippet(com.locations.get(0).address)
                        .position(
                                new LatLng(comLoc.lat, comLoc.lng)));




            }
        });

        comImage = (ImageView) findViewById(R.id.comImage);
        Picasso.with(this).load("http://pioalert.com" + com.image).into(comImage);

        shopNameTextView = (TextView) findViewById(R.id.shopNameTextView);
        shopNameTextView.setText(com.brandName);

        shopDistanceTextView = (TextView) findViewById(R.id.shopDistanceTextView);
        shopDistanceTextView.setText(comLoc.distanceHuman);

        comDesc = (TextView) findViewById(R.id.comDesc);
        comDesc.setText(com.description);

        phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        phoneNumber.setText(com.phone);

        locationAddress = (TextView) findViewById(R.id.locationAddress);
        locationAddress.setText(comLoc.address);

        phoneButton = (LinearLayout) findViewById(R.id.phoneButton);
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("phoneButton","Calling: "+com.phone);
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + com.phone));
                if (ActivityCompat.checkSelfPermission(CompanyActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    askForPermission(android.Manifest.permission.CALL_PHONE,CALL);
                    return;
                }
                startActivity(intent);
            }
        });


        infoButton = (Button) findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);

                String mailto = "mailto:feedback@pioalert.com";

                if (!com.email.equals("accountemail@fakeaccountemail.it")) {
                    mailto+=", "+com.email;
                }

                Uri uri = Uri.parse(mailto);
                intent.setData(uri);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Richiesta di informazioni su "+com.brandName+" - "+com.locations.get(0).address);

                startActivity(intent);
            }
        });


        productsHeader = (LinearLayout) findViewById(R.id.productsHeader);

        companyPromos = (RecyclerView) findViewById(R.id.companyPromos);
        companyPromos.setHasFixedSize(true);
        companyPromos.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CompanyActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        companyPromos.setLayoutManager(layoutManager);


        companyProducts = (RecyclerView) findViewById(R.id.companyProducts);
        companyProducts.setHasFixedSize(true);
        companyProducts.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(CompanyActivity.this);
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        companyProducts.setLayoutManager(layoutManager2);



        /*

        */


    }

    boolean initialized = false;
    LinearLayout productsHeader;
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();


        if (!initialized) {

            promoItems = WebApi.getInstance().companyAds(com.cid);
            productItems = WebApi.getInstance().companyProducts(com.cid);



            promoAdapter = new PromoRecyclerView(promoItems,null);
            companyPromos.setAdapter(promoAdapter);

            prodAdapter = new ProductRecyclerView(productItems);
            companyProducts.setAdapter(prodAdapter);

            if (productItems.size() == 0) {
                productsHeader.setVisibility(View.GONE);
                companyProducts.setVisibility(View.GONE);
            }
            initialized = true;

        }


    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(CompanyActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(CompanyActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(CompanyActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(CompanyActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }
}
