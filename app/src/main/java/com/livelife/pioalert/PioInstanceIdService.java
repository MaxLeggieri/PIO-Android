package com.livelife.pioalert;

import android.app.Service;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Max on 09/01/2017.
 */

public class PioInstanceIdService extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("PioInstanceIdService", "Refreshed token: " + refreshedToken);

        WebApi.getInstance().tokenHandler(refreshedToken);
    }
}
