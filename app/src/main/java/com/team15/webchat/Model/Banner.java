package com.team15.webchat.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Banner {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("slider")
    @Expose
    private String slider;
    @SerializedName("app_id")
    @Expose
    private Integer appId;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSlider() {
        return slider;
    }

}
