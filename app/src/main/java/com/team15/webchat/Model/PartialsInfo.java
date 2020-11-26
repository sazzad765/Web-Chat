package com.team15.webchat.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PartialsInfo {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("purchase")
    @Expose
    private Integer purchase;
    @SerializedName("point")
    @Expose
    private Integer point;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("favorite")
    @Expose
    private Integer favorite;
    @SerializedName("ref")
    @Expose
    private Integer ref;
    @SerializedName("referral_point")
    @Expose
    private Integer referralPoint;
    @SerializedName("pending_point")
    @Expose
    private Integer pendingPoint;
    @SerializedName("set_point")
    @Expose
    private Integer setPoint;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPurchase() {
        return purchase;
    }

    public void setPurchase(Integer purchase) {
        this.purchase = purchase;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getFavorite() {
        return favorite;
    }

    public void setFavorite(Integer favorite) {
        this.favorite = favorite;
    }

    public Integer getRef() {
        return ref;
    }

    public void setRef(Integer ref) {
        this.ref = ref;
    }

    public Integer getReferralPoint() {
        return referralPoint;
    }

    public void setReferralPoint(Integer referralPoint) {
        this.referralPoint = referralPoint;
    }

    public Integer getPendingPoint() {
        return pendingPoint;
    }

    public void setPendingPoint(Integer pendingPoint) {
        this.pendingPoint = pendingPoint;
    }

    public Integer getSetPoint() {
        return setPoint;
    }

    public void setSetPoint(Integer setPoint) {
        this.setPoint = setPoint;
    }

}
