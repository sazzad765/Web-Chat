package com.team15.webchat.Repositories;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.team15.webchat.Api.APIClient;
import com.team15.webchat.Api.APIInterface;
import com.team15.webchat.App.Config;
import com.team15.webchat.Model.Banner;

import java.util.List;
import java.util.Observable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppRepository extends Observable {
    private APIInterface apiInterface;
    private static AppRepository appRepository;

    public static AppRepository getInstance() {
        if (appRepository == null) {
            appRepository = new AppRepository();
        }
        return appRepository;
    }

    private AppRepository() {
        apiInterface = APIClient.getRetrofitInstance().create(APIInterface.class);
    }

//    public void sendMessage(String token ,String senderId,String receiverId,String message,String type) {
//        ArrayList<String> list=new ArrayList<String>();
//        list.add(token);
//        list.add(senderId);
//
//        new ChatRepository.SendMessageAsyncTask(apiInterface).execute(list);
//    }


    public LiveData<List<Banner>> getBanner(){
        final MutableLiveData<List<Banner>> data = new MutableLiveData<>();
        Call<List<Banner>> call2 = apiInterface.getBanner(Config.APP_ID);
        call2.enqueue(new Callback<List<Banner>>() {
            @Override
            public void onResponse(Call<List<Banner>> call, Response<List<Banner>> response) {
                data.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Banner>> call, Throwable t) {

            }
        });
        return data;
    }



//    private static class SendMessageAsyncTask extends AsyncTask<List, Void,Void> {
//        private final APIInterface apiInterface;
//        private SendMessageAsyncTask(APIInterface apiInterface) {
//            this.apiInterface = apiInterface;
//        }


//        @Override
//        protected Void doInBackground(List... lists) {
//            Call<JsonObject> call = apiInterface.sendMessage(
//                    lists[0].get(0).toString(),lists[0].get(1).toString(),lists[0].get(2).toString(), Config.APP_ID,lists[0].get(3).toString(),lists[0].get(4).toString());
//            call.enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//
//                }
//
//                @Override
//                public void onFailure(Call<JsonObject> call, Throwable t) {
//
//                }
//            });
//            return null;
//        }
//    }
}