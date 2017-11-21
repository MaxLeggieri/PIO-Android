package com.livelife.pioalert;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Maroof Ahmed Siddique on 17/10/17.
 */

public class AllReviewsListActivity extends AppCompatActivity{

    RecyclerView recyclerView;
    private ImageButton backButton;
    private int mPageNumber = 1;
    Product mProduct;
    Company mCompany;
    Promo mPromo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_list_layout);
        recyclerView = findViewById(R.id.recyclerView);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //or
        recyclerView.addItemDecoration(new DividerItemDecoration(AllReviewsListActivity.this,DividerItemDecoration.VERTICAL));


        if (getIntent() != null) {
            if (getIntent().hasExtra("PRODUCT")) {
                mProduct = (Product) getIntent().getSerializableExtra("PRODUCT");
                callWS();

            }
            if (getIntent().hasExtra("COMPANY")) {
                mCompany = (Company) getIntent().getSerializableExtra("COMPANY");
                callWS();

            }
            if (getIntent().hasExtra("PROMO")) {
                mPromo = (Promo) getIntent().getSerializableExtra("PROMO");
                callWS();

            }
        }
    }
    private void callWS() {
        if (!Utility.isNetworkConnected(this, new InternetCallback() {
            @Override
            public void retryInternet() {

            }
        })){
//            Toast.makeText(this,getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
            return ;
        }
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        String element_type = "";
        String element_id = "";
        if (mProduct!=null){
            element_type = "product";
            element_id = String.valueOf(mProduct.pid);
        } else if (mCompany!=null&&mCompany.locations!=null&&mCompany.locations.size()>0){
            element_type = "location";
            element_id = String.valueOf(mCompany.locations.get(0).idLoc);
        } else if (mPromo!=null){
            element_type = "ad";
            element_id = String.valueOf(mPromo.pid);
        }
        Call<JsonElement> call = apiService.getRatings(element_type,
                element_id,
                PioUser.getInstance().uid,
                mPageNumber
        );
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.code() == 200) {
                    Gson mGson = new Gson();
                    ReviewsModal mReviewModal = mGson.fromJson(response.body().toString(), ReviewsModal.class);
                    if (mReviewModal.getResponse().getResponse()) {
                        dumpData(mReviewModal.getResponse().getData().getRatings());


                    } else {
                        Toast.makeText(AllReviewsListActivity.this, getResources().getString(R.string.someerrorText), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AllReviewsListActivity.this, getResources().getString(R.string.someerrorText), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                System.out.println(call);
                Toast.makeText(AllReviewsListActivity.this, getResources().getString(R.string.someerrorText), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void dumpData(List<ReviewsModal.Response.Data.Rating> mRatings) {

        ReviewsListAdapter mAdapter = new ReviewsListAdapter(mRatings,AllReviewsListActivity.this);
        recyclerView.setAdapter(mAdapter);
    }
}
