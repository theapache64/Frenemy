package com.theah64.frenemy.web.servlets;


import com.theah64.frenemy.web.database.tables.Frenemies;
import com.theah64.frenemy.web.exceptions.RequestException;

import com.theah64.frenemy.web.model.Frenemy;
import com.theah64.frenemy.web.utils.Response;
import org.json.JSONException;

import javax.servlet.annotation.WebServlet;
import java.sql.SQLException;

/**
 * Created by theapache64 on 19/11/16,3:11 PM.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/update_fcm"})
public class UpdateFCMServlet extends AdvancedBaseServlet {

    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[]{Frenemies.COLUMN_FCM_ID};
    }

    @Override
    protected void doAdvancedPost() throws RequestException, JSONException, SQLException {
        final String frenemyId = getHeaderSecurity().getFrenemyId();
        final String fcmId = getStringParameter(Frenemies.COLUMN_FCM_ID);
        Frenemies.getInstance().update(Frenemies.COLUMN_ID, frenemyId, Frenemies.COLUMN_FCM_ID, fcmId);
        getWriter().write(new Response("FCM id updated", null).getResponse());
    }
}
