package com.theah64.frenemy.web.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by theapache64 on 14/9/16,6:07 PM.
 */
public class FCMUtils {

    private static final String FCM_SEND_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String FCM_NOTIFICATION_KEY = "AAAAiHNbpVY:APA91bHMqa2585JSRDXv6Uf93PIMT6fpDD45nz4RxPSmP4Z8d8oeDDLKlerKcuxHBPU3G7lIHAHtk7WKQCVsbfL4NEOL0wgesUSzpifFC_c_umUzA03WdwvC50SuNdl660HsIjgGgHDC";


    public static JSONObject sendWakeUp(String fcmId, final String token) {

        final JSONObject joFcm = new JSONObject();
        try {

            joFcm.put("to", fcmId);

            final JSONObject joData = new JSONObject();
            joData.put("type", "wakeup");
            joData.put("terminal_token", token);
            joFcm.put("data", joData);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sendPayload(joFcm.toString());
    }

    private static JSONObject sendPayload(String payload) {


        try {

            System.out.println("To FCM: " + payload);

            final URL url = new URL(FCM_SEND_URL);
            final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.addRequestProperty("Authorization", "key=" + FCM_NOTIFICATION_KEY);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");

            OutputStream os = urlConnection.getOutputStream();
            os.write(payload.getBytes());
            os.flush();
            os.close();

            final BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            final StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line).append("\n");
            }

            br.close();

            System.out.println("FCM Says : " + response.toString());

            return new JSONObject(response.toString());

        } catch (java.io.IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }

    }


}
