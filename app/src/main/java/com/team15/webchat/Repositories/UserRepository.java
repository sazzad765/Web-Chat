package com.team15.webchat.Repositories;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.team15.webchat.Api.APIClient;
import com.team15.webchat.Api.APIInterface;
import com.team15.webchat.App.Config;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.DeviceReg;
import com.team15.webchat.Model.Login;
import com.team15.webchat.Model.ShortProfile;
import com.team15.webchat.Model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserRepository extends Observable {
    private APIInterface apiInterface;
    private MutableLiveData<List<User>> allNotes = new MutableLiveData<>();
    final MutableLiveData<User> userProfile = new MutableLiveData<>();
     MutableLiveData<ShortProfile> shortProfileMutableLiveData = new MutableLiveData<>();

    private static UserRepository userRepository;

    public static UserRepository getInstance() {
        if (userRepository == null) {
            userRepository = new UserRepository();
        }
        return userRepository;
    }

    private UserRepository() {
        apiInterface = APIClient.getRetrofitInstance().create(APIInterface.class);
    }
    public void updateDeviceId(DeviceReg deviceReg) {
        new UpdateDeviceIdAsyncTask(apiInterface).execute(deviceReg);
    }

    public void isOnline(String token,String user_id,String status) {
        ArrayList<String> list=new ArrayList<String>();
        list.add(token);
        list.add(user_id);
        list.add(status);
        new IsOnlineAsyncTask(apiInterface).execute(list);
    }

    public LiveData<User> getUser(String token,String userId){

        Call<User> call2 = apiInterface.getUser(token,userId);
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

    public LiveData<ShortProfile> getShortProfile(String token, String userId){

        Call<ShortProfile> call2 = apiInterface.shortProfile(token,userId);
        call2.enqueue(new Callback<ShortProfile>() {
            @Override
            public void onResponse(Call<ShortProfile> call, Response<ShortProfile> response) {
                shortProfileMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ShortProfile> call, Throwable t) {
                call.cancel();
            }
        });
        return shortProfileMutableLiveData;
    }


    public LiveData<Login> getLogin(String phone, String password){
        final MutableLiveData<Login> loginData = new MutableLiveData<>();
        Call<Login> call2 = apiInterface.getUserLogin(phone,password, Config.APP_ID);
        call2.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                loginData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {

            }
        });
        return loginData;
    }

    public LiveData<ApiResponse> registration(String name,String phone,String password,String appId,String email){
        final MutableLiveData<ApiResponse> apiResponse = new MutableLiveData<>();
        Call<ApiResponse> call2 = apiInterface.registration(name,phone,password,appId,email);
        call2.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                apiResponse.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
        return apiResponse;
    }

    public LiveData<ApiResponse> updateProfile(String token,String userId,String name,String phone,String email){
        final MutableLiveData<ApiResponse> apiResponse = new MutableLiveData<>();
        Call<ApiResponse> call2 = apiInterface.updateProfile(token,userId,name,phone,email);
        call2.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                apiResponse.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
        return apiResponse;
    }

    public LiveData<ApiResponse> updateRef(String token,String userId,String refId){
        final MutableLiveData<ApiResponse> apiResponse = new MutableLiveData<>();
        Call<ApiResponse> call2 = apiInterface.updateRef(token,userId,refId);
        call2.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                apiResponse.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
        return apiResponse;
    }

    private static class UpdateDeviceIdAsyncTask extends AsyncTask<DeviceReg , Void, Void> {
        private final APIInterface apiInterface;
        private UpdateDeviceIdAsyncTask(APIInterface apiInterface) {
            this.apiInterface = apiInterface;
        }
        @Override
        protected Void doInBackground(DeviceReg... deviceRegs) {
            Call<ApiResponse> call = apiInterface.updateToken(deviceRegs[0].getToken(),deviceRegs[0].getDeviceId(),deviceRegs[0].getUserId());
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {

                }
            });
            return null;
        }
    }

    private static class IsOnlineAsyncTask extends AsyncTask<List , Void, Void> {
        private final APIInterface apiInterface;
        private IsOnlineAsyncTask(APIInterface apiInterface) {
            this.apiInterface = apiInterface;
        }

        @Override
        protected Void doInBackground(List... lists) {
            Call<JsonObject> call = apiInterface.isOnline(lists[0].get(0).toString(),lists[0].get(1).toString(),lists[0].get(2).toString());
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