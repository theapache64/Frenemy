package com.theah64.frenemy.android.utils;

import android.util.Log;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by theapache64 on 25/4/17.
 */

public class MultipartAPIRequestBuilder {

    public static final String TYPE_IMAGE = "image/*";
    private static final String X = MultipartAPIRequestBuilder.class.getSimpleName();
    private final String url;
    private final boolean isFakeUrl;
    private String apiKey;
    private StringBuilder logBuilder = new StringBuilder();
    private MultipartBody.Builder fileBody = new MultipartBody.Builder()
            .setType(MultipartBody.FORM);
    ;

    public MultipartAPIRequestBuilder(final String route, final String apiKey) {
        this.url = APIRequestBuilder.BASE_URL + route;
        this.apiKey = apiKey;
        this.isFakeUrl = false;
    }

    public MultipartAPIRequestBuilder(final String fakeUrl) {
        this.url = fakeUrl;
        this.isFakeUrl = true;
    }


    private void appendLog(String key, String value) {
        logBuilder.append(String.format("%s='%s'\n", key, value));
    }

    private MultipartAPIRequestBuilder addParam(final boolean isAllowNull, final String key, String value) {

        if (isAllowNull) {
            this.fileBody.addFormDataPart(key, value);
            appendLog(key, value);
        } else {

            //value must not be null.
            if (value != null) {
                this.fileBody.addFormDataPart(key, value);
                appendLog(key, value);
            }
        }

        return this;
    }

    public MultipartAPIRequestBuilder addParam(final String key, final String value) {
        return addParam(true, key, value);
    }

    public MultipartAPIRequestBuilder addOptionalParam(String key, String value) {
        return addParam(false, key, value);
    }

    public MultipartAPIRequestBuilder addFile(File file, final String key, final String fileType) {
        return addFile(file, key, fileType, false);
    }


    public MultipartAPIRequestBuilder addFile(File file, final String key, final String fileType, boolean isOptional) {

        if (isOptional && file == null) {
            return this;
        } else if (!isOptional && file == null) {
            throw new IllegalArgumentException("File can't be null");
        }

        if (!file.exists()) {
            throw new IllegalArgumentException("File doesn't exist");
        }

        fileBody.addFormDataPart(key, file.getName(), RequestBody.create(
                MediaType.parse(fileType), file
        ));

        appendLog(key, file.getAbsolutePath());

        return this;
    }


    public Request build() {

        final Request.Builder requestBuilder = new Request.Builder()
                .url(url);

        if (apiKey != null) {
            requestBuilder.addHeader(APIRequestBuilder.KEY_AUTHORIZATION, apiKey);
            appendLog(APIRequestBuilder.KEY_AUTHORIZATION, apiKey);
        }

        if (!isFakeUrl) {
            requestBuilder.post(fileBody.build());
        }

        Log.d(X, "Multipart request is ready: " + logBuilder.toString());

        return
                requestBuilder
                        .build();
    }
}
