package com.theah64.frenemy.web.servlets;


import com.theah64.frenemy.web.database.tables.Frenemies;
import com.theah64.frenemy.web.exceptions.RequestException;
import com.theah64.frenemy.web.utils.APIResponse;
import com.theah64.frenemy.web.utils.FCMUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;
import java.io.IOException;

/**
 * Created by theapache64 on 19/11/16,3:21 PM.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/wakeup"})
public class WakeUpServlet extends AdvancedBaseServlet {

    private static final String KEY_TOKEN = "token";

    @Override
    protected boolean isSecureServlet() {
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[]{
                Frenemies.COLUMN_ID,
                KEY_TOKEN
        };
    }

    @Override
    protected void doAdvancedPost() throws JSONException, RequestException {

        final String frenemyId = getStringParameter(Frenemies.COLUMN_ID);
        final String fcmId = Frenemies.getInstance().get(Frenemies.COLUMN_ID, frenemyId, Frenemies.COLUMN_FCM_ID, true);
        final String token = getStringParameter(KEY_TOKEN);

        if (fcmId != null) {

            //Alright
            final JSONObject joFcmResp = FCMUtils.sendWakeUp(fcmId, token);

            if (joFcmResp != null) {

                final boolean isEverythingOk = joFcmResp.getInt("failure") == 0;

                if (!isEverythingOk) {
                    System.out.println("FCM FAILED: " + joFcmResp);
                    getWriter().write(new APIResponse("Failed to fire fcm").getResponse());
                } else {
                    getWriter().write(new APIResponse("Wakeup request sent", null).getResponse());
                }


            } else {
                throw new RequestException("Failed to send Wakeup request");
            }

        } else {
            throw new RequestException("No frenemy found");
        }

    }
}
