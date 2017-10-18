package com.livelife.pioalert;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

//import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import com.facebook.FacebookSdk;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.config.ACRAConfiguration;
import org.acra.config.ConfigurationBuilder;
import org.acra.sender.HttpSender;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Max on 12/01/2017.
 */

@ReportsCrashes(
        reportType = HttpSender.Type.JSON,
        formUri = "https://www.crgs.it/moto/crashReport.php",
        customReportContent = {
                ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL,
                ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT},
        mode = ReportingInteractionMode.SILENT,
        resToastText = R.string.crash_toast_text
)

public class PioAlert extends Application implements BootstrapNotifier {

    //private BackgroundPowerSaver backgroundPowerSaver;
    public static final String PACKAGE_NAME = PioAlert.class.getPackage().getName();

    private static PioAlert mInstance;



    public static synchronized PioAlert getInstance() {
        return mInstance;
    }
    private RegionBootstrap regionBootstrap;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.DEV_LOGGING = false;
        ACRA.init(this);
    }

    public void onCreate() {
        super.onCreate();
        mInstance = this;

        // Simply constructing this class and holding a reference to it in your custom Application class
        // enables auto battery saving of about 60%
        //backgroundPowerSaver = new BackgroundPowerSaver(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Lato-Medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

        Region region = new Region("com.livelife.pioalert.boostrapRegion", null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);

    }


    @Override
    public void didEnterRegion(Region region) {
        regionBootstrap.disable();


        /*
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        */
    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }

}
