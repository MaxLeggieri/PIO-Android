package com.livelife.pioalert;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.livelife.pioalert.R.id.prodImageButton;

/**
 * Created by Maroof Ahmed Siddique on 15/10/17.
 */

public class RatingScreen extends AppCompatActivity implements View.OnClickListener {

    ImageButton backButton;
    Button write_a_review_btn;
    RatingBar rating_bar_rb;
    EditText comment_et;
    TextView prod_name_tv;
    ImageView product_iv;
    Product mProduct;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_screen);

        backButton = findViewById(R.id.backButton);
        rating_bar_rb = findViewById(R.id.rating_bar_rb);
        write_a_review_btn = findViewById(R.id.write_a_review_btn);
        comment_et = findViewById(R.id.comment_et);
        product_iv = findViewById(R.id.product_iv);
        prod_name_tv = findViewById(R.id.prod_name_tv);
        write_a_review_btn.setOnClickListener(this);
        backButton.setOnClickListener(this);

        if (getIntent() != null) {
            if (getIntent().hasExtra("PRODUCT")) {
                mProduct = (Product) getIntent().getSerializableExtra("PRODUCT");
                prod_name_tv.setText(mProduct.name);
                Picasso.with(this).load("http://pioalert.com" + mProduct.image).into(product_iv);

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton:
                finish();
                break;
            case R.id.write_a_review_btn:
                if (validData()) {
                    callWS();
                }

                break;
            default:
                break;

        }
    }

    private boolean validData() {
       /* if (rating_bar_rb.getRating() == 0) {
            Toast.makeText(RatingScreen.this, "Please give rating!", Toast.LENGTH_SHORT).show();
            return false;
        }*/
        if (comment_et.getText().toString().trim().isEmpty()) {
            Toast.makeText(RatingScreen.this, "Please enter your review!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void callWS() {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<JsonElement> call = apiService.setRating(
                String.valueOf(PioUser.getInstance().uid),
//                String.valueOf(607),
                rating_bar_rb.getRating(),
                "product",
                String.valueOf(mProduct.pid),
                comment_et.getText().toString().trim()
        );
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.code() == 200) {
                    Gson mGson = new Gson();
                    SetRatingResponse mSetResponse = mGson.fromJson(response.body().toString(), SetRatingResponse.class);
                    if (mSetResponse.getResponse().getResponse()) {
                        Toast.makeText(RatingScreen.this, "Review added", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RatingScreen.this, getResources().getString(R.string.someerrorText), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RatingScreen.this, getResources().getString(R.string.someerrorText), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                System.out.println(call);
                Toast.makeText(RatingScreen.this, getResources().getString(R.string.someerrorText), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
