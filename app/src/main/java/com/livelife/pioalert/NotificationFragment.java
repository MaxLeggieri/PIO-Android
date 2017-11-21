package com.livelife.pioalert;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ScrollView;

import java.util.ArrayList;

/**
 * Created by Max on 26/06/2017.
 */

public class NotificationFragment extends Fragment {

    private static final String tag = PromoFragment.class.getSimpleName();

    public NotificationFragment() {
        // Required empty public constructor
    }


    MainActivity mainActivity;

    RecyclerView promoRecycler;
    ArrayList<Promo> allPromo;
    PromoRecyclerView promoAdapter;


    SwipeRefreshLayout swipeRefresh;

    int page = 1;

    String lastIds;
    String lastTimeref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }


    private View view;
    ScrollView scrollView;

    public String lastCatSelected = "0";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        mainActivity = (MainActivity) getActivity();
        mainActivity.clearNotificationBadge();
        view = inflater.inflate(R.layout.fragment_notification, container, false);

        ImageButton menuButton = (ImageButton) view.findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //drawerLayout.openDrawer(Gravity.LEFT);
                mainActivity.openDrawer();
            }
        });

        scrollView = (ScrollView) view.findViewById(R.id.scrollView);

        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                page = 1;
                allPromo = null;
                runPromos();
                swipeRefresh.setRefreshing(false);

            }
        });

        promoRecycler = (RecyclerView) view.findViewById(R.id.promoRecycler);


        runPromos();

        //if (lastIds != null) {
        if (Utility.isNetworkConnected(getActivity(), new InternetCallback() {
            @Override
            public void retryInternet() {
            }
        })){

            WebApi.getInstance().notificationsRead(lastIds,lastTimeref);
        }else{
            //Toast.makeText(CartActivity.this,getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
        }


            //lastIds = null;
            //lastTimeref = null;
        //}

        return view;
    }


    void runPromos() {
        Runnable promoRunnable = new Runnable() {
            @Override
            public void run() {

                if (allPromo == null) {
                    if (!Utility.isNetworkConnected(getActivity(), new InternetCallback() {
                        @Override
                        public void retryInternet() {
                        }
                    })){
                        //Toast.makeText(CartActivity.this,getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    allPromo = WebApi.getInstance().adsNotified();
                }




                promoRecycler.setHasFixedSize(true);
                promoRecycler.setNestedScrollingEnabled(false);
                LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                promoRecycler.setLayoutManager(layoutManager);

                promoAdapter = new PromoRecyclerView(allPromo,null);
                promoRecycler.setAdapter(promoAdapter);


            }
        };

        Handler ph = new Handler();
        ph.postDelayed(promoRunnable,300);
    }

    public void scrollToTop() {
        promoRecycler.scrollToPosition(0);
    }




}
