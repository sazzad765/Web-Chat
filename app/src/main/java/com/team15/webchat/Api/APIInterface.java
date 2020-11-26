package com.team15.webchat.Api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team15.webchat.Model.ActiveUser;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.Banner;
import com.team15.webchat.Model.ChatListPaging;
import com.team15.webchat.Model.ChatPag;
import com.team15.webchat.Model.Login;
import com.team15.webchat.Model.PartialsInfo;
import com.team15.webchat.Model.ProductList;
import com.team15.webchat.Model.PurchaseList;
import com.team15.webchat.Model.ReferralPointList;
import com.team15.webchat.Model.SellerList;
import com.team15.webchat.Model.ShortProfile;
import com.team15.webchat.Model.User;
import com.team15.webchat.Model.WaitingList;

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
    @POST("auth/short_profile")
    Call<ShortProfile> shortProfile(
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
    @POST("auth/referral")
    Call<ApiResponse> updateRef(
            @Header("Authorization") String token,
            @Field("user_id") String user_id,
            @Field("referral_id") String referral_id
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
            @Field("seller_id") String user_id,
            @Field("app_id") String app_id,
            @Field("page") String currentPage
    );

    @FormUrlEncoded
    @POST("auth/waiting_list")
    Call<List<WaitingList>> getWaitingList(
            @Header("Authorization") String token,
            @Field("app_id") String app_id
    );

    @FormUrlEncoded
    @POST("auth/start_chat")
    Call<JsonObject> startChat(
            @Header("Authorization") String token,
            @Field("app_id") String app_id,
            @Field("agent_id") String agent_id,
            @Field("user_id") String user_id,
            @Field("seller_id") String seller_id

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
            @Field("app_id") String app_id,
            @Field("page") String page
    );

    @FormUrlEncoded
    @POST("auth/send_messages")
    Call<JsonObject> sendMessage(
            @Header("Authorization") String token,
            @Field("sender_id") String sender_id,
            @Field("reciver_id") String receiver_id,
            @Field("app_id") String app_id,
            @Field("message") String message,
            @Field("type") String type,
            @Field("user_type") String user_type

    );

    @FormUrlEncoded
    @POST("banner")
    Call<List<Banner>> getBanner(
            @Field("app_id") String app_id
    );

    @FormUrlEncoded
    @POST("auth/product_list")
    Call<ProductList.ProductListPaging> getProduct(
            @Header("Authorization") String token,
            @Field("app_id") String app_id,
            @Field("page") String page

    );

    @FormUrlEncoded
    @POST("auth/get_seller_sells")
    Call<PurchaseList.PurchaseListPaging> getSellerSell(
            @Header("Authorization") String token,
            @Field("type") String type,
            @Field("app_id") String app_id,
            @Field("page") String page

    );

    @FormUrlEncoded
    @POST("auth/get_user_sells")
    Call<PurchaseList.PurchaseListPaging> getUserPurchase(
            @Header("Authorization") String token,
            @Field("user_id") String user_id,
            @Field("app_id") String app_id,
            @Field("page") String page

    );

    @FormUrlEncoded
    @POST("auth/referral_point_list")
    Call<ReferralPointList.ReferralPointPaging> getReferralPointList(
            @Header("Authorization") String token,
            @Field("user_id") String user_id,
            @Field("page") String page

    );

    @FormUrlEncoded
    @POST("auth/online_status")
    Call<JsonObject> isOnline(
            @Header("Authorization") String token,
            @Field("user_id") String user_id,
            @Field("status") String status
    );

    @FormUrlEncoded
    @POST("auth/unseen")
    Call<JsonObject> isSeen(
            @Header("Authorization") String token,
            @Field("user_id") String user_id,
            @Field("sender_id") String sender_id,
            @Field("app_id") String app_id
    );

    @FormUrlEncoded
    @POST("auth/waiting_position")
    Call<JsonObject> waitingTime(
            @Header("Authorization") String token,
            @Field("user_id") String user_id,
            @Field("app_id") String app_id
    );

    @FormUrlEncoded
    @POST("auth/seller_list")
    Call<List<SellerList>> getSellerList(
            @Header("Authorization") String token,
            @Field("app_id") String app_id
    );

    @FormUrlEncoded
    @POST("auth/user_inbox")
    Call<JsonObject> getSeenCount(
            @Header("Authorization") String token,
            @Field("user_id") String user_id,
            @Field("seller_id") String seller_id,
            @Field("app_id") String app_id
    );

    @FormUrlEncoded
    @POST("auth/seller_contact")
    Call<JsonObject> getSellerContact(
            @Header("Authorization") String token,
            @Field("app_id") String app_id
    );

    @Multipart
    @POST("auth/profile_image_update")
    Call<ApiResponse> uploadProfileImg(
            @Header("Authorization") String token,
            @Part MultipartBody.Part file,
            @Part("image") RequestBody name,
            @Part("user_id") RequestBody user_id
    );

    @Multipart
    @POST("auth/send_messages")
    Call<ApiResponse> chatImage(
            @Header("Authorization") String token,
            @Part MultipartBody.Part file,
            @Part("image") RequestBody name,
            @Part("sender_id") RequestBody sender_id,
            @Part("reciver_id") RequestBody receiver_id,
            @Part("app_id") RequestBody app_id,
            @Part("type") RequestBody type,
            @Part("user_type") RequestBody user_type
    );

    @FormUrlEncoded
    @POST("auth/partials-info")
    Call<PartialsInfo> getPartialsInfo(
            @Header("Authorization") String token,
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("auth/favorite")
    Call<JsonObject> isFavourite(
            @Header("Authorization") String token,
            @Field("user_id") String user_id,
            @Field("favorite") String favorite
    );

    @FormUrlEncoded
    @POST("auth/note")
    Call<ApiResponse> updateNote(
            @Header("Authorization") String token,
            @Field("user_id") String user_id,
            @Field("note") String note
    );

    @FormUrlEncoded
    @POST("auth/seller_transfer")
    Call<ApiResponse> transferUser(
            @Header("Authorization") String token,
            @Field("app_id") String appId,
            @Field("user_id") String user_id,
            @Field("seller_id") String seller_id
    );

    @FormUrlEncoded
    @POST("auth/sell_cancel")
    Call<ApiResponse> cancelPurchase(
            @Header("Authorization") String token,
            @Field("app_id") String app_id,
            @Field("sell_id") String sell_id
    );

    @FormUrlEncoded
    @POST("auth/sell_approve")
    Call<ApiResponse> acceptPurchase(
            @Header("Authorization") String token,
            @Field("app_id") String app_id,
            @Field("sell_id") String sell_id
    );

    @FormUrlEncoded
    @POST("auth/sell")
    Call<ApiResponse> sellRequest(
            @Header("Authorization") String token,
            @Field("user_id") String user_id,
            @Field("product_id") String product_id,
            @Field("game_id") String game_id,
            @Field("app_id") String app_id
    );

    @FormUrlEncoded
    @POST("auth/purchase")
    Call<ApiResponse> purchase(
            @Header("Authorization") String token,
            @Field("user_id") String user_id,
            @Field("seller_id") String seller_id

    );

    @FormUrlEncoded
    @POST("auth/point")
    Call<ApiResponse> updatePoint(
            @Header("Authorization") String token,
            @Field("user_id") String user_id,
            @Field("point") String point
    );
    @FormUrlEncoded
    @POST("auth/set_point")
    Call<ApiResponse> purchasePoint(
            @Header("Authorization") String token,
            @Field("user_id") String user_id,
            @Field("point") String point
    );

    @FormUrlEncoded
    @POST("auth/password_change")
    Call<ApiResponse> password_change(
            @Header("Authorization") String token,
            @Field("user_id") String user_id,
            @Field("old_password") String old_password,
            @Field("new_password") String new_password
    );
}
