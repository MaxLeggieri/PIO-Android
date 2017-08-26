package com.livelife.pioalert;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ConsentActivity extends AppCompatActivity {

    String tag = "ConsentActivity";

    Button eulaButton,privacyButton,continueButton;
    CheckBox geoCheck,profileCheck,eulaCheck,privacyCheck;

    int selectedJobId = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent);

        geoCheck = (CheckBox) findViewById(R.id.geoCheck);
        profileCheck = (CheckBox) findViewById(R.id.profileCheck);
        eulaCheck = (CheckBox) findViewById(R.id.eulaCheck);
        privacyCheck = (CheckBox) findViewById(R.id.privacyCheck);

        eulaButton = (Button) findViewById(R.id.eulaButton);
        eulaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ConsentActivity.this);

                WebView wv = new WebView(ConsentActivity.this);
                wv.loadUrl("http://www.pioalert.com/app/eula/");
                alert.setView(wv);
                alert.setNegativeButton("chiudi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
        privacyButton = (Button) findViewById(R.id.privacyButton);
        privacyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ConsentActivity.this);

                WebView wv = new WebView(ConsentActivity.this);
                wv.loadUrl("http://www.pioalert.com/app/privacy/");
                alert.setView(wv);
                alert.setNegativeButton("chiudi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        continueButton = (Button) findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!eulaCheck.isChecked() || !privacyCheck.isChecked()) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ConsentActivity.this);
                    dialog.setMessage("Per proseguire devi spuntare i campi obbligatori.");
                    dialog.setTitle("Attenzione");
                    dialog.setPositiveButton("OK", null);
                    dialog.show();
                } else {
                    PioUser.getInstance().setConsent(true);
                    finish();
                }

            }
        });

    }
}
