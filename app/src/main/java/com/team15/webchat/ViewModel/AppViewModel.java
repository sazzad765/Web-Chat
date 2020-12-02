package com.team15.webchat.ViewModel;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.Banner;
import com.team15.webchat.Model.PartialsInfo;
import com.team15.webchat.Model.ProductList;
import com.team15.webchat.Model.PurchaseList;
import com.team15.webchat.Model.ReferralPointList;
import com.team15.webchat.Model.SellerList;
import com.team15.webchat.Model.User;
import com.team15.webchat.R;
import com.team15.webchat.Repositories.AppRepository;


import java.util.List;

public class AppViewModel extends ViewModel {
    private AppRepository repository;
    LiveData<List<Banner>> data;

    LiveData<ProductList.ProductListPaging>productListLiveData;
    LiveData<PurchaseList.PurchaseListPaging>purchaseListPagingLiveData;
    LiveData<ReferralPointList.ReferralPointPaging>referralPointPagingLiveData;

    public AppViewModel() {
        super();
        repository = AppRepository.getInstance();

    }

    public LiveData<List<Banner>> getBanner() {
        data = repository.getBanner();
        return data;
    }
    public LiveData<ProductList.ProductListPaging> getProduct(String token,String page) {
        productListLiveData = repository.getProduct(token,page);
        return productListLiveData;
    }

    public LiveData<PurchaseList.PurchaseListPaging> getSellerSell(String token,String type,String page) {
        purchaseListPagingLiveData = repository.getSellerSell(token,type,page);
        return purchaseListPagingLiveData;
    }
    public LiveData<PurchaseList.PurchaseListPaging> getUserPurchase(String token,String userId,String page) {
        purchaseListPagingLiveData = repository.getUserPurchase(token,userId,page);
        return purchaseListPagingLiveData;
    }

    public LiveData<ReferralPointList.ReferralPointPaging> getReferralPointList(String token, String userId, String page) {
        referralPointPagingLiveData = repository.getReferralPointList(token,userId,page);
        return referralPointPagingLiveData;
    }

    public LiveData<User> getSeller(String token, String userId) {
        LiveData<User> getUser;
        getUser = repository.getSeller(token, userId);
        return getUser;
    }

    public LiveData<JsonObject> getPosition(String token, String userId) {
        LiveData<JsonObject> liveData;
        liveData = repository.getPosition(token, userId);
        return liveData;
    }

    public LiveData<List<SellerList>> getSellerList(String token) {
        LiveData<List<SellerList>> liveData;
        liveData = repository.getSellerList(token);
        return liveData;
    }

    public LiveData<JsonObject> getSeenCount(String token, String userId, String seller_id) {
        LiveData<JsonObject> liveData;
        liveData = repository.getSeenCount(token, userId, seller_id);
        return liveData;
    }

    public LiveData<JsonObject> getSellerContact(String token) {
        LiveData<JsonObject> liveData;
        liveData = repository.getSellerContact(token);
        return liveData;
    }

    public LiveData<PartialsInfo> getPartialsInfo(String token, String userId) {
        LiveData<PartialsInfo> liveData;
        liveData = repository.getPartialsInfo(token, userId);
        return liveData;
    }

    public LiveData<ApiResponse> updateNote(String token, String userId, String note) {
        LiveData<ApiResponse> liveData;
        liveData = repository.updateNote(token, userId, note);
        return liveData;
    }

    public LiveData<ApiResponse> transferUser(String token, String userId, String seller_id) {
        LiveData<ApiResponse> liveData;
        liveData = repository.transferUser(token, userId, seller_id);
        return liveData;
    }

    public LiveData<ApiResponse> cancelPurchase(String token, String sellId ) {
        LiveData<ApiResponse> liveData;
        liveData = repository.cancelPurchase(token, sellId);
        return liveData;
    }
    public LiveData<ApiResponse> acceptPurchase(String token, String sellId ,String id) {
        LiveData<ApiResponse> liveData;
        liveData = repository.acceptPurchase(token, sellId,id);
        return liveData;
    }

    public LiveData<ApiResponse> sellRequest(String token, String userId, String product_id,String game_id) {
        LiveData<ApiResponse> liveData;
        liveData = repository.sellRequest(token, userId, product_id,game_id);
        return liveData;
    }

    public LiveData<ApiResponse> purchase(String token, String userId,String sellerId) {
        LiveData<ApiResponse> liveData;
        liveData = repository.purchase(token, userId,sellerId);
        return liveData;

    }

    public LiveData<ApiResponse> updatePoint(String token, String userId, String point) {
        LiveData<ApiResponse> liveData;
        liveData = repository.updatePoint(token, userId, point);
        return liveData;
    }
    public LiveData<ApiResponse> purchasePoint(String token, String userId, String point) {
        LiveData<ApiResponse> liveData;
        liveData = repository.purchasePoint(token, userId, point);
        return liveData;
    }

    public LiveData<ApiResponse> password_change(String token, String userId,String old_password,String new_password) {
        LiveData<ApiResponse> liveData;
        liveData = repository.password_change(token, userId, old_password,new_password);
        return liveData;
    }

    public void isFavourite(String token, String user_id, String favourite) {
        repository.isFavourite(token, user_id, favourite);
    }

}
