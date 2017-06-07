package com.theah64.frenemy.android.models;

/**
 * Created by theapache64 on 7/6/17.
 */

public class Frenemy {

    public static final String KEY_FCM_ID = "fcm_id";
    public static final String KEY_IS_FCM_SYNCED = "is_fcm_synced";
    private final String apiKey;

    public Frenemy(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }
}
