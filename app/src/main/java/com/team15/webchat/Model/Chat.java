package com.team15.webchat.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Chat implements Serializable {
    @SerializedName("reciver_id")
    @Expose
    private String reciverId;
    @SerializedName("sender_id")
    @Expose
    private String senderId;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("app_id")
    @Expose
    private Integer appId;
    @SerializedName("seen")
    @Expose
    private Integer seen;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("type")
    @Expose
    private String type;

    public Chat(String reciverId, String senderId, String message, Integer appId, Integer seen, String createdAt, String type) {
        this.reciverId = reciverId;
        this.senderId = senderId;
        this.message = message;
        this.appId = appId;
        this.seen = seen;
        this.createdAt = createdAt;
        this.type = type;
    }

    public Chat() {
    }

    public String getReciverId() {
        return reciverId;
    }

    public void setReciverId(String reciverId) {
        this.reciverId = reciverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getSeen() {
        return seen;
    }

    public void setSeen(Integer seen) {
        this.seen = seen;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
