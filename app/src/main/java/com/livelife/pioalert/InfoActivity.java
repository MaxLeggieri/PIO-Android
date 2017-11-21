package com.livelife.pioalert;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class InfoActivity extends AppCompatActivity {


    TextView appVersion,claim,claimText;
    Button closeButton;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        closeButton = (Button) findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        appVersion = (TextView) findViewById(R.id.appVersion);
        claim = (TextView) findViewById(R.id.claim);
        claimText = (TextView) findViewById(R.id.claimText);

        PackageManager manager = getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    getApplicationContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        appVersion.setText("version "+info.versionName+"\n"+"build "+info.versionCode);
        if (!Utility.isNetworkConnected(InfoActivity.this, new InternetCallback() {
            @Override
            public void retryInternet() {
            }
        })){
            //Toast.makeText(InfoActivity.this,getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
            return ;
        }
        ArrayList<String> claimArr = WebApi.getInstance().claim();
        claim.setText(claimArr.get(0));
        claimText.setText(claimArr.get(1));
    }
}
