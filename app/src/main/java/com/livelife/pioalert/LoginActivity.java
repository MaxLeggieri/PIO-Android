package com.livelife.pioalert;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Shoeb on 14/10/17.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText login_et;
    private EditText password_et;
    private Button login_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_et  = findViewById(R.id.login_et);
        password_et  = findViewById(R.id.password_et);
        login_btn  = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidData()){
                    ApiInterface apiService =
                            ApiClient.getClient().create(ApiInterface.class);

                    Call<JsonElement> call = apiService.postLogin(login_et.getText().toString().trim(),password_et
                    .getText().toString().trim(),"login");
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(Call<JsonElement>call, Response<JsonElement> response) {

                            Log.e(TAG, "onResponse called : "+response.body().toString());
                            Gson mGson = new Gson();
                            LoginResponse mLoginresponse  = mGson.fromJson(response.body().toString(),LoginResponse.class);
                            if (mLoginresponse.getResponse().getResponse()){
                                MyPreference.setUserLoginData(mLoginresponse);

                                Intent mIntent = new Intent(LoginActivity.this,CreateAdActivity.class);
                                startActivity(mIntent);
                            }else{
                                Toast.makeText(LoginActivity.this,mLoginresponse.getResponse().getReason(),Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void onFailure(Call<JsonElement>call, Throwable t) {
                            // Log error here since request failed
                            Log.e(TAG, "onFailure called : "+t.toString());
                            Toast.makeText(LoginActivity.this,"Some error occured",Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });

    }

    private boolean isValidData() {
        return true;
    }
}
