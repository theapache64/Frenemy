package com.theah64.frenemy.android.asyncs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.theah64.frenemy.android.models.Frenemy;
import com.theah64.frenemy.android.utils.APIRequestBuilder;
import com.theah64.frenemy.android.utils.APIRequestGateway;
import com.theah64.frenemy.android.utils.OkHttpUtils;
import com.theah64.frenemy.android.utils.PrefUtils;
import com.theah64.frenemy.android.utils.Response;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;

/**
 * Created by theapache64 on 28/9/16.
 */

public class FCMSynchronizer extends BaseJSONPostNetworkAsyncTask<Void> {

    private static final String X = FCMSynchronizer.class.getSimpleName();
    private final String newFcmId;
    private final boolean isFCMSynced;

    public FCMSynchronizer(Context context, String apiKey) {
        super(context, apiKey);
        final PrefUtils prefUtils = PrefUtils.getInstance(context);
        this.newFcmId = prefUtils.getString(Frenemy.KEY_FCM_ID);
        this.isFCMSynced = prefUtils.getBoolean(Frenemy.KEY_IS_FCM_SYNCED);

        Log.d(X, "Started");
    }

    @Override
    protected synchronized Void doInBackground(String... strings) {

        if (newFcmId != null && !isFCMSynced) {

            Log.d(X, "Updating...");

            new APIRequestGateway(getContext(), new APIRequestGateway.APIRequestGatewayCallback() {
                @Override
                public void onReadyToRequest(String apiKey, final String frenemyId) {

                    final Request fcmUpdateRequest = new APIRequestBuilder("/update_fcm", apiKey)
                            .addParam(Frenemy.KEY_FCM_ID, newFcmId)
                            .build();

                    OkHttpUtils.getInstance().getClient().newCall(fcmUpdateRequest).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, @NonNull okhttp3.Response response) throws IOException {
                            try {
                                new Response(OkHttpUtils.logAndGetStringBody(response));
                                PrefUtils.getInstance(getContext()).getEditor()
                                        .putBoolean(Frenemy.KEY_IS_FCM_SYNCED, true)
                                        .commit();

                                Log.d(X, "FCM Synced");

                            } catch (JSONException | Response.ResponseException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }

                @Override
                public void onFailed(String reason) {
                    Log.e(X, "Failed to update fcm : " + reason);
                }
            });
        }

        return null;
    }
}
