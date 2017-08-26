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

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrdersActivity extends AppCompatActivity {

    static final String tag = OrdersActivity.class.getSimpleName();

    ArrayList<Order> allOrders;

    ImageButton backButton;

    OrderAdapterView orderAdapter;
    RecyclerView recyclerView;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

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
                allOrders = WebApi.getInstance().orders();
                Log.e(tag,allOrders.size()+" orders...");
                if (allOrders.size() > 0) {
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setNestedScrollingEnabled(false);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(OrdersActivity.this);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);

                    if (orderAdapter == null) {
                        orderAdapter = new OrderAdapterView(allOrders);
                        recyclerView.setAdapter(orderAdapter);
                    } else {
                        orderAdapter.setItems(allOrders);
                    }
                }
            }
        };


        Handler ch = new Handler();
        ch.postDelayed(r,200);
    }
}
