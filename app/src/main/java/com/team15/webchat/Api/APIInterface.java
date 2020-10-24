package com.team15.webchat.Api;

import com.google.gson.JsonObject;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.Login;
import com.team15.webchat.Model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {

//    @GET("home.php")
//    Call<List<Photo>> doGetPhotoList();
//
//    @GET("userPost.php")
//    Call<List<Photo>> getUserPhotoList(@Query("user_id") String user_id);

    @FormUrlEncoded
    @POST("auth/login")
    Call<Login> getUserLogin(
            @Field("email") String phone,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("registration")
    Call<ApiResponse> registration(
            @Field("user_name") String name,
            @Field("phone") String phone,
            @Field("password") String password,
            @Field("app_id") String app_id,
            @Field("email") String email
    );
    @FormUrlEncoded
    @POST("auth/profile")
    Call<User> getUser(
            @Header("Authorization") String token,
            @Field("user_id") String user_id
    );

//    @FormUrlEncoded
//    @POST("updateProfile.php")
//    Call<JsonObject> updateAccount(
//            @Field("user_id") String user_id,
//            @Field("fname") String fname,
//            @Field("lname") String lname
//    );


}
