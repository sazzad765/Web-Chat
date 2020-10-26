package com.team15.webchat.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceReg {
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("deviceId")
    @Expose
    private String deviceId;
    @SerializedName("userId")
    @Expose
    private String userId;

    public DeviceReg(String token, String deviceId, String userId) {
        this.token = token;
        this.deviceId = deviceId;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
