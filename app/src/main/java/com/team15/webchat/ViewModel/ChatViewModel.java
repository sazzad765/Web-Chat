package com.team15.webchat.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.team15.webchat.Model.ActiveUser;
import com.team15.webchat.Model.Chat;
import com.team15.webchat.Model.ChatListPaging;
import com.team15.webchat.Model.ChatPag;
import com.team15.webchat.Model.DeviceReg;
import com.team15.webchat.Model.User;
import com.team15.webchat.Repositories.ChatRepository;

public class ChatViewModel extends ViewModel {
    private ChatRepository repository;
    private LiveData<User> getUser;
    LiveData<ActiveUser> data;
    LiveData<ChatListPaging>pagingLiveData;
    LiveData<ChatPag> chatPagLiveData;

    public ChatViewModel() {
        super();
        repository = ChatRepository.getInstance();
    }

    public LiveData<ActiveUser> activeUser(String token, String appId,String currentPage) {
        data = repository.activeUser(token, appId,currentPage);
        return data;
    }

    public LiveData<ChatListPaging> getChatList(String token, String user_id, String currentPage) {
        pagingLiveData = repository.getChatList(token, user_id,currentPage);
        return pagingLiveData;
    }

    public LiveData<ChatPag> messageData(String token, String sender_id, String receiver_id, String appId) {
        chatPagLiveData = repository.messageData(token, sender_id,receiver_id,appId);
        return chatPagLiveData;
    }

    public void sendMessage(String token ,String senderId,String receiverId,String message,String type) {
        repository.sendMessage( token , senderId, receiverId, message, type);
    }
    public void seenMessage(String token ,String senderId,String receiverId) {
        repository.seenMessage( token , senderId, receiverId);
    }
}
