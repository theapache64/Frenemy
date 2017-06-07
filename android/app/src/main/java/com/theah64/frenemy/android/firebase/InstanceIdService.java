package com.theah64.frenemy.android.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.theah64.frenemy.android.asyncs.FCMSynchronizer;
import com.theah64.frenemy.android.utils.APIRequestGateway;


public class InstanceIdService extends FirebaseInstanceIdService {

    private static final String X = InstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        final String newFcmId = FirebaseInstanceId.getInstance().getToken();
        Log.i(X, "Firebase token refreshed : " + newFcmId);
        new APIRequestGateway(this, new APIRequestGateway.APIRequestGatewayCallback() {
            @Override
            public void onReadyToRequest(String apiKey, String frenemyId) {
                new FCMSynchronizer(InstanceIdService.this, apiKey).execute();
            }

            @Override
            public void onFailed(String reason) {

            }
        });
    }


}
