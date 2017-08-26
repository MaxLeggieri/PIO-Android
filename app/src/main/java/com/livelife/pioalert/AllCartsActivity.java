package com.livelife.pioalert;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AllCartsActivity extends AppCompatActivity {

    static final String tag = AllCartsActivity.class.getSimpleName();

    ArrayList<Cart> allCarts;

    ImageButton backButton;

    CartAdapterView cartAdapter;
    RecyclerView recyclerView;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_carts);

        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);


    }

    @Override
    protected void onResume() {
        super.onResume();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                allCarts = WebApi.getInstance().basketShowAll();
                Log.e(tag,allCarts.size()+" carts...");
                if (allCarts.size() > 0) {
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setNestedScrollingEnabled(false);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(AllCartsActivity.this);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);

                    if (cartAdapter == null) {
                        cartAdapter = new CartAdapterView(allCarts);
                        recyclerView.setAdapter(cartAdapter);
                    } else {
                        cartAdapter.setItems(allCarts);
                    }
                }
            }
        };


        Handler ch = new Handler();
        ch.postDelayed(r,200);
    }
}
