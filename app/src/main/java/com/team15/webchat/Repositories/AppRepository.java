package com.team15.webchat.Repositories;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.team15.webchat.Api.APIClient;
import com.team15.webchat.Api.APIInterface;
import com.team15.webchat.App.Config;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.Banner;
import com.team15.webchat.Model.PartialsInfo;
import com.team15.webchat.Model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppRepository extends Observable {
    private APIInterface apiInterface;
    private static AppRepository appRepository;
    final MutableLiveData<User> userProfile = new MutableLiveData<>();
    MutableLiveData<List<Banner>> bannerData = new MutableLiveData<>();


    public static AppRepository getInstance() {
        if (appRepository == null) {
            appRepository = new AppRepository();
        }
        return appRepository;
    }

    private AppRepository() {
        apiInterface = APIClient.getRetrofitInstance().create(APIInterface.class);
    }

    public void isFavourite(String token, String user_id, String favourite) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(token);
        list.add(user_id);
        list.add(favourite);

        new AppRepository.IsFavourite(apiInterface).execute(list);
    }


    public LiveData<List<Banner>> getBanner() {

        Call<List<Banner>> call2 = apiInterface.getBanner(Config.APP_ID);
        call2.enqueue(new Callback<List<Banner>>() {
            @Override
            public void onResponse(Call<List<Banner>> call, Response<List<Banner>> response) {
                bannerData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Banner>> call, Throwable t) {

            }
        });
        return bannerData;
    }

    public LiveData<User> getSeller(String token, String userId) {

        Call<User> call2 = apiInterface.getUser(token, userId);
        call2.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                userProfile.postValue(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                call.cancel();
            }
        });
        return userProfile;
    }

    public LiveData<ApiResponse> updateNote(String token, String userId, String note) {
        final MutableLiveData<ApiResponse> data= new MutableLiveData<>();
        Call<ApiResponse> call2 = apiInterface.updateNote(token, userId, note);
        call2.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                data.postValue(response.body());
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                call.cancel();
            }
        });
        return data;
    }

    public LiveData<ApiResponse> purchase(String token, String userId) {
        final MutableLiveData<ApiResponse> data= new MutableLiveData<>();
        Call<ApiResponse> call2 = apiInterface.purchase(token, userId);
        call2.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                data.postValue(response.body());
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                call.cancel();
            }
        });
        return data;
    }

    public LiveData<ApiResponse> updatePoint(String token, String userId, String point) {
        final MutableLiveData<ApiResponse> data= new MutableLiveData<>();
        Call<ApiResponse> call2 = apiInterface.updatePoint(token, userId, point);
        call2.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                data.postValue(response.body());
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                call.cancel();
            }
        });
        return data;
    }

    public LiveData<JsonObject> getPosition(String token, String userId, String selle_id) {

        final MutableLiveData<JsonObject> liveData = new MutableLiveData<>();
        Call<JsonObject> call2 = apiInterface.waitingTime(token, userId, selle_id, Config.APP_ID);
        call2.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                liveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
            }
        });
        return liveData;
    }

    public LiveData<JsonObject> getSeenCount(String token, String userId, String selle_id) {

        final MutableLiveData<JsonObject> liveData = new MutableLiveData<>();
        Call<JsonObject> call2 = apiInterface.getSeenCount(token, userId, selle_id, Config.APP_ID);
        call2.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                liveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
            }
        });
        return liveData;
    }

    public LiveData<PartialsInfo> getPartialsInfo(String token, String userId) {

        final MutableLiveData<PartialsInfo> liveData = new MutableLiveData<>();
        Call<PartialsInfo> call2 = apiInterface.getPartialsInfo(token, userId);
        call2.enqueue(new Callback<PartialsInfo>() {
            @Override
            public void onResponse(Call<PartialsInfo> call, Response<PartialsInfo> response) {
                liveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<PartialsInfo> call, Throwable t) {
                call.cancel();
            }
        });
        return liveData;
    }


    private static class IsFavourite extends AsyncTask<List, Void, Void> {
        private final APIInterface apiInterface;

        private IsFavourite(APIInterface apiInterface) {
            this.apiInterface = apiInterface;
        }

        @Override
        protected Void doInBackground(List... lists) {
            Call<JsonObject> call = apiInterface.isFavourite(lists[0].get(0).toString(), lists[0].get(1).toString(), lists[0].get(2).toString());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
            return null;
        }
    }
}