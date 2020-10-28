package com.team15.webchat.Repositories;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.team15.webchat.Api.APIClient;
import com.team15.webchat.Api.APIInterface;
import com.team15.webchat.App.Config;
import com.team15.webchat.Model.ActiveUser;
import com.team15.webchat.Model.ChatListPaging;
import com.team15.webchat.Model.ChatPag;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepository extends Observable {
    private APIInterface apiInterface;
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

    public void sendMessage(String token ,String senderId,String receiverId,String message,String type) {
        ArrayList<String> list=new ArrayList<String>();
        list.add(token);
        list.add(senderId);
        list.add(receiverId);
        list.add(message);
        list.add(type);
        new SendMessageAsyncTask(apiInterface).execute(list);
    }


    public LiveData<ActiveUser> activeUser(String token, String appId,String currentPage){
        final MutableLiveData<ActiveUser> data = new MutableLiveData<>();
        Call<ActiveUser> call2 = apiInterface.activeUser(token,appId,currentPage);
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

    public LiveData<ChatListPaging> getChatList(String token, String user_id, String currentPage){
        final MutableLiveData<ChatListPaging> data = new MutableLiveData<>();
        Call<ChatListPaging> call2 = apiInterface.chatList(token,user_id,Config.APP_ID,currentPage);
        call2.enqueue(new Callback<ChatListPaging>() {
            @Override
            public void onResponse(Call<ChatListPaging> call, Response<ChatListPaging> response) {
                data.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ChatListPaging> call, Throwable t) {

            }
        });
        return data;
    }
    public LiveData<ChatPag> messageData(String token, String sender_id, String receiver_id, String appId){
        final MutableLiveData<ChatPag> chatData = new MutableLiveData<>();
        Call<ChatPag> call2 = apiInterface.messageData(token,sender_id,receiver_id,appId);
        call2.enqueue(new Callback<ChatPag>() {
            @Override
            public void onResponse(Call<ChatPag> call, Response<ChatPag> response) {
                chatData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ChatPag> call, Throwable t) {

            }
        });
        return chatData;
    }


    private static class SendMessageAsyncTask extends AsyncTask<List, Void,Void> {
        private final APIInterface apiInterface;
        private SendMessageAsyncTask(APIInterface apiInterface) {
            this.apiInterface = apiInterface;
        }


        @Override
        protected Void doInBackground(List... lists) {
            Call<JsonObject> call = apiInterface.sendMessage(
                    lists[0].get(0).toString(),lists[0].get(1).toString(),lists[0].get(2).toString(), Config.APP_ID,lists[0].get(3).toString(),lists[0].get(4).toString());
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