package com.livelife.pioalert;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PioPlaceActivity extends AppCompatActivity {

    TextView promoTitleTextView;

    ImageView imageView;
    TextView placeName,placeAddress,phoneNumber;
    String websiteUrl,placeId,phoneNumberString,photoReference;
    LinearLayout phoneButton;

    Button webButton;

    static final Integer CALL = 0x2;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pio_place);

        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imageView = (ImageView) findViewById(R.id.imageView);
        promoTitleTextView = (TextView) findViewById(R.id.promoTitleTextView);
        placeName = (TextView) findViewById(R.id.placeName);
        placeAddress = (TextView) findViewById(R.id.placeAddress);
        phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        phoneButton = (LinearLayout) findViewById(R.id.phoneButton);
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumberString));
                if (ActivityCompat.checkSelfPermission(PioPlaceActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    askForPermission(android.Manifest.permission.CALL_PHONE,CALL);
                    return;
                }
                startActivity(intent);
            }
        });
        webButton = (Button) findViewById(R.id.webButton);
        webButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl));
                startActivity(browserIntent1);
            }
        });

        String name = getIntent().getStringExtra("name");
        String address = getIntent().getStringExtra("address");
        photoReference = getIntent().getStringExtra("photoReference");

        promoTitleTextView.setText(name);
        placeName.setText(name);
        placeAddress.setText(address);

        placeId = getIntent().getStringExtra("placeId");

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Utility.isNetworkConnected(PioPlaceActivity.this, new InternetCallback() {
            @Override
            public void retryInternet() {
            }
        })){
            //Toast.makeText(PioPlaceActivity.this,getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
            return ;
        }
        JSONObject obj = WebApi.getInstance().getPoiDetails(placeId);
        if (obj.has("formatted_phone_number")) {
            try {
                phoneNumberString = obj.getString("formatted_phone_number");
                phoneNumber.setText(phoneNumberString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            phoneButton.setVisibility(View.GONE);
        }

        if (obj.has("website")) {
            try {
                websiteUrl = obj.getString("website");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            webButton.setVisibility(View.GONE);
        }

        String path = "https://maps.googleapis.com/maps/api/place/photo?photoreference="+photoReference+"&maxwidth=1280&key=AIzaSyDRV45yi1TJZDx3rCNe5S-9qmRy3AtonPI";
        Picasso.with(this).load(path).into(imageView);
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(PioPlaceActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(PioPlaceActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(PioPlaceActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(PioPlaceActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }
}
