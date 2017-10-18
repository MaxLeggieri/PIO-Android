package com.livelife.pioalert;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;


public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, BeaconConsumer {


    private static final String tag = LocationService.class.getSimpleName();
    int uid;
    String deviceToken;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;
    private BeaconManager beaconManager;

    private final IBinder mBinder = new LocalBinder();
    public class LocalBinder extends Binder {
        LocationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocationService.this;
        }
    }

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;


    @Override
    public void onCreate() {
        super.onCreate();


        buildGoogleApiClient();
        beaconManager = BeaconManager.getInstanceForApplication(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            uid = intent.getIntExtra("uid", 0);
            deviceToken = intent.getStringExtra("deviceToken");
        } else {
            uid = getSharedPreferences("PIO",0).getInt("uid",0);
            deviceToken = getSharedPreferences("PIO",0).getString("deviceToken","");
        }

        Log.e(tag,"Started LocationService with uid: "+uid+" and deviceToken: "+deviceToken);


        mGoogleApiClient.connect();

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }

        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);

        return START_STICKY;

    }


    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(5000);

        mLocationRequest.setFastestInterval(2000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {

        try {

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        } catch (SecurityException ex) {


        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }


    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {

                new Thread(new Runnable() {
                    public void run() {
                        Log.e("onBeaconServiceConnect","didRangeBeaconsInRegion... "+beacons.size());

                        if (beacons.size() > 0) {

                            for (final Beacon b: beacons) {

                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendBeaconData(b);
                                    }
                                });


                            }

                        }
                    }
                }).start();


            }
        });

        try {
            Log.e("PioBeaconService","Starting beacon service");
            beaconManager.startRangingBeaconsInRegion(new Region("com.livelife.pioalert.boostrapRegion",
                    Identifier.parse("7ADF3A88-FCCF-4C68-B9E0-F12143E3FCDB"),null,null));
        } catch (RemoteException e) { e.printStackTrace(); }
    }

    @Override
    public void onDestroy() {

        Log.e(tag,tag+" destroyed...");

        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        beaconManager.unbind(this);

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }


    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) throws SecurityException {
        Log.e(tag, "Connected to GoogleApiClient");


        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }


        if (mCurrentLocation != null) {
            PioUser.getInstance().setUserLocation(mCurrentLocation,getApplicationContext());
        }

        startLocationUpdates();

    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {

        Log.e(tag,"Location changed...");

        if (location == null) return;

        mCurrentLocation = location;

        PioUser.getInstance().setUserLocation(location,getApplicationContext());

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                sendGeo();
            }
        });

    }

    @Override
    public void onConnectionSuspended(int cause) {

        Log.e(tag, "onConnectionSuspended...");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        Log.e(tag, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }




    public void sendGeo() {

        HttpURLConnection c = null;
        try {

            String query  = "method=sendGeo";
            query += "&uid="+uid;
            query += "&device_token="+deviceToken;
            query += "&lat=" + mCurrentLocation.getLatitude();
            query += "&lng=" + mCurrentLocation.getLongitude();


            //String url = "http://www.crgs.it/moto/loc.php?"+query;
            String url = "https://www.pioalert.com/api/?"+query;

            Log.e(tag,"Calling: "+url);

            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:

                    Log.e(tag,"sendGeo sent...");

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

    }


    public boolean sendBeaconData(Beacon beacon) {

        String query = "method=sendBeaconData";
        query += "&device_token="+deviceToken;
        query += "&uid="+uid;
        query += "&uuid="+beacon.getId1().toUuid().toString();
        query += "&idmajor="+beacon.getId2().toInt();
        query += "&idminor="+beacon.getId3().toInt();
        query += "&accuracy="+beacon.getDistance();

        String url = "https://www.pioalert.com/api/?"+query;
        Log.e(tag,"Calling: "+url);

        HttpURLConnection c = null;
        try {


            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:

                    return true;

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        return false;

    }
}
