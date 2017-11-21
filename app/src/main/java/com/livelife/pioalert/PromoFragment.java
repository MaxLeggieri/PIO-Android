package com.livelife.pioalert;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Max on 08/06/2017.
 */

public class PromoFragment extends Fragment implements PromoRecyclerView.OnRecyclerScrollListener, CategoryRecyclerView.CategoryChangedListener {

    private static final String tag = PromoFragment.class.getSimpleName();

    public PromoFragment() {
        // Required empty public constructor
    }


    MainActivity mainActivity;

    RecyclerView promoRecycler;
    ArrayList<Promo> allPromo;
    PromoRecyclerView promoAdapter;

    RecyclerView catRecycler;
    //ArrayList<Category> allCat;
    CategoryRecyclerView catAdapter;



    SwipeRefreshLayout swipeRefresh;

    int page = 1;

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




        view = inflater.inflate(R.layout.fragment_promo, container, false);

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

        runCategories();

        return view;
    }

    void reloadPromos(final String cats) {

        page = 1;
        lastCatSelected = cats;

        Log.e(tag,"reloadPromos...");

        Runnable catRunnable = new Runnable() {
            @Override
            public void run() {
                if (!Utility.isNetworkConnected(getActivity(), new InternetCallback() {
                    @Override
                    public void retryInternet() {
                    }
                })){
                    //Toast.makeText(CartActivity.this,getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
                    return ;
                }
                allPromo = WebApi.getInstance().getAds2user(page,lastCatSelected);
                promoAdapter.setItems(allPromo);
            }
        };


        Handler ch = new Handler();
        ch.postDelayed(catRunnable,200);
    }

    void addPromos(final String cats) {

        Log.e(tag,"addPromos...");
        lastCatSelected = cats;

        page+=1;

        Runnable catRunnable = new Runnable() {
            @Override
            public void run() {
                if (!Utility.isNetworkConnected(getActivity(), new InternetCallback() {
                    @Override
                    public void retryInternet() {
                    }
                })){
                    //Toast.makeText(CartActivity.this,getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
                    return ;
                }
                List<Promo> newPromos = WebApi.getInstance().getAds2user(page,lastCatSelected);
                Log.e(tag,"Adding "+newPromos.size()+" items...");
                if (newPromos.size() > 0) {
                    allPromo.addAll(newPromos);
                    promoAdapter.setItems(allPromo);
                }
            }
        };


        Handler ch = new Handler();
        ch.postDelayed(catRunnable,200);

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
                    allPromo = WebApi.getInstance().getAds2user(page,lastCatSelected);
                }




                promoRecycler.setHasFixedSize(true);
                promoRecycler.setNestedScrollingEnabled(false);
                LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                promoRecycler.setLayoutManager(layoutManager);

                promoAdapter = new PromoRecyclerView(allPromo,PromoFragment.this);
                promoRecycler.setAdapter(promoAdapter);


            }
        };

        Handler ph = new Handler();
        ph.postDelayed(promoRunnable,300);
    }

    void runCategories() {
        Runnable catRunnable = new Runnable() {
            @Override
            public void run() {
                if(WebApi.getInstance().allPromoCats == null) {

                    WebApi.getInstance().allPromoCats = WebApi.getInstance().getAllCategories();

                    Category all = new Category();
                    all.selected = false;
                    all.name = "Tutte le categorie";
                    all.cid = -1;

                    Category favorites = new Category();
                    favorites.selected = true;
                    favorites.name = "Mi interessa";
                    favorites.cid = 0;



                    WebApi.getInstance().allPromoCats.add(0,favorites);
                    WebApi.getInstance().allPromoCats.add(1,all);

                }


                catRecycler = (RecyclerView) view.findViewById(R.id.catRecycler);
                catRecycler.setHasFixedSize(false);
                catRecycler.setNestedScrollingEnabled(false);
                LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                catRecycler.setLayoutManager(layoutManager);

                catAdapter = new CategoryRecyclerView(WebApi.getInstance().allPromoCats,PromoFragment.this);
                catRecycler.setAdapter(catAdapter);

            }
        };

        Handler ch = new Handler();
        ch.postDelayed(catRunnable,400);
    }

    public void scrollToTop() {
        promoRecycler.scrollToPosition(0);
    }

    @Override
    public void onNearEndScrollListener() {
        Log.e(tag,"onNearEndScrollListener...");
        addPromos(lastCatSelected);
    }

    @Override
    public void onCategoryChanged(String category) {
        catAdapter.setItems(WebApi.getInstance().allPromoCats);
        lastCatSelected = category;
        scrollToTop();
        reloadPromos(category);
    }
}
