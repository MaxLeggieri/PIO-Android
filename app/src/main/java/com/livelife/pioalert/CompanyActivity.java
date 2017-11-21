package com.livelife.pioalert;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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

public class CompanyActivity extends AppCompatActivity implements ProductCategoryCallback {

    TextView comNameTextView, shopNameTextView, shopDistanceTextView, comDesc, phoneNumber, locationAddress, numberReviews,
            review_info_tv;
    LinearLayout phoneButton;
    Company com;
    ImageButton backButton;
    ImageView comImage;
    Button infoButton;
    Button write_a_review_btn;
    Button write_a_review_btn_top;

    RatingBar ratingBar;
    RatingBar ratingBar_bottom;
    LinearLayout review_ll;
    LinearLayout rating_bar_ll;

    MapView mapView;
    GoogleMap map;

    RecyclerView companyPromos;
    RecyclerView companyProducts;
    RecyclerView categories_rv;
    PromoRecyclerView promoAdapter;
    ProductRecyclerView prodAdapter;
    ProductCategoriesAdapter mProductCategoriesAdapter;

    ArrayList<Promo> promoItems = new ArrayList<>();
    ArrayList<Product> productItems = new ArrayList<>();
    ArrayList<CategoryProductModal> mCategoryProductModalArrayList = new ArrayList<>();

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
        if (!Utility.isNetworkConnected(CompanyActivity.this, new InternetCallback() {
            @Override
            public void retryInternet() {
            }
        })) {
            //Toast.makeText(CartActivity.this,getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
            return;
        }
        com = WebApi.getInstance().getCompanyById(comId);

        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        write_a_review_btn = (Button) findViewById(R.id.write_a_review_btn);
        write_a_review_btn_top = (Button) findViewById(R.id.write_a_review_btn_top);
        write_a_review_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(CompanyActivity.this, RatingScreen.class);
                mIntent.putExtra("COMPANY", com);
                startActivity(mIntent);
            }
        });
        write_a_review_btn_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(CompanyActivity.this, RatingScreen.class);
                mIntent.putExtra("COMPANY", com);
                startActivity(mIntent);
            }
        });
        comNameTextView = (TextView) findViewById(R.id.comNameTextView);
        comNameTextView.setText(com.brandName);
        review_ll = (LinearLayout) findViewById(R.id.review_ll);
        rating_bar_ll = (LinearLayout) findViewById(R.id.rating_bar_ll);
        review_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(CompanyActivity.this, AllReviewsListActivity.class);
                mIntent.putExtra("COMPANY", com);

                startActivity(mIntent);
            }
        });
        rating_bar_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(CompanyActivity.this, RatingScreen.class);
                mIntent.putExtra("COMPANY", com);
                startActivity(mIntent);
            }
        });

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar_bottom = (RatingBar) findViewById(R.id.ratingBar_bottom);
        if (com != null && com.locations != null && com.locations.size() > 0) {
            ratingBar.setRating((float) com.locations.get(0).avrReviews);
            ratingBar_bottom.setRating(Float.parseFloat(com.locations.get(0).userRating));
        }

        numberReviews = (TextView) findViewById(R.id.numberReviews);
        if (com != null && com.locations != null && com.locations.size() > 0) {
            numberReviews.setText("" + com.locations.get(0).numReviews);
            review_info_tv = (TextView) findViewById(R.id.review_info_tv);

            if (com.locations.get(0).numReviews <= 0) {
                review_info_tv.setVisibility(View.GONE);
                review_ll.setVisibility(View.GONE);
                write_a_review_btn_top.setVisibility(View.VISIBLE);
                review_info_tv.setText("NO REVIEWS");
            } else {
                write_a_review_btn_top.setVisibility(View.GONE);
                review_info_tv.setVisibility(View.VISIBLE);
                review_ll.setVisibility(View.VISIBLE);
                review_info_tv.setText("VEDI RECENSIONI");
            }
        }

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
                Log.e("phoneButton", "Calling: " + com.phone);
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + com.phone));
                if (ActivityCompat.checkSelfPermission(CompanyActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    askForPermission(android.Manifest.permission.CALL_PHONE, CALL);
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
                    mailto += ", " + com.email;
                }

                Uri uri = Uri.parse(mailto);
                intent.setData(uri);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Richiesta di informazioni su " + com.brandName + " - " + com.locations.get(0).address);

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


        categories_rv = (RecyclerView) findViewById(R.id.categories_rv);
        categories_rv.setHasFixedSize(true);
        categories_rv.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(CompanyActivity.this);
        layoutManager3.setOrientation(LinearLayoutManager.HORIZONTAL);
        categories_rv.setLayoutManager(layoutManager3);



        /*

        */


    }

    boolean initialized = false;
    LinearLayout productsHeader;

    boolean isReloaded = false;

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        if (isReloaded) {

            int comId = getIntent().getIntExtra("comId", 0);
            if (comId == 0) finish();
            if (!Utility.isNetworkConnected(CompanyActivity.this, new InternetCallback() {
                @Override
                public void retryInternet() {
                }
            })) {
                //Toast.makeText(CartActivity.this,getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
                return;
            }
            com = WebApi.getInstance().getCompanyById(comId);

            ratingBar = (RatingBar) findViewById(R.id.ratingBar);
            ratingBar_bottom = (RatingBar) findViewById(R.id.ratingBar_bottom);

            if (com != null && com.locations != null && com.locations.size() > 0) {
                ratingBar.setRating((float) com.locations.get(0).avrReviews);
                ratingBar_bottom.setRating(Float.parseFloat(com.locations.get(0).userRating));
            }

            numberReviews = (TextView) findViewById(R.id.numberReviews);
            if (com != null && com.locations != null && com.locations.size() > 0) {
                numberReviews.setText("" + com.locations.get(0).numReviews);
                review_info_tv = (TextView) findViewById(R.id.review_info_tv);

                if (com.locations.get(0).numReviews <= 0) {
                    review_info_tv.setVisibility(View.GONE);
                    review_ll.setVisibility(View.GONE);
                    write_a_review_btn_top.setVisibility(View.VISIBLE);
                    review_info_tv.setText("NO REVIEWS");
                } else {
                    write_a_review_btn_top.setVisibility(View.GONE);
                    review_info_tv.setVisibility(View.VISIBLE);
                    review_ll.setVisibility(View.VISIBLE);
                    review_info_tv.setText("VEDI RECENSIONI");
                }
            }

        }

        isReloaded = true;

        if (!initialized) {
            if (!Utility.isNetworkConnected(CompanyActivity.this, new InternetCallback() {
                @Override
                public void retryInternet() {
                }
            })) {
                //Toast.makeText(CartActivity.this,getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
                return;
            }
            promoItems = WebApi.getInstance().companyAds(com.cid);
            productItems = WebApi.getInstance().companyProducts(com.cid);


            promoAdapter = new PromoRecyclerView(promoItems, null);
            companyPromos.setAdapter(promoAdapter);

            prodAdapter = new ProductRecyclerView(productItems);
            companyProducts.setAdapter(prodAdapter);
            if (productItems.size() > 0) {
                mCategoryProductModalArrayList = new ArrayList<>();
              /*  mCategoryProductModalArrayList.add(new CategoryProductModal("name 1", "1", true));
                mCategoryProductModalArrayList.add(new CategoryProductModal("name 2", "1", false));
                mCategoryProductModalArrayList.add(new CategoryProductModal("name 3", "1", false));
                mCategoryProductModalArrayList.add(new CategoryProductModal("name 4", "1", false));
                mCategoryProductModalArrayList.add(new CategoryProductModal("name 5", "1", false));
                mCategoryProductModalArrayList.add(new CategoryProductModal("name 6", "1", false));
                mCategoryProductModalArrayList.add(new CategoryProductModal("name 7", "1", false));
                mCategoryProductModalArrayList.add(new CategoryProductModal("name 8", "1", false));
                mCategoryProductModalArrayList.add(new CategoryProductModal("name 9", "1", false));
                mCategoryProductModalArrayList.add(new CategoryProductModal("name 10", "1", false));
                mCategoryProductModalArrayList.add(new CategoryProductModal("name 11", "1", false));
              */
//              mProductCategoriesAdapter = new ProductCategoriesAdapter(mCategoryProductModalArrayList);


                if (com.comcatList.size() > 0) {

                    categories_rv.setVisibility(View.VISIBLE);

                    mProductCategoriesAdapter = new ProductCategoriesAdapter(com.comcatList);
                    mProductCategoriesAdapter.setCallback(this);

                    categories_rv.setAdapter(mProductCategoriesAdapter);

/*
                    if (com.comcatList.size()>0){
                        filterProductListById (com.comcatList.get(0));

                    }*/
                } else {
                    categories_rv.setVisibility(View.GONE);
                }
            }


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

    @Override
    public void itemSelected(CategoryProductModal bean, int position) {
        if (bean.isSelected()) {
            return;
        }

        for (CategoryProductModal mCategoryProductModal :
                mProductCategoriesAdapter.getAllItems()) {
            mCategoryProductModal.setSelected(false);
        }
        if (!bean.isSelected()) {
            bean.setSelected(true);
        }

        mProductCategoriesAdapter.updateSingleItem(bean, position);

        filterProductListById(bean);

    }

    private void filterProductListById(CategoryProductModal bean) {
        if (bean == null) {
            return;
        }
        if (bean.getId() == null) {
            prodAdapter = new ProductRecyclerView(productItems);
            companyProducts.setAdapter(prodAdapter);
            return;
        }

        ArrayList<Product> mTempList = new ArrayList<>();

        for (Product mProduct :
                productItems) {

            if (mProduct.comcatId.size() > 0) {
                if (mProduct.comcatId.get(0).equalsIgnoreCase(bean.getId())) {
                    mTempList.add(mProduct);
                }
            }

        }

        if (mTempList.size() > 0) {
            prodAdapter = new ProductRecyclerView(mTempList);
            companyProducts.setAdapter(prodAdapter);
        }


    }
}
