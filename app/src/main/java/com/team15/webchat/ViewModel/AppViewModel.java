package com.team15.webchat.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.team15.webchat.Model.Banner;
import com.team15.webchat.Repositories.AppRepository;


import java.util.List;

public class AppViewModel extends ViewModel {
    private AppRepository repository;
    LiveData<List< Banner >> data;
    public AppViewModel() {
        super();
        repository = AppRepository.getInstance();

    }

    public LiveData<List<Banner>> getBanner() {
        data = repository.getBanner();
        return data;
    }


//    public void sendMessage(String token ,String senderId,String receiverId,String message,String type) {
//        repository.sendMessage( token , senderId, receiverId, message, type);
//    }
}
