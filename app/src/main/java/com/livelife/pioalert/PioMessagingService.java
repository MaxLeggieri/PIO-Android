    package com.livelife.pioalert;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.zxing.common.StringUtils;

import java.util.ArrayList;

    /**
 * Created by Max on 08/01/2017.
 */

public class PioMessagingService extends FirebaseMessagingService {

    LocalBroadcastManager broadcaster;

        interface RemoteNotificationListener {
            public void onMessageReceived(RemoteMessage m);
        }

        ArrayList<RemoteNotificationListener> listeners = new ArrayList<>();




    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);



        Log.v("PioMessagingService", "Entire MESSAGE: " + remoteMessage.getNotification());

        Log.v("PioMessagingService", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            //Log.e("PioMessagingService", "Message data payload: " + remoteMessage.getData());

            String promoIds = remoteMessage.getData().get("idad");
            int nCount = promoIds.split(",").length;

            String timeref = remoteMessage.getData().get("timeref");
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            Log.e("PioMessagingService","Promo ids: "+promoIds);

            //PioUser.getInstance().setAdsToNotify(promoIds,timeref,title,body);
            String message = "MESSAGE_RECEIVED";

            Intent intent = new Intent("NOTIFICATION_RESULT");
            intent.putExtra("NOTIFICATION_MESSAGE", message);
            intent.putExtra("title", title);
            intent.putExtra("body", body);
            intent.putExtra("timeref",timeref);
            intent.putExtra("count",nCount);
            intent.putExtra("ids",promoIds);
            broadcaster.sendBroadcast(intent);

        }


    }


    public void sendResult(String message) {

        Log.e("RemoteMessage","sendResult...");

    }




}
