package com.team15.webchat.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.team15.webchat.Model.ActiveUser;
import com.team15.webchat.Model.User;
import com.team15.webchat.Repositories.ChatRepository;

public class ChatViewModel extends ViewModel {
    private ChatRepository repository;
    private LiveData<User> getUser;

    public ChatViewModel() {
        super();
        repository = ChatRepository.getInstance();

    }

    public LiveData<ActiveUser> activeUser(String token, String appId) {
        LiveData<ActiveUser> data;
        data = repository.activeUser(token, appId);
        return data;
    }
}
