package com.livelife.pioalert;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShippingAddressActivity extends AppCompatActivity {

    ImageButton backButton;
    EditText shipName,shipSurname,shipAddress,shipZip,shipCity,shipSubcity,shipPhone;
    Button modifyButton;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_address);

        initUI();
    }


    void initUI() {

        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        shipName = (EditText) findViewById(R.id.shipName);
        shipSurname = (EditText) findViewById(R.id.shipSurname);
        shipAddress = (EditText) findViewById(R.id.shipAddress);
        shipZip = (EditText) findViewById(R.id.shipZip);
        shipCity = (EditText) findViewById(R.id.shipCity);
        shipSubcity = (EditText) findViewById(R.id.shipSubcity);
        shipPhone = (EditText) findViewById(R.id.shipPhone);


        PioUser pu = PioUser.getInstance();

        if(pu.shipAddress != null) {
            shipName.setText(pu.shipName);
            shipSurname.setText(pu.shipSurname);
            shipAddress.setText(pu.shipAddress);
            shipZip.setText(pu.shipZip);
            shipCity.setText(pu.shipCity);
            shipSubcity.setText(pu.shipSubCity);
            shipPhone.setText(pu.shipPhone);
        }


        modifyButton = (Button) findViewById(R.id.modifyButton);
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean error = false;
                if(shipName.getText().toString().length() == 0) {
                    error = true;
                }
                else if(shipSurname.getText().toString().length() == 0) {
                    error = true;
                }
                else if(shipAddress.getText().toString().length() == 0) {
                    error = true;
                }
                else if(shipZip.getText().toString().length() == 0) {
                    error = true;
                }
                else if(shipCity.getText().toString().length() == 0) {
                    error = true;
                }
                else if(shipSubcity.getText().toString().length() == 0) {
                    error = true;
                }
                else if(shipPhone.getText().toString().length() == 0) {
                    error = true;
                }


                if(!error) {

                    JSONObject shipData = new JSONObject();
                    try {
                        shipData.put("first_name", shipName.getText().toString());
                        shipData.put("last_name", shipSurname.getText().toString());
                        shipData.put("address", shipAddress.getText().toString());
                        shipData.put("zip", shipZip.getText().toString());
                        shipData.put("town", shipCity.getText().toString());
                        shipData.put("province", shipSubcity.getText().toString());
                        shipData.put("tel", shipPhone.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    PioUser.getInstance().setShippingAddress(shipData);


                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            if (!Utility.isNetworkConnected(ShippingAddressActivity.this)){
                                Toast.makeText(ShippingAddressActivity.this,getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
                                return ;
                            }
                            WebApi.getInstance().shippingAddressChange();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            });

                        }
                    });


                } else {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(ShippingAddressActivity.this);
                    dialog.setMessage("I campi sono tutti obbligatori, riempili tutti.");
                    dialog.setTitle("Attentione");
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            finish();
                        }
                    });
                    dialog.show();

                    Utility.getInstance().getAlertDialog("Attentione","I campi sono tutti obbligatori, riempili tutti.",ShippingAddressActivity.this).show();

                }

            }
        });

    }


}
