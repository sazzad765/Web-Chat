package com.team15.webchat.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Login {
    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("loginInfo")
    @Expose
    private List<LoginInfo> loginInfo = null;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<LoginInfo> getLoginInfo() {
        return loginInfo;
    }

    public void setLoginInfo(List<LoginInfo> loginInfo) {
        this.loginInfo = loginInfo;
    }





//    @SerializedName("success")
//    @Expose
//    private Integer success;
//    @SerializedName("message")
//    @Expose
//    private String message;
//    @SerializedName("data")
//    @Expose
//    private DataList data;
//
//    public Login(Integer success, String message, DataList data) {
//        this.success = success;
//        this.message = message;
//        this.data = data;
//    }
//
//    public Integer getSuccess() {
//        return success;
//    }
//
//    public void setSuccess(Integer success) {
//        this.success = success;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    public DataList getData() {
//        return data;
//    }
//
//    public void setData(DataList data) {
//        this.data = data;
//    }


}
