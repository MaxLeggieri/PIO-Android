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

public class FelixActivity extends AppCompatActivity implements CompanyRecyclerView.OnRecyclerScrollListener {

    static final String tag = FelixActivity.class.getSimpleName();

    ImageButton backButton;
    int page = 1;

    RecyclerView companiesRecycler;
    ArrayList<Company> allCompanies;
    CompanyRecyclerView companiesAdapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_felix);

        companiesRecycler = (RecyclerView) findViewById(R.id.recyclerView);
        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Runnable compRunnable = new Runnable() {
            @Override
            public void run() {

                if (allCompanies == null) {
                    allCompanies = WebApi.getInstance().getCompanies(page,"0","felix");
                }




                companiesRecycler.setHasFixedSize(true);
                companiesRecycler.setNestedScrollingEnabled(false);
                LinearLayoutManager layoutManager = new LinearLayoutManager(FelixActivity.this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                companiesRecycler.setLayoutManager(layoutManager);

                companiesAdapter = new CompanyRecyclerView(allCompanies,FelixActivity.this);
                companiesRecycler.setAdapter(companiesAdapter);


            }
        };

        Handler ph = new Handler();
        ph.postDelayed(compRunnable,300);
    }

    void addCompanies() {

        Log.e(tag,"addCompanies...");

        page+=1;

        Runnable catRunnable = new Runnable() {
            @Override
            public void run() {
                List<Company> newComps = WebApi.getInstance().getCompanies(page,"0","felix");
                Log.e(tag,"Adding "+newComps.size()+" items...");
                if (newComps.size() > 0) {
                    allCompanies.addAll(newComps);
                    companiesAdapter.setItems(allCompanies);
                }
            }
        };


        Handler ch = new Handler();
        ch.postDelayed(catRunnable,200);

    }

    @Override
    public void onNearEndScrollListener() {
        addCompanies();
    }
}
