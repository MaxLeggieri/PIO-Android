package com.livelife.pioalert;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RankingActivity extends AppCompatActivity {

    static final String tag = RankingActivity.class.getSimpleName();

    ImageButton backButton;

    ArrayList<PioPlayer> allPlayers;

    RecyclerView recyclerView;
    RankAdapterView adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

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
                if (!Utility.isNetworkConnected(RankingActivity.this, new InternetCallback() {
                    @Override
                    public void retryInternet() {
                    }
                })){
                    //Toast.makeText(RankingActivity.this,getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
                    return ;
                }
                allPlayers = WebApi.getInstance().ranking(20);
                Log.e(tag,allPlayers.size()+" players...");
                if (allPlayers.size() > 0) {
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setNestedScrollingEnabled(false);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(RankingActivity.this);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);

                    if (adapter == null) {
                        adapter = new RankAdapterView(allPlayers);
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.setItems(allPlayers);
                    }
                }
            }
        };


        Handler ch = new Handler();
        ch.postDelayed(r,200);
    }
}
