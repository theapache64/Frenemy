package com.theah64.frenemy.android.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.theah64.frenemy.android.models.SocketMessage;
import com.theah64.frenemy.android.utils.APIRequestGateway;
import com.theah64.frenemy.android.utils.WebSocketHelper;

import org.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;
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
                    try {
                        WebSocketHelper.getInstance(FCMReceiver.this).getHelper(terminalToken, apiKey).send(new SocketMessage(SocketMessage.WAKEUP_RESPONSE));
                    } catch (IOException | JSONException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(String reason) {

                }
            });
        }
    }

}
