package com.theah64.frenemy.android.models;


import com.theah64.frenemy.android.utils.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by theapache64 on 17/1/17.
 */

public class SocketMessage {

    public static final String TYPE_MESSAGE = "message";
    public static final String WAKEUP_RESPONSE = "Hey, Wassupp!!!";
    private static final String KEY_TYPE = "type";
    private JSONObject joSocketMessage;

    public SocketMessage(String message, boolean isError, String type, boolean isFinished, boolean isHTML) {

        try {


            joSocketMessage = new JSONObject();
            joSocketMessage.put(Response.KEY_MESSAGE, message);
            joSocketMessage.put(Response.KEY_ERROR, isError);
            joSocketMessage.put("is_wakeup", message.startsWith(WAKEUP_RESPONSE));
            joSocketMessage.put("is_finished", isFinished);
            joSocketMessage.put("is_html", isHTML);

            final JSONObject joData = new JSONObject();

            if (!isError) {
                joData.put(KEY_TYPE, type);
            }

            joSocketMessage.put(Response.KEY_DATA, joData);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public SocketMessage(final String message, boolean isFinished, boolean isHTML) {
        this(message, false, TYPE_MESSAGE, isFinished, isHTML);
    }


    public SocketMessage(final String message, boolean isError, boolean isFinished, boolean isHTML) {
        this(message, isError, TYPE_MESSAGE, isFinished, isHTML);
    }

    @Override
    public String toString() {
        return joSocketMessage.toString();
    }
}
