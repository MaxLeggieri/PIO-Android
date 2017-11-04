package com.livelife.pioalert;

import android.util.Log;
import android.widget.Toast;

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
        if (!Utility.isNetworkConnected(this)){
            Toast.makeText(this,getResources().getString(R.string.internet_check_text),Toast.LENGTH_SHORT).show();
            return ;
        }
        WebApi.getInstance().tokenHandler(refreshedToken);
    }
}
