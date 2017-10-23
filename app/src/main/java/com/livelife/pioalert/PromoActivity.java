package com.livelife.pioalert;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PromoActivity extends AppCompatActivity {

    static final String tag = PromoActivity.class.getSimpleName();
    Promo p;
    ImageView imageView,comImageView;
    ImageButton backButton;
    TextView promoTitleTextView,shopNameTextView,shopDistanceTextView,promoTitle,expireTextView,promoDesc,numberViews;
    Button linkButton,attachmentButton,couponButton,viewShopButton;
    ImageButton videoPreviewButton,likeButton,shareButton,playButton;
    MapView mapView;
    GoogleMap map;
    MediaPlayer mpDone, mpErr;
    AlertDialog.Builder alertDialogBuilder;

    RecyclerView galleryRecycler;
    ImageAdapter imageAdapter;

    boolean couponConvalidated = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo);

        int promoId = getIntent().getIntExtra("promoId",0);

        if (promoId == 0) finish();

        p = WebApi.getInstance().getPromoById(promoId);

        Log.e("PromoActivity",p.title);

        imageView = (ImageView) findViewById(R.id.imageView);
        comImageView = (ImageView) findViewById(R.id.comImageView);
        comImageView.setClickable(true);
        comImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent companyIntent = new Intent(PromoActivity.this,CompanyActivity.class);
                companyIntent.putExtra("comId",p.brandId);
                startActivity(companyIntent);
            }
        });
        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Picasso.with(this).load("http://pioalert.com"+p.imagePath).into(imageView);
        Picasso.with(this).load("http://pioalert.com"+p.cimage).into(comImageView);

        promoTitleTextView = (TextView) findViewById(R.id.promoTitleTextView);
        promoTitleTextView.setText(p.brandName);


        shopNameTextView = (TextView) findViewById(R.id.shopNameTextView);
        shopNameTextView.setText(p.brandName);

        shopDistanceTextView = (TextView) findViewById(R.id.shopDistanceTextView);
        shopDistanceTextView.setText("A "+p.distanceHuman+" da te");

        promoTitle = (TextView) findViewById(R.id.promoTitle);
        promoTitle.setText(p.title);

        expireTextView = (TextView) findViewById(R.id.expireTextView);
        expireTextView.setText("Promo valida fino al "+p.expiration);

        promoDesc = (TextView) findViewById(R.id.promoDesc);
        promoDesc.setText(p.desc);

        numberViews = (TextView) findViewById(R.id.numberViews);
        numberViews.setText("Visto "+p.viewedCount+" volte");

        viewShopButton = (Button) findViewById(R.id.viewShopButton);
        viewShopButton.setPaintFlags(viewShopButton.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        viewShopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent companyIntent = new Intent(PromoActivity.this,CompanyActivity.class);
                companyIntent.putExtra("comId",p.brandId);
                startActivity(companyIntent);
            }
        });

        linkButton = (Button) findViewById(R.id.linkButton);
        linkButton.setOnClickListener(detailsListener);
        if(p.link.equals("")) {
            linkButton.setVisibility(View.GONE);
        }

        attachmentButton = (Button) findViewById(R.id.attachmentButton);
        attachmentButton.setOnClickListener(detailsListener);
        Log.v(tag,"ATTACHMENT: "+p.attachment);
        if(p.attachment.equals("")) {
            attachmentButton.setVisibility(View.GONE);
        }

        couponButton = (Button) findViewById(R.id.couponButton);
        couponButton.setOnClickListener(detailsListener);
        if(p.usedCoupon != 0 || p.couponCode.equals("")) {
            couponButton.setVisibility(View.GONE);
        }


        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.getUiSettings().setMyLocationButtonEnabled(false);

                MapsInitializer.initialize(PromoActivity.this);

                // Updates the location and zoom of the MapView
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(p.lat, p.lng), 10);
                map.animateCamera(cameraUpdate);
                map.addMarker(new MarkerOptions()
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_geolocal_simple))
                        .anchor(0.5f, 1.0f)
                        .title(p.title)
                        .snippet(p.address)
                        .position(
                                new LatLng(p.lat, p.lng)));
            }
        });




        videoPreviewButton = (ImageButton) findViewById(R.id.videoPreviewButton);
        playButton = (ImageButton) findViewById(R.id.playButton);
        videoPreviewButton.setOnClickListener(detailsListener);
        playButton.setOnClickListener(detailsListener);
        if(p.youtube.equals("")) {
            videoPreviewButton.setVisibility(View.GONE);
            playButton.setVisibility(View.GONE);
        } else {
            Picasso.with(this).load(p.youtubeImg).into(videoPreviewButton);
        }


        likeButton = (ImageButton) findViewById(R.id.likeButton);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(p.liked) {
                    likeButton.setImageResource(R.drawable.icon_like);
                } else {
                    likeButton.setImageResource(R.drawable.icon_like_attivo);
                }
                p.liked = !p.liked;

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        WebApi.getInstance().likeAd(p.liked,p.pid);
                    }
                });
            }
        });

        if(p.liked) {
            likeButton.setImageResource(R.drawable.icon_like_attivo);
        }

        shareButton = (ImageButton) findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(tag,"PROMO LINK: "+p.link);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.pioalert.com/sharead/?idad="+p.pid+"&uid="+PioUser.getInstance().uid);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Guarda questa offerta su PIO, l'app delle promozioni!");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Condividi con:"));
            }
        });



        mpErr = MediaPlayer.create(this,R.raw.error_sound);
        mpDone = MediaPlayer.create(this,R.raw.done_sound);
        alertDialogBuilder = new AlertDialog.Builder(this);

        galleryRecycler = (RecyclerView) findViewById(R.id.galleryRecycler);

        if (p.gallery != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            layoutManager.scrollToPosition(0);
            galleryRecycler.setLayoutManager(layoutManager);

            imageAdapter = new ImageAdapter(this, p.gallery);
            galleryRecycler.setAdapter(imageAdapter);
        } else {
            galleryRecycler.setVisibility(View.GONE);
        }

    }

    void showGallery(int position) {
        Intent i = new Intent(this,SlideshowActivity.class);
        String images = android.text.TextUtils.join(",", p.gallery);
        i.putExtra("images",images);
        i.putExtra("promoTitle",p.title);
        i.putExtra("position",position);
        startActivity(i);
    }

    View.OnClickListener detailsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.linkButton:
                    Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(p.link));
                    startActivity(browserIntent1);
                    break;
                case R.id.attachmentButton:
                    Intent browserIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://pioalert.com"+p.attachment));
                    startActivity(browserIntent2);
                    break;

                case R.id.videoPreviewButton:
                    Intent browserIntent3 = new Intent(Intent.ACTION_VIEW, Uri.parse(p.youtube));
                    startActivity(browserIntent3);
                    break;

                case R.id.playButton:
                    Intent browserIntent4 = new Intent(Intent.ACTION_VIEW, Uri.parse(p.youtube));
                    startActivity(browserIntent4);
                    break;

                case R.id.couponButton:

                    Log.v(tag,"QR: "+p.couponCode);
                    IntentIntegrator integrator = new IntentIntegrator(PromoActivity.this);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                    integrator.setPrompt("Inquadra il QR code");
                    integrator.setCameraId(0);  // Use a specific camera of the device
                    integrator.setBeepEnabled(false);
                    integrator.setOrientationLocked(false);
                    integrator.initiateScan();

                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.v(tag,"Scan cancelled...");

            } else {
                String qr = data.getStringExtra("SCAN_RESULT");
                Log.v(tag, "Scanned: " + data.getStringExtra("SCAN_RESULT"));

                if(qr.equals(p.couponCode)) {
                    mpDone.start();
                    couponButton.setEnabled(false);
                    couponButton.setAlpha(0.4f);
                    couponConvalidated = true;
                    alertDialogBuilder.setTitle("Coupon Convalidato")
                            .setMessage(p.title+"\n\nMostra questo messaggio all'operatore.")
                            .setNeutralButton("OK",null);
                    alertDialogBuilder.show();


                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject obj = WebApi.getInstance().useCoupon(p.couponCode,p.pid);
                            try {
                                Log.e(tag,obj.toString(2));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                } else {
                    mpErr.start();
                    alertDialogBuilder.setTitle("Coupon Errato")
                            .setMessage(p.title+"\n\nRiprova oppure accertati di visualizzare l'offerta corretta")
                            .setNeutralButton("OK",null);
                    alertDialogBuilder.show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

            // TODO
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
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
        mpDone.release();
        mpErr.release();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();

    }
}
