package com.theah64.frenemy.android.utils;

import android.util.Log;

import okhttp3.FormBody;
import okhttp3.Request;

/**
 * Created by shifar on 29/7/16.
 * Utility class to create API request object.
 */
public class APIRequestBuilder {


    public static final String BASE_URL = App.IS_DEBUG_MODE ? "http://192.168.43.141:8080/frenemy/v1" : "http://theapache64.xyz:8080/frenemy/v1";
    public static final String KEY_AUTHORIZATION = "Authorization";
    private static final String X = APIRequestBuilder.class.getSimpleName();
    private final Request.Builder requestBuilder = new Request.Builder();
    private final StringBuilder logBuilder = new StringBuilder();

    private final String url;
    private FormBody.Builder params = new FormBody.Builder();

    public APIRequestBuilder(String route, final String apiKey) {

        this.url = BASE_URL + route;
        appendLog("URL", url);

        if (apiKey != null) {
            requestBuilder.addHeader(KEY_AUTHORIZATION, apiKey);
            appendLog(KEY_AUTHORIZATION, apiKey);
        }
    }

    APIRequestBuilder(String route) {
        this(route, null);
    }


    private void appendLog(String key, String value) {
        logBuilder.append(String.format("%s='%s'\n", key, value));
    }

    private APIRequestBuilder addParam(final boolean isAllowNull, final String key, String value) {


        if (isAllowNull) {
            this.params.add(key, value);
            appendLog(key, value);
        } else {

            //value must not be null.
            if (value != null) {
                this.params.add(key, value);
                appendLog(key, value);
            }
        }


        return this;
    }

    public APIRequestBuilder addParam(final String key, final String value) {
        return addParam(true, key, value);
    }


    /**
     * Used to build the OkHttpRequest.
     */
    public Request build() {

        requestBuilder
                .post(params.build())
                .url(url);

        Log.d(X, "Request : " + logBuilder.toString());

        return requestBuilder.build();
    }

    APIRequestBuilder addParamIfNotNull(String key, String value) {
        return addParam(false, key, value);
    }
}
