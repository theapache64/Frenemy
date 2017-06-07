package com.theah64.frenemy.web.database.tables;

import com.theah64.frenemy.web.database.Connection;
import com.theah64.frenemy.web.model.Frenemy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by theapache64 on 7/6/17.
 */
public class Frenemies extends BaseTable<Frenemy> {
    public static final String COLUMN_DEVICE_HASH = "device_hash";
    public static final String COLUMN_API_KEY = "api_key";
    public static final String COLUMN_IMEI = "imei";
    public static final String COLUMN_FCM_ID = "fcm_id";

    private Frenemies() {
        super("frenemies");
    }

    private static final Frenemies instance = new Frenemies();

    public static Frenemies getInstance() {
        return instance;
    }

    @Override
    public Frenemy get(String column, String value) {
        Frenemy frenemy = null;
        final String query = String.format("SELECT id,name,fcm_id, api_key FROM frenemies WHERE %s = ? AND is_active = 1 LIMIT 1;", column);
        final String query2 = String.format("SELECT id,name,fcm_id, api_key FROM frenemies WHERE %s = '%s' AND is_active = 1 LIMIT 1;", column, value);
        System.out.println(query2);
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, value);
            final ResultSet rs = ps.executeQuery();

            if (rs.first()) {
                final String id = rs.getString(COLUMN_ID);
                final String name = rs.getString(COLUMN_NAME);
                final String fcmId = rs.getString(COLUMN_FCM_ID);
                final String apiKey = rs.getString(COLUMN_API_KEY);
                frenemy = new Frenemy(id, name, null, null, fcmId, null, apiKey);
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return frenemy;
    }

    @Override
    public String addv3(Frenemy user) throws SQLException {
        String userId = null;
        String error = null;
        final String query = "INSERT INTO frenemies (name,imei,device_hash,fcm_id,api_key) VALUES (?,?,?,?,?);";
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getImei());
            ps.setString(3, user.getDeviceHash());
            ps.setString(4, user.getFcmId());
            ps.setString(5, user.getApiKey());
            ps.executeUpdate();
            final ResultSet rs = ps.getGeneratedKeys();
            if (rs.first()) {
                userId = rs.getString(1);
            } else {
                error = "Failed to add frenemy";
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
            error = e.getMessage();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        manageError(error);
        return userId;
    }
}
