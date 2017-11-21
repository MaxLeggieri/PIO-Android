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
 * Created by Max on 23/06/2017.
 */

public class ShopFragment extends Fragment implements CompanyRecyclerView.OnRecyclerScrollListener, CategoryRecyclerView.CategoryChangedListener {

    private static final String tag = PromoFragment.class.getSimpleName();

    public ShopFragment() {
        // Required empty public constructor
    }


    MainActivity mainActivity;

    RecyclerView companiesRecycler;
    ArrayList<Company> allCompanies;
    CompanyRecyclerView companiesAdapter;



    RecyclerView catRecycler;
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
        view = inflater.inflate(R.layout.fragment_company, container, false);

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
                allCompanies = null;
                runCompanies();
                swipeRefresh.setRefreshing(false);

            }
        });

        companiesRecycler = (RecyclerView) view.findViewById(R.id.companiesRecycler);


        runCompanies();

        runCategories();

        return view;
    }

    void reloadCompanies(final String cats) {

        page = 1;
        lastCatSelected = cats;

        Log.e(tag,"reloadCompanies...");

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
                allCompanies = WebApi.getInstance().getCompanies(page,lastCatSelected,null);
                companiesAdapter.setItems(allCompanies);
            }
        };


        Handler ch = new Handler();
        ch.postDelayed(catRunnable,200);
    }

    void addCompanies(final String cats) {

        Log.e(tag,"addCompanies...");
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
                List<Company> newComps = WebApi.getInstance().getCompanies(page,lastCatSelected,null);
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

    void runCompanies() {
        Runnable compRunnable = new Runnable() {
            @Override
            public void run() {

                if (allCompanies == null) {
                    if (!Utility.isNetworkConnected(getActivity(), new InternetCallback() {
                        @Override
                        public void retryInternet() {
                        }
                    })){
                        //Toast.makeText(CartActivity.this,getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    allCompanies = WebApi.getInstance().getCompanies(page,lastCatSelected,null);
                }




                companiesRecycler.setHasFixedSize(true);
                companiesRecycler.setNestedScrollingEnabled(false);
                LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                companiesRecycler.setLayoutManager(layoutManager);

                companiesAdapter = new CompanyRecyclerView(allCompanies,ShopFragment.this);
                companiesRecycler.setAdapter(companiesAdapter);


            }
        };

        Handler ph = new Handler();
        ph.postDelayed(compRunnable,300);
    }

    void runCategories() {
        Runnable catRunnable = new Runnable() {
            @Override
            public void run() {
                if(WebApi.getInstance().allCompanyCats == null) {

                    WebApi.getInstance().allCompanyCats = WebApi.getInstance().getAllCategories();

                    Category all = new Category();
                    all.selected = false;
                    all.name = "Tutte le categorie";
                    all.cid = -1;

                    Category favorites = new Category();
                    favorites.selected = true;
                    favorites.name = "Mi interessa";
                    favorites.cid = 0;



                    WebApi.getInstance().allCompanyCats.add(0,favorites);
                    WebApi.getInstance().allCompanyCats.add(1,all);

                }


                catRecycler = (RecyclerView) view.findViewById(R.id.catRecycler);
                catRecycler.setHasFixedSize(false);
                catRecycler.setNestedScrollingEnabled(false);
                LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                catRecycler.setLayoutManager(layoutManager);

                catAdapter = new CategoryRecyclerView(WebApi.getInstance().allCompanyCats,ShopFragment.this);
                catRecycler.setAdapter(catAdapter);

            }
        };

        Handler ch = new Handler();
        ch.postDelayed(catRunnable,400);
    }

    public void scrollToTop() {
        companiesRecycler.scrollToPosition(0);
    }

    @Override
    public void onNearEndScrollListener() {
        Log.e(tag,"onNearEndScrollListener...");
        addCompanies(lastCatSelected);

    }

    @Override
    public void onCategoryChanged(String category) {
        catAdapter.setItems(WebApi.getInstance().allCompanyCats);
        lastCatSelected = category;
        scrollToTop();
        reloadCompanies(category);
    }


}
