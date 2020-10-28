package com.team15.webchat.Api;

import com.google.gson.JsonObject;
import com.team15.webchat.Model.ActiveUser;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.Banner;
import com.team15.webchat.Model.ChatListPaging;
import com.team15.webchat.Model.ChatPag;
import com.team15.webchat.Model.Login;
import com.team15.webchat.Model.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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
            @Field("password") String password,
            @Field("app_id") String app_id
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

    @FormUrlEncoded
    @POST("auth/profile_update")
    Call<ApiResponse> updateProfile(
            @Header("Authorization") String token,
            @Field("user_id") String user_id,
            @Field("name") String name,
            @Field("phone") String phone,
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("auth/update_device_id")
    Call<ApiResponse> updateToken(
            @Header("Authorization") String token,
            @Field("reg_id") String deviceId,
            @Field("user_id") String user_id

    );

    @FormUrlEncoded
    @POST("auth/chat_list")
    Call<ChatListPaging> chatList(
            @Header("Authorization") String token,
            @Field("user_id") String user_id,
            @Field("app_id") String app_id,
            @Field("page") String currentPage
    );

    @FormUrlEncoded
    @POST("auth/active_users")
    Call<ActiveUser> activeUser(
            @Header("Authorization") String token,
            @Field("app_id") String app_id,
            @Field("page") String currentPage
    );

    @FormUrlEncoded
    @POST("auth/get_messages")
    Call<ChatPag> messageData(
            @Header("Authorization") String token,
            @Field("sender_id") String sender_id,
            @Field("receiver_id") String receiver_id,
            @Field("app_id") String app_id
    );
    @FormUrlEncoded
    @POST("auth/send_messages")
    Call<JsonObject> sendMessage(
            @Header("Authorization") String token,
            @Field("sender_id") String sender_id,
            @Field("reciver_id") String receiver_id,
            @Field("app_id") String app_id,
            @Field("message") String message,
            @Field("type") String type

    );

    @FormUrlEncoded
    @POST("banner")
    Call<List<Banner>> getBanner(
            @Field("app_id") String app_id
    );

    @FormUrlEncoded
    @POST("auth/online_status")
    Call<JsonObject> isOnline(
            @Header("Authorization") String token,
            @Field("user_id") String user_id,
            @Field("status") String status
    );


    @Multipart
    @POST("retrofit_example/upload_image.php")
    Call uploadFile(@Part MultipartBody.Part file,
                    @Part("file") RequestBody name,
                    @Field("user_id") String user_id
    );



}
