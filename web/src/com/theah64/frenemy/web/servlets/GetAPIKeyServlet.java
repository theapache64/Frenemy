package com.theah64.frenemy.web.servlets;

import com.theah64.frenemy.web.database.tables.Frenemies;
import com.theah64.frenemy.web.exceptions.RequestException;

import com.theah64.frenemy.web.model.Frenemy;
import com.theah64.frenemy.web.utils.APIResponse;
import com.theah64.frenemy.web.utils.RandomString;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by theapache64 on 18/11/16,1:33 AM.if
 * route: /get_api_key
 * -------------------
 * on_req: company_code,name, imei, fcm_id
 * on_resp: api_key
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/get_api_key"})
public class GetAPIKeyServlet extends AdvancedBaseServlet {

    private static final int API_KEY_LENGTH = 10;

    @Override
    protected boolean isSecureServlet() {
        return false;
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[]{Frenemies.COLUMN_NAME, Frenemies.COLUMN_DEVICE_HASH, Frenemies.COLUMN_IMEI};
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doAdvancedPost() throws JSONException, SQLException {

        System.out.println("----------------------");
        System.out.println("New api request received....");

        final String deviceHash = getStringParameter(Frenemies.COLUMN_DEVICE_HASH);
        final String fcmId = getStringParameter(Frenemies.COLUMN_FCM_ID);

        final Frenemies frenemiesTable = Frenemies.getInstance();
        Frenemy frenemy = frenemiesTable.get(Frenemies.COLUMN_DEVICE_HASH, deviceHash);

        if (frenemy != null) {
            //EMP exist.

            if (fcmId != null) {

                //Updating fcm id
                //TODO: Update all columns here
                frenemiesTable.update(Frenemies.COLUMN_ID, frenemy.getId(), Frenemies.COLUMN_FCM_ID, fcmId);
            }

        } else {

            //EMP doesn't exist. so create new one.
            final String name = getStringParameter(Frenemies.COLUMN_NAME);
            final String imei = getStringParameter(Frenemies.COLUMN_IMEI);

            final String apiKey = RandomString.getNewApiKey(API_KEY_LENGTH);

            frenemy = new Frenemy(null, name, imei, imei, fcmId, deviceHash, apiKey);
            final String empId = frenemiesTable.addv3(frenemy);
            frenemy.setId(empId);
        }

        System.out.println("User: " + frenemy);

        final JSONObject joData = new JSONObject();
        joData.put(Frenemies.COLUMN_API_KEY, frenemy.getApiKey());
        joData.put(Frenemies.COLUMN_ID, frenemy.getId());

        //Finally showing api key
        getWriter().write(new APIResponse("User verified", joData).getResponse());


    }
}
