package com.livelife.pioalert;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements PioUser.PioUserListener, OnMapReadyCallback {

    private static final String tag = HomeFragment.class.getSimpleName();

    GoogleMap map;
    MapView mapView;
    JSONObject home;
    ImageButton zoomToUserButton;

    ScrollView scrollView;
    ImageView com1ImageView;
    TextView com1TextView;

    ImageView com2ImageView;
    TextView com2TextView;

    EditText searchEditText;

    ListView searchResultsListView;
    ArrayAdapter<String> searchResultsAdapter;
    ArrayList<String> items = new ArrayList<>();
    JSONArray searchResults;

    RecyclerView recyclerView,com1RecyclerView,com2RecyclerView;


    AppBarLayout appBar;
    FrameLayout homeContainer;
    ImageButton refreshButton;

    ImageButton cancelSearchButton;

    String lastSearch = "";
    int lastCat = 0;

    boolean textRefreshed = false;

    ImageButton menuButton;

    Button comButton;

    public HomeFragment() {
        // Required empty public constructor
    }


    MainActivity mainActivity;

    public void handleMapView(Bundle savedInstanceState) {

        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        updateMapAndResults();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e(tag,"onCreateView...");

        if(savedInstanceState != null) {
            Log.e(tag,"savedInstanceState not NULL...");
        }

        mainActivity = (MainActivity) getActivity();

        if (view != null) {

            Log.e(tag,"view is NOT null...");


            handleMapView(savedInstanceState);

            return view;

        }



        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        appBar = (AppBarLayout) view.findViewById(R.id.appBar);
        homeContainer = (FrameLayout) view.findViewById(R.id.homeContainer);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);

        if(!homeStarted) {
            scrollView.setVisibility(View.INVISIBLE);
        }

        refreshButton = (ImageButton) view.findViewById(R.id.refreshButton);
        refreshButton.setAdjustViewBounds(true);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (homeStarted) {
                    if (!Utility.isNetworkConnected(getActivity())){
                        Toast.makeText(getActivity(),getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    home = WebApi.getInstance().home(1,null,0);
                    updateMapAndResults();

                }
            }
        });

        rotate(refreshButton);


        menuButton = (ImageButton) view.findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //drawerLayout.openDrawer(Gravity.LEFT);
                mainActivity.openDrawer();
            }
        });

        items.add(getResources().getString(R.string.no_results));
        searchResultsListView = (ListView) view.findViewById(R.id.searchResultsListView);
        searchResultsAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.simple_custom_list_item, R.id.text1, items);
        searchResultsListView.setAdapter(searchResultsAdapter);
        searchResultsListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

                return false;
            }
        });
        searchResultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Log.e(tag,"Clicked item "+i);
                    final JSONObject res = searchResults.getJSONObject(i);
                    textRefreshed = true;
                    searchEditText.clearFocus();
                    searchResultsListView.setVisibility(View.GONE);
                    scrollView.scrollTo(0, 0);
                    lastCat = res.getInt("idcat");
                    searchEditText.setText(res.getString("name"));
                    if (!Utility.isNetworkConnected(getActivity())){
                        Toast.makeText(getActivity(),getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    home = WebApi.getInstance().home(1,res.getString("name"),lastCat);
                    updateMapAndResults();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        cancelSearchButton = (ImageButton) view.findViewById(R.id.cancelSearchButton);
        cancelSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEditText.setText("");
                searchResultsListView.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                cancelSearchButton.setVisibility(View.GONE);

            }
        });



        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        com1RecyclerView = (RecyclerView) view.findViewById(R.id.com1RecyclerView);
        com2RecyclerView = (RecyclerView) view.findViewById(R.id.com2RecyclerView);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity());
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager2.scrollToPosition(0);
        com1RecyclerView.setLayoutManager(layoutManager2);

        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getActivity());
        layoutManager3.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager3.scrollToPosition(0);
        com2RecyclerView.setLayoutManager(layoutManager3);

        zoomToUserButton = (ImageButton) view.findViewById(R.id.zoomToUserButton);
        zoomToUserButton.setEnabled(false);
        zoomToUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                zoomToUser();

            }
        });

        com1ImageView = (ImageView) view.findViewById(R.id.com1ImageView);
        com1TextView = (TextView) view.findViewById(R.id.com1TextView);
        comButton = (Button) view.findViewById(R.id.comButton);
        comButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idCom = (int) view.getTag();
                Intent companyIntent = new Intent(getActivity(),CompanyActivity.class);
                companyIntent.putExtra("comId",idCom);
                getActivity().startActivity(companyIntent);
            }
        });



        com2ImageView = (ImageView) view.findViewById(R.id.com2ImageView);
        com2ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idCom = (int) view.getTag();
                if (idCom==0) return;
                Intent companyIntent = new Intent(getActivity(),CompanyActivity.class);
                companyIntent.putExtra("comId",idCom);
                getActivity().startActivity(companyIntent);
            }
        });

        com2TextView = (TextView) view.findViewById(R.id.com2TextView);

        searchEditText = (EditText) view.findViewById(R.id.searchEditText);
        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    autosuggest(searchEditText.getText().toString());
                }
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }


            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {



            }

            @Override
            public void afterTextChanged(Editable editable) {
                final String text = editable.toString();

                if (textRefreshed) {
                    textRefreshed = false;
                    return;
                }

                if (lastSearch.equals(editable.toString())) {
                    return;
                }
                Log.e(tag,"afterTextChanged... "+text);
                if (text.length() == 0) return;

                autosuggest(text);

                lastSearch = text;
            }
        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ( (actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN ))){

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                    cancelSearchButton.setVisibility(View.GONE);

                    lastSearch = searchEditText.getText().toString();
                    searchEditText.clearFocus();
                    searchResultsListView.setVisibility(View.GONE);
                    scrollView.scrollTo(0, 0);
                    if (!Utility.isNetworkConnected(getActivity())){
                        Toast.makeText(getActivity(),getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    home = WebApi.getInstance().home(1,lastSearch,0);
                    updateMapAndResults();



                    return true;
                }
                else{
                    return false;
                }
            }
        });


        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(this);
        PioUser.getInstance().addListener(this);

        return view;




    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }

    public void rotate(View v) {

        final RotateAnimation wheelRotation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        wheelRotation.setDuration(1500);
        wheelRotation.setInterpolator(getActivity(), android.R.interpolator.accelerate_decelerate);
        refreshButton.startAnimation(wheelRotation);

        wheelRotation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                if (!homeStarted) {
                    refreshButton.startAnimation(wheelRotation);
                }
            }

            public void onAnimationRepeat(Animation animation) {

            }

            public void onAnimationStart(Animation animation) {
                //Log.e("RotationActivity", "rotation started...");
            }
        });
    }


    public void autosuggest(final String text) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!Utility.isNetworkConnected(getActivity())){
                    Toast.makeText(getActivity(),getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
                    return ;
                }
                searchResults = WebApi.getInstance().autosuggest(text);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (searchResults.length() > 0) {
                            Log.e(tag,"Got "+searchResults.length()+" results...");
                            searchResultsListView.setVisibility(View.VISIBLE);
                            cancelSearchButton.setVisibility(View.VISIBLE);
                        } else {
                            searchResultsListView.setVisibility(View.GONE);
                            cancelSearchButton.setVisibility(View.GONE);
                            Log.e(tag,"No results...");
                        }

                        searchResultsAdapter.clear();

                        for (int i = 0; i < searchResults.length(); i++) {
                            try {
                                JSONObject res = searchResults.getJSONObject(i);
                                searchResultsAdapter.add(res.getString("name"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        searchResultsAdapter.notifyDataSetChanged();
                    }
                });


            }
        }).start();







    }


    public void checkLocalization() {

        LocationManager service = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        boolean gpsEnabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        boolean networkEnabled = service
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gpsEnabled && !networkEnabled) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

            dialog.setMessage("La geo-localizzazione non è attiva");
            dialog.setTitle("Attiva la localizzazione");
            dialog.setPositiveButton("Attiva", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(myIntent);

                }
            });
            dialog.setNegativeButton("Ignora", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                    Log.e(tag,"User did not activated location...");
                }
            });
            dialog.show();
        }

    }

    @Override
    public void onResume() {
        Log.e(tag,"onResume...");

        checkLocalization();

        super.onResume();
        mapView.onResume();

    }

    @Override
    public void onPause() {
        Log.e(tag,"onPause...");
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        Log.e(tag,"onDestroy...");
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        Log.e(tag,"onLowMemory...");
        super.onLowMemory();
        mapView.onLowMemory();
    }


    Marker mPositionMarker;
    boolean homeStarted = false;

    @Override
    public void onUserPositionChanged(final Location loc) {


        /*
        LatLng croplatlng = new LatLng(loc.getLatitude(), loc.getLongitude());
        mo.position(croplatlng);
        */


        if (loc == null || map == null) {

            Log.e(tag,"loc or map null...");

            return;
        }

        if (mPositionMarker == null) {
            mPositionMarker = map.addMarker(new MarkerOptions()
                    .flat(true)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.you))
                    .anchor(0.5f, 0.5f)
                    .position(
                            new LatLng(loc.getLatitude(), loc
                                    .getLongitude())));


        }

        mPositionMarker.setPosition(new LatLng(loc.getLatitude(), loc
                .getLongitude()));
        mPositionMarker.setRotation(loc.getBearing());
        mPositionMarker.setTag(9999);
        //mPositionMarker.setZIndex(-1);



        if (!homeStarted && isAdded() && mainActivity != null) {
            if (!Utility.isNetworkConnected(getActivity())){
                Toast.makeText(getActivity(),getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
                return ;
            }
            //mainActivity.initializeBeacons();
            home = WebApi.getInstance().home(1, null, 0);

            /*
                    LatLng croplatlng = new LatLng(loc.getLatitude(), loc.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(croplatlng, 16);
                    map.animateCamera(cameraUpdate);
                    */

            scrollView.setVisibility(View.VISIBLE);
            updateMapAndResults();

            /*
            new Thread(new Runnable() {
                @Override
                public void run() {






                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });


                }
            }).start();
            */



        } else {
            //Log.e(tag,"Home already started...");
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mainActivity = (MainActivity) getActivity();
    }

    public void zoomToUser() {

        LatLng croplatlng = new LatLng(PioUser.getInstance().location.getLatitude(), PioUser.getInstance().location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(croplatlng, 15);
        map.animateCamera(cameraUpdate);
    }

    public void zoomToMarkers() {


        int padding = 100; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(markersBounds, padding);

        map.animateCamera(cu);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        map = googleMap;
        map.getUiSettings().setMapToolbarEnabled(true);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                int pos = 0;

                if (marker.getTag() instanceof PioPlace) {
                   Log.v(tag,"PioPlace tag...");
                } else {
                    int tag = (int) marker.getTag();

                    if (tag == 9999) {
                        return true;
                    } else {
                        pos = (int) marker.getTag();
                    }


                    marker.showInfoWindow();
                    recyclerView.scrollToPosition(pos);


                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), map.getCameraPosition().zoom);
                    map.animateCamera(cameraUpdate);
                }
                return false;
            }
        });

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                if (marker.getTag() instanceof Integer) return;

                PioPlace pp = (PioPlace) marker.getTag();

                Log.v(tag,"PioPlace clicked: "+pp.name);

                Intent i = new Intent(getActivity(),PioPlaceActivity.class);
                i.putExtra("name",pp.name);
                i.putExtra("placeId",pp.placeId);
                i.putExtra("photoReference",pp.photoReference);
                i.putExtra("address",pp.address);
                startActivity(i);
            }
        });



        zoomToUserButton.setEnabled(true);


        Location loc = PioUser.getInstance().location;
        if (loc == null) return;


        addUserMarker();

        LatLng croplatlng = new LatLng(loc.getLatitude(),loc.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(croplatlng, 16);
        map.animateCamera(cameraUpdate);

    }

    JSONArray mapResults,com1ProdResults,com2ProdeResults;
    JSONObject com1Results,com2Results;

    RecycleViewAdapter mapResultsAdapter;
    RecycleViewAdapter com1ResultsAdapter;
    RecycleViewAdapter com2ResultsAdapter;

    final ArrayList<Marker> markers = new ArrayList<>();

    LatLngBounds markersBounds;

    Thread homeThread;

    private void addMarker(final JSONObject res, final int pos, LatLngBounds.Builder builder) throws JSONException {

        String coord = res.getString("LL");
        final String [] cArr = TextUtils.split(coord,",");
        //final int pos = i;


        builder.include(new LatLng(Double.parseDouble(cArr[0]), Double.parseDouble(cArr[1])));

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                String type = null;
                try {
                    type = res.getString("type");
                    Bitmap bg = BitmapFactory.decodeResource(mainActivity.getResources(),R.drawable.icon_geolocal_bg);
                    int w = Math.round(bg.getWidth()*0.8f);//(int)();
                    String imgPath = "";
                    if (type.equals("ad")) {
                        imgPath = res.getString("brandimg");
                    } else {
                        imgPath = res.getString("img");
                    }

                    Bitmap ad = Picasso.with(getActivity()).load("http://pioalert.com"+imgPath).resize(w,w).get();
                    final Bitmap overlay = overlay(bg,getCroppedBitmap(ad));

                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Marker m = null;
                            try {
                                m = map.addMarker(new MarkerOptions()
                                        .flat(false)
                                        .icon(BitmapDescriptorFactory.fromBitmap(overlay))
                                        .anchor(0.5f, 1.0f)
                                        .title(res.getString("title"))
                                        .snippet(res.getString("address"))
                                        .position(
                                                new LatLng(Double.parseDouble(cArr[0]), Double.parseDouble(cArr[1]))));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            m.setTag(pos);

                            markers.add(m);
                        }
                    });

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    void addUserMarker() {

        Log.e(tag, "addUserMarker...");

        Location loc = PioUser.getInstance().location;
        if (loc == null) return;
        mPositionMarker = map.addMarker(new MarkerOptions()
                .flat(true)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.you))
                .anchor(0.5f, 0.5f)
                .position(
                        new LatLng(loc.getLatitude(), loc
                                .getLongitude())));

        mPositionMarker.setPosition(new LatLng(loc.getLatitude(), loc
                .getLongitude()));
        mPositionMarker.setRotation(loc.getBearing());
        mPositionMarker.setTag(9999);

        //mPositionMarker.setZIndex(-1);
    }


    public void updateMapAndResults() {

        if(getActivity() == null || !isAdded()) {

            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateMapAndResults();
                }
            },1000);
            return;
        }

        if (getActivity() != null) {
            mainActivity = (MainActivity) getActivity();
        }

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rotate(refreshButton);
            }
        });


        Log.e(tag,"updateMapAndResults...");


        try {

            if (home == null) {
                if (!Utility.isNetworkConnected(getActivity())){
                    Toast.makeText(getActivity(),getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
                    return ;
                }
                home = WebApi.getInstance().home(1,lastSearch,lastCat);
            }

            /*
            if (home.has("unreadNotifiedAds")) {
                JSONObject unreadNotifiedAds = home.getJSONObject("unreadNotifiedAds");
                int unread = unreadNotifiedAds.getInt("howmany");
                String idads = unreadNotifiedAds.getString("idads");
                mainActivity.notificationFragment.lastIds = idads;
                mainActivity.setNotificationBadge(unread);
            }
            */

            mapResults = home.getJSONArray("map");
            Log.e(tag,"mapResults: "+mapResults.length());
            map.clear();

            addUserMarker();



            //map.getUiSettings().setMapToolbarEnabled(true);


            ArrayList<JSONObject> items = new ArrayList<>();
            markers.clear();



            final LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i=0; i < mapResults.length(); i++) {

                if (i >= 50) break;
                final JSONObject res = mapResults.getJSONObject(i);
                items.add(res);
                addMarker(res,i,builder);

            }


            if (mapResults.length() > 0) {
                markersBounds = builder.build();
            }

            if (mapResultsAdapter == null) {
                mapResultsAdapter = new RecycleViewAdapter(mainActivity, items);
                recyclerView.setHasFixedSize(false);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setAdapter(mapResultsAdapter);
            } else {
                mapResultsAdapter.setItems(items);
                mapResultsAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(0);
            }




            com1Results = home.getJSONObject("com1");
            comButton.setTag(com1Results.getInt("id"));
            com1ProdResults = home.getJSONArray("com1prd");

            Log.e(tag,"com1ProdResults: "+com1ProdResults.length());
            if (home.has("com2")) {
                com2Results = home.getJSONObject("com2");
                com2ImageView.setTag(com2Results.getInt("id"));
                String imgPath2 = com2Results.getString("img");
                Picasso.with(mainActivity).load("http://pioalert.com"+imgPath2).resize(120,120).into(com2ImageView);
                com2TextView.setText(com2Results.getString("brand").toUpperCase());
            }

            if (home.has("com2prd")) {
                com2ProdeResults = home.getJSONArray("com2prd");
            }


            // TODO

            Log.e(tag,"COM1: "+com1Results.toString(2));

            String imgPath = com1Results.getString("brandimg");
            Picasso.with(mainActivity).load("http://pioalert.com"+imgPath).resize(800,800).into(com1ImageView);

            com1TextView.setText(com1Results.getString("brand").toUpperCase());

            ArrayList<JSONObject> items1 = new ArrayList<>();
            for (int i = 0; i < com1ProdResults.length(); i++) {
                items1.add(com1ProdResults.getJSONObject(i));
            }

            Log.e(tag,"com1ProdResults items: "+items.size());
            if (com1ResultsAdapter == null) {
                com1ResultsAdapter = new RecycleViewAdapter(mainActivity, items1);
                com1RecyclerView.setHasFixedSize(false);
                com1RecyclerView.setNestedScrollingEnabled(false);
                com1RecyclerView.setAdapter(com1ResultsAdapter);
            } else {


                com1ResultsAdapter.setItems(items1);
                com1ResultsAdapter.notifyDataSetChanged();
                com1RecyclerView.scrollToPosition(0);

                if (items1.size() == 0) {
                    com1RecyclerView.setVisibility(View.GONE);
                }
            }

            ArrayList<JSONObject> items2 = new ArrayList<>();
            for (int i = 0; i < com2ProdeResults.length(); i++) {
                items2.add(com2ProdeResults.getJSONObject(i));
            }

            if (com2ResultsAdapter == null) {
                com2ResultsAdapter = new RecycleViewAdapter(mainActivity, items2);
                com1RecyclerView.setHasFixedSize(false);
                com1RecyclerView.setNestedScrollingEnabled(false);
                com2RecyclerView.setAdapter(com2ResultsAdapter);
            } else {
                com2ResultsAdapter.setItems(items2);
                com2ResultsAdapter.notifyDataSetChanged();
                com2RecyclerView.scrollToPosition(0);

                if (items2.size() == 0) {
                    com2RecyclerView.setVisibility(View.GONE);
                }
            }



            /*
            if (lastSearch != null) {
                zoomToMarkers();
            } else {
                zoomToUser();
            }
            */

            zoomToUser();

            homeStarted = true;


            getGooglePlaces();

        } catch (JSONException e) {
            e.printStackTrace();
        }




    }


    public void getGooglePlaces() {
        if (!Utility.isNetworkConnected(getActivity())){
            Toast.makeText(getActivity(),getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
            return ;
        }
        ArrayList<PioPlace> pioPlaces = WebApi.getInstance().getGooglePlaces();

        Log.v(tag,"PLACES: "+pioPlaces.size());


        for (final PioPlace pp: pioPlaces) {

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    try {
                        Bitmap bg = BitmapFactory.decodeResource(mainActivity.getResources(),R.drawable.icon_geolocal_bg);
                        int w = Math.round(bg.getWidth()*0.8f);//(int)();

                        Bitmap ad = Picasso.with(getActivity()).load(pp.icon).resize(w,w).get();
                        final Bitmap overlay = overlay(bg,getCroppedBitmap(ad));

                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Marker m = null;
                                m = map.addMarker(new MarkerOptions()
                                        .flat(false)
                                        .icon(BitmapDescriptorFactory.fromBitmap(overlay))
                                        .anchor(0.5f, 1.0f)
                                        .title(pp.name)
                                        .snippet(pp.address)
                                        .position(
                                                new LatLng(pp.lat, pp.lon)));
                                m.setTag(pp);


                                markers.add(m);
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });


        }

    }

    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {

        int gap = (bmp1.getWidth() - bmp2.getWidth()) / 2;


        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);

        canvas.drawBitmap(bmp2, gap, gap, null);
        
        return bmOverlay;
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;


        return output;
    }


}
