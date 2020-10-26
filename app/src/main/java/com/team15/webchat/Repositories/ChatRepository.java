package com.team15.webchat.Repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.team15.webchat.Api.APIClient;
import com.team15.webchat.Api.APIInterface;
import com.team15.webchat.Model.ActiveUser;
import com.team15.webchat.Model.DeviceReg;
import com.team15.webchat.Model.Login;
import com.team15.webchat.Model.User;

import java.util.List;
import java.util.Observable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepository extends Observable {
    private APIInterface apiInterface;
    final MutableLiveData<ActiveUser> data = new MutableLiveData<>();

    private static ChatRepository chatRepository;

    public static ChatRepository getInstance() {
        if (chatRepository == null) {
            chatRepository = new ChatRepository();
        }
        return chatRepository;
    }

    private ChatRepository() {
        apiInterface = APIClient.getRetrofitInstance().create(APIInterface.class);
    }


    public LiveData<ActiveUser> activeUser(String token, String appId){
        Call<ActiveUser> call2 = apiInterface.activeUser(token,appId);
        call2.enqueue(new Callback<ActiveUser>() {
            @Override
            public void onResponse(Call<ActiveUser> call, Response<ActiveUser> response) {
                data.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ActiveUser> call, Throwable t) {

            }
        });
        return data;
    }
}