package com.theah64.frenemy.android.models;


import com.theah64.frenemy.android.utils.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by theapache64 on 17/1/17.
 */

public class SocketMessage {

    public static final String TYPE_MESSAGE = "message";
    public static final String WAKEUP_RESPONSE = "Hey, wassup?!";
    private static final String KEY_TYPE = "type";
    private final JSONObject joSocketMessage;

    public SocketMessage(String message, boolean isError, String type) throws JSONException {


        joSocketMessage = new JSONObject();
        joSocketMessage.put(Response.KEY_MESSAGE, message);
        joSocketMessage.put(Response.KEY_ERROR, isError);
        joSocketMessage.put("is_wakeup", message.equals(WAKEUP_RESPONSE));

        final JSONObject joData = new JSONObject();

        if (!isError) {
            joData.put(KEY_TYPE, type);
        }

        joSocketMessage.put(Response.KEY_DATA, joData);

    }

    public SocketMessage(final String message) throws JSONException {
        this(message, false, TYPE_MESSAGE);
    }


    public SocketMessage(final String message, boolean isError) throws JSONException {
        this(message, isError, TYPE_MESSAGE);
    }

    @Override
    public String toString() {
        return joSocketMessage.toString();
    }
}
