package com.theah64.frenemy.web.model;

/**
 * Created by theapache64 on 7/6/17.
 */
public class Frenemy {
    private String id;
    private final String name;
    private final String email;
    private final String imei;
    private final String fcmId;
    private final String deviceHash;
    private final String apiKey;

    public Frenemy(String id, String name, String email, String imei, String fcmId, String deviceHash, String apiKey) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.imei = imei;
        this.fcmId = fcmId;
        this.deviceHash = deviceHash;
        this.apiKey = apiKey;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getImei() {
        return imei;
    }

    public String getFcmId() {
        return fcmId;
    }

    public String getDeviceHash() {
        return deviceHash;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setId(String id) {
        this.id = id;
    }
}
