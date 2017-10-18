package com.livelife.pioalert;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import org.acra.ACRA;

import java.util.HashMap;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements DrawerLayout.DrawerListener {


    static final String tag = MainActivity.class.getSimpleName();
    private final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION=1;

    BroadcastReceiver receiver;

    LocationService mService;
    boolean mBound = false;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    ImageView userImageView;
    TextView userWelcomeTextView,userPointsTextView;

    AHBottomNavigation bottomNavigation;



    public void startHome() {

        if(homeFragment == null) {



            int permissionCheck = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showExplanation(getResources().getString(R.string.alert_location_permission_title), getResources().getString(R.string.alert_location_permission_message), android.Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
                } else {
                    requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
                }
            } else {
                // Already Granted
                Log.e(tag,"Already Granted permissions for LocationService...");
                Intent serviceIntent = new Intent(this, LocationService.class);
                serviceIntent.putExtra("uid",PioUser.getInstance().uid);
                serviceIntent.putExtra("deviceToken",WebApi.getInstance().deviceToken);
                startService(serviceIntent);

                PioPlayer you = WebApi.getInstance().userRanking();
                Log.v(tag,"PioPlayer: "+you.name);
                userPointsTextView.setText(""+you.score+" pts");

                homeFragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content, homeFragment, "HomeFragment").commit();
                bottomNavigation.setCurrentItem(0);


            }



        }


        checkNotificationIntent(getIntent());

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter("NOTIFICATION_RESULT")
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }



    @Override
    protected void onPause() {
        super.onPause();
        // TODO check if needed...
        //if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unbind from the service
        if (mBound) {
            mBound = false;
        }

        //beaconManager.unbind(this);
    }

    HomeFragment homeFragment;
    PromoFragment promoFragment = new PromoFragment();
    ShowcaseFragment showcaseFragment = new ShowcaseFragment();
    ShopFragment shopFragment = new ShopFragment();
    NotificationFragment notificationFragment = new NotificationFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebApi.getInstance().setMainActivity(this);

        WebApi.getInstance().deviceToken = getSharedPreferences("PIO", 0).getString("deviceToken","");
        if(WebApi.getInstance().deviceToken == "") {
            WebApi.getInstance().deviceToken = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
            SharedPreferences.Editor editor = this.getSharedPreferences("PIO",0).edit();
            editor.putString("deviceToken", WebApi.getInstance().deviceToken);
            editor.commit();
        }


        PioUser.getInstance().setContext(getApplicationContext());






        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        drawerLayout.addDrawerListener(this);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                switch (item.getItemId()) {
                    case R.id.nav_interest:

                        Intent i1 = new Intent(MainActivity.this,ProfileActivity.class);
                        startActivity(i1);

                        break;

                    case R.id.nav_carts:

                        Intent i2 = new Intent(MainActivity.this,AllCartsActivity.class);
                        startActivity(i2);

                        break;

                    case R.id.nav_orders:

                        Intent i3 = new Intent(MainActivity.this,OrdersActivity.class);
                        startActivity(i3);

                        break;

                    case R.id.nav_ranking:

                        Intent i4 = new Intent(MainActivity.this,RankingActivity.class);
                        startActivity(i4);

                        break;

                    case R.id.promo_express:
                        if (MyPreference.getCurrentUser()!=null){
                            Intent mIntentLogin = new Intent(MainActivity.this,CreateAdActivity.class);
                            startActivity(mIntentLogin);

                        }else{
                            Intent mIntentLogin = new Intent(MainActivity.this,LoginActivity.class);
                            startActivity(mIntentLogin);

                        }


                        break;

                    case R.id.nav_felix:

                        Intent i5 = new Intent(MainActivity.this,FelixActivity.class);
                        startActivity(i5);

                        break;


                    case R.id.nav_logout:

                        FirebaseAuth.getInstance().signOut();
                        PioUser.getInstance().setLogged(false);

                        drawerLayout.closeDrawer(Gravity.LEFT);

                        checkAppStatus();

                        break;


                    case R.id.nav_info:

                        Intent i6 = new Intent(MainActivity.this,InfoActivity.class);
                        startActivity(i6);

                        break;

                }

                return false;
            }
        });

        View navHeaderView = navigationView.getHeaderView(0);
        userImageView = (ImageView) navHeaderView.findViewById(R.id.userImageView);
        userWelcomeTextView = (TextView) navHeaderView.findViewById(R.id.userWelcomeTextView);
        userPointsTextView = (TextView) navHeaderView.findViewById(R.id.userPointsTextView);




        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottomNavigation);
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#fefefe"));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#fc8320"));



        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (PioUser.getInstance().location == null) {
                    return false;
                }

                switch (position) {
                    case 0:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content,homeFragment,"HomeFragment").commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content,promoFragment,"PromoFragment").commit();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content,showcaseFragment,"ShowcaseFragment").commit();
                        break;
                    case 3:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content,shopFragment,"ShopFragment").commit();
                        break;
                    case 4:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content,notificationFragment,"NotificationFragment").commit();
                        break;

                }

                return true;
            }
        });


        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Esplora", R.drawable.menu_esplora);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Promo", R.drawable.menu_promo);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Vetrina", R.drawable.menu_vetrina);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("Negozi", R.drawable.menu_negozi);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem("Notifiche", R.drawable.menu_notifiche);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);
        bottomNavigation.addItem(item5);

        //bottomNavigation.setForceTint(true);
        bottomNavigation.setAccentColor(Color.parseColor("#f8ca00"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));






        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra("NOTIFICATION_MESSAGE");
                String title = intent.getStringExtra("title");
                String body = intent.getStringExtra("body");
                String ids = intent.getStringExtra("ids");
                String timeref = intent.getStringExtra("timeref");
                int count = intent.getIntExtra("count",0);
                notificationFragment.lastIds = ids;
                notificationFragment.lastTimeref = timeref;

                Log.e(tag,"NOTIFICATION_MESSAGE: "+s);

                if(s.equals("MESSAGE_RECEIVED")) {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle(title);
                    dialog.setMessage(body);
                    dialog.setPositiveButton("Mostra", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.content, notificationFragment, "NotificationFragment").commit();
                            bottomNavigation.setCurrentItem(4);
                            bottomNavigation.setNotification("0",4);

                        }
                    });
                    dialog.setNegativeButton("Ignora", null);
                    dialog.show();

                    if (count != 0) {
                        bottomNavigation.setNotification(""+count+"",4);
                    }

                }
            }
        };

        final String nt = FirebaseInstanceId.getInstance().getToken();
        Log.v(tag,"Firebase token: "+nt);
        if (nt != null) {
            WebApi.getInstance().tokenHandler(nt);
        }

    }

    public void openDrawer() {
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    public void checkNotificationIntent(final Intent intent) {




        if (intent.getExtras() != null) {

            Log.e(tag,"intent: "+intent.getExtras().toString());
            Log.e(tag,"intent: "+intent.getType());

            if (intent.hasExtra("idad")) {
                int count = intent.getStringExtra("idad").split(",").length;
                bottomNavigation.setNotification(""+count+"",4);
                Log.e(tag,"intent: notified "+count+" to badge");

                if (intent.getBooleanExtra("notified",false) == false) {

                    /*
                    String title, body;
                    title = intent.getStringExtra("title");
                    body = intent.getStringExtra("body");
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle(title);
                    dialog.setMessage(body);
                    dialog.setPositiveButton("Mostra", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.content, notificationFragment, "NotificationFragment").commit();
                            bottomNavigation.setCurrentItem(4);

                        }
                    });
                    dialog.setNegativeButton("Ignora", null);

                    dialog.show();
                    */

                    intent.putExtra("notified", true);
                    bottomNavigation.setCurrentItem(4);
                    bottomNavigation.setNotification("0",4);

                } else {
                    Log.e(tag,"intent: Already notified...");
                }

            }


        }
        else {

            // Check notification from server...
            HashMap<String,String> unread = WebApi.getInstance().unreadNotifiedAds();

            if (!unread.containsKey("howmany")) {
                return;
            }

            int count = Integer.parseInt(unread.get("howmany"));
            String idad = unread.get("idad");

            if (count > 0) {
                bottomNavigation.setNotification(""+count+"",4);
                notificationFragment.lastIds = idad;
                Log.e(tag,"intent: notified "+unread+" to badge from API");
            }


        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        checkNotificationIntent(intent);


        String action = intent.getAction();
        String data = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            Log.e(tag,"deeplink: "+data);

            int idad = Integer.parseInt(intent.getData().getQueryParameter("idad"));
            int uid = Integer.parseInt(intent.getData().getQueryParameter("uid"));

            Intent pi = new Intent(this,PromoActivity.class);
            pi.putExtra("promoId",idad);
            startActivity(pi);
        }




    }

    @Override
    protected void onResume() {

        super.onResume();



        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAppStatus();
            }
        },1000);




    }

    public void checkAppStatus() {
        if (!PioUser.getInstance().logged) {
            Intent signin = new Intent(this,SigninActivity.class);
            startActivity(signin);
        }
        else if (!PioUser.getInstance().profiled) {
            Intent profile = new Intent(this,ProfileActivity.class);
            startActivity(profile);
        }

        else if (!PioUser.getInstance().consent) {
            Intent consent = new Intent(this,ConsentActivity.class);
            startActivity(consent);
        }
        else {
            Log.v(tag,"Ready to go... Starting home (if needed...)");

            if (!PioUser.getInstance().userImagePath.equals("")) {
                Picasso.with(this).load(PioUser.getInstance().userImagePath).into(userImageView);
            }
            userWelcomeTextView.setText("Ciao "+PioUser.getInstance().userName);


            ACRA.getErrorReporter().putCustomData("userName", PioUser.getInstance().userName);
            ACRA.getErrorReporter().putCustomData("lastName", PioUser.getInstance().lastName);



            startHome();
        }
    }



    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.e(tag,"User has grant access to LocationService");
                    // Bind to LocalService




                } else {

                    Log.e(tag,"User has denied access to location");
                }
        }
    }


    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }



    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }


    public void clearNotificationBadge() {
        bottomNavigation.setNotification("",4);
    }

    public void setNotificationBadge(int howmany) {
        bottomNavigation.setNotification(""+howmany+"",4);
    }


}
