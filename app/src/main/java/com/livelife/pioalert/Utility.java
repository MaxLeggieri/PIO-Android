package com.livelife.pioalert;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by Max on 14/01/2017.
 */

public class Utility {


    private static final String tag = Utility.class.getSimpleName();

    static class SingletonHolder {
        static final Utility INSTANCE = new Utility();
    }

    protected Utility() {
        // Exists only to defeat instantiation.
    }

    public static synchronized Utility getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public AlertDialog getAlertDialog(String title, String message, final Activity context, boolean customButton) {
        AlertDialog alertDialog = new AlertDialog.Builder(context,android.R.style.Theme_Material_Dialog_Alert).create();

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        if(!customButton) {
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //context.finish();
                        }
                    });
        }



        return alertDialog;
    }


    public AlertDialog getAlertDialog(String title, String message, final Activity context) {
        return getAlertDialog(title,message,context,false);
    }


    public String getFormattedPrice(Object price) {

        if(price instanceof Double) {
            return String.format("€ %.2f",price);
        }
        else if (price instanceof String) {
            return String.format("€ %.2f",Double.parseDouble((String) price));
        }
        else {
            return "";
        }

    }

    /*
    public String getOrderStatus(Order order) {

        long time = System.currentTimeMillis()/1000;

        String res = "";

        String[] createdArr = TextUtils.split(order.createdHuman," ");
        String created = createdArr[0]+" "+createdArr[1];

        String[] deliveryArr = TextUtils.split(order.deliveryTimeHuman," ");
        String delivery = deliveryArr[0]+" "+deliveryArr[1];

        String[] cutoffArr = TextUtils.split(order.cutoffTimeHuman," ");
        String cutoff = cutoffArr[0]+" "+cutoffArr[1];


        long deliveryDayStart = order.deliveryTime - (3600*16);
        if (time < order.cutoffTime) {
            res = "Ordine ricevuto "+created;
        }
        else if (time > order.cutoffTime && time < deliveryDayStart) {
            res = "Spedito "+cutoff;
        }
        else {

            if (time > order.deliveryTime) {
                res = "Consegnato il "+delivery;
            } else {
                res = "In consegna oggi";
            }

        }

        return res;


    }
    */

    /*
    public void runFadeInAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(350);
        anim.setRepeatCount(1);
        //anim.setRepeatMode(Animation.REVERSE);
        view.startAnimation(anim);
    }
    */

    public void SendLogcatMail(Activity activity){

        // save logcat in file
        File outputFile = new File(Environment.getExternalStorageDirectory(),
                "logcat.txt");
        try {
            Runtime.getRuntime().exec(
                    "logcat -f " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //send file using email
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // Set type to "email"
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {"maxleggieri.test@gmail.com"};
        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
        // the attachment
        emailIntent .putExtra(Intent.EXTRA_STREAM, outputFile.getAbsolutePath());
        // the mail subject
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject");
        activity.startActivity(Intent.createChooser(emailIntent , "Send email..."));
    }

    /**TO check the Internet*/

    public static boolean isNetworkConnected(Context context, InternetCallback internetCallback) {
        boolean isNetAvail = false;

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isNetAvail = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (! isNetAvail){
            showAlertDialog(context,internetCallback);
        }
        return isNetAvail;
    }

    public  static AlertDialog.Builder builder;
    public static AlertDialog alert;
    private static void showAlertDialog(Context context, final InternetCallback internetCallback) {

        if (builder==null){
            builder = new AlertDialog.Builder(context);
            builder.setMessage("Please check your Internet connection!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            dialog.dismiss();
                            internetCallback.retryInternet();
                        }
                    });
        }

        if (alert==null){
            alert = builder.create();
        }

        if (!alert.isShowing()){
            alert.show();
        }


    }
}
