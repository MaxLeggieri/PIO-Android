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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Max on 21/06/2017.
 */

public class ShowcaseFragment extends Fragment implements ShowcaseRecyclerView.OnRecyclerScrollListener, CategoryRecyclerView.CategoryChangedListener {

    private static final String tag = PromoFragment.class.getSimpleName();

    public ShowcaseFragment() {
        // Required empty public constructor
    }


    MainActivity mainActivity;

    RecyclerView productsRecycler;
    ArrayList<Product> allProducts;
    ShowcaseRecyclerView productsAdapter;



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
        view = inflater.inflate(R.layout.fragment_showcase, container, false);

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
                allProducts = null;
                runProducts();
                swipeRefresh.setRefreshing(false);

            }
        });

        productsRecycler = (RecyclerView) view.findViewById(R.id.productsRecycler);


        runProducts();

        runCategories();

        return view;
    }

    void reloadProducts(final String cats) {

        page = 1;
        lastCatSelected = cats;

        Log.e(tag,"reloadPromos...");

        Runnable catRunnable = new Runnable() {
            @Override
            public void run() {
                if (!Utility.isNetworkConnected(getActivity())){
                    Toast.makeText(getActivity(),getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
                    return ;
                }
                allProducts = WebApi.getInstance().productsByCats(lastCatSelected,page);
                productsAdapter.setItems(allProducts);
            }
        };


        Handler ch = new Handler();
        ch.postDelayed(catRunnable,200);
    }

    void addProducts(final String cats) {

        Log.e(tag,"addProducts...");
        lastCatSelected = cats;

        page+=1;

        Runnable catRunnable = new Runnable() {
            @Override
            public void run() {
                if (!Utility.isNetworkConnected(getActivity())){
                    Toast.makeText(getActivity(),getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
                    return ;
                }
                List<Product> newProds = WebApi.getInstance().productsByCats(lastCatSelected,page);
                Log.e(tag,"Adding "+newProds.size()+" items...");
                if (newProds.size() > 0) {
                    allProducts.addAll(newProds);
                    productsAdapter.setItems(allProducts);
                }
            }
        };


        Handler ch = new Handler();
        ch.postDelayed(catRunnable,200);

    }

    void runProducts() {
        Runnable prodRunnable = new Runnable() {
            @Override
            public void run() {

                if (allProducts == null) {
                    if (!Utility.isNetworkConnected(getActivity())){
                        Toast.makeText(getActivity(),getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    allProducts = WebApi.getInstance().productsByCats(lastCatSelected,page);
                }




                productsRecycler.setHasFixedSize(true);
                productsRecycler.setNestedScrollingEnabled(false);
                LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                productsRecycler.setLayoutManager(layoutManager);

                productsAdapter = new ShowcaseRecyclerView(allProducts,ShowcaseFragment.this);
                productsRecycler.setAdapter(productsAdapter);


            }
        };

        Handler ph = new Handler();
        ph.postDelayed(prodRunnable,300);
    }

    void runCategories() {
        Runnable catRunnable = new Runnable() {
            @Override
            public void run() {
                if(WebApi.getInstance().allProductCats == null) {

                    WebApi.getInstance().allProductCats = WebApi.getInstance().getAllCategories();

                    Category all = new Category();
                    all.selected = false;
                    all.name = "Tutte le categorie";
                    all.cid = -1;

                    Category favorites = new Category();
                    favorites.selected = true;
                    favorites.name = "Mi interessa";
                    favorites.cid = 0;



                    WebApi.getInstance().allProductCats.add(0,favorites);
                    WebApi.getInstance().allProductCats.add(1,all);

                }


                catRecycler = (RecyclerView) view.findViewById(R.id.catRecycler);
                catRecycler.setHasFixedSize(false);
                catRecycler.setNestedScrollingEnabled(false);
                LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                catRecycler.setLayoutManager(layoutManager);

                catAdapter = new CategoryRecyclerView(WebApi.getInstance().allProductCats,ShowcaseFragment.this);
                catRecycler.setAdapter(catAdapter);

            }
        };

        Handler ch = new Handler();
        ch.postDelayed(catRunnable,400);
    }

    public void scrollToTop() {
        productsRecycler.scrollToPosition(0);
    }

    @Override
    public void onNearEndScrollListener() {
        Log.e(tag,"onNearEndScrollListener...");
        addProducts(lastCatSelected);
    }

    @Override
    public void onCategoryChanged(String category) {
        catAdapter.setItems(WebApi.getInstance().allProductCats);
        lastCatSelected = category;
        scrollToTop();
        reloadProducts(category);
    }
}
