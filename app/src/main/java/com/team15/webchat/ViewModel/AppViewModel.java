package com.team15.webchat.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.Banner;
import com.team15.webchat.Model.PartialsInfo;
import com.team15.webchat.Model.User;
import com.team15.webchat.Repositories.AppRepository;


import java.util.List;

public class AppViewModel extends ViewModel {
    private AppRepository repository;
    LiveData<List< Banner >> data;
    private LiveData<User> getUser;
    public AppViewModel() {
        super();
        repository = AppRepository.getInstance();

    }

    public LiveData<List<Banner>> getBanner() {
        data = repository.getBanner();
        return data;
    }
    public LiveData<User> getSeller(String token, String userId) {
        getUser = repository.getSeller(token,userId);
        return getUser;
    }
    public LiveData<JsonObject> getPosition(String token, String userId,String seller_id) {
        LiveData<JsonObject> liveData;
        liveData = repository.getPosition(token,userId,seller_id);
        return liveData;
    }
    public LiveData<JsonObject> getSeenCount(String token, String userId,String seller_id) {
        LiveData<JsonObject> liveData;
        liveData = repository.getSeenCount(token,userId,seller_id);
        return liveData;
    }
    public LiveData<PartialsInfo> getPartialsInfo(String token, String userId) {
        LiveData<PartialsInfo> liveData;
        liveData = repository.getPartialsInfo(token,userId);
        return liveData;
    }
    public LiveData<ApiResponse> updateNote(String token, String userId,String note) {
        LiveData<ApiResponse> liveData;
        liveData = repository.updateNote(token,userId,note);
        return liveData;
    }

    public LiveData<ApiResponse> purchase(String token, String userId) {
        LiveData<ApiResponse> liveData;
        liveData = repository.purchase(token, userId);
        return liveData;

    }
        public LiveData<ApiResponse> updatePoint(String token, String userId,String point) {
        LiveData<ApiResponse> liveData;
        liveData = repository.updatePoint(token,userId,point);
        return liveData;
    }

    public void isFavourite(String token ,String user_id,String favourite) {
        repository.isFavourite( token , user_id, favourite);
    }
}
