package com.team15.webchat.ViewModel;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import com.team15.webchat.App.Config;
import com.team15.webchat.Model.ActiveUser;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.Chat;
import com.team15.webchat.Model.ChatListPaging;
import com.team15.webchat.Model.ChatPag;
import com.team15.webchat.Model.DeviceReg;
import com.team15.webchat.Model.User;
import com.team15.webchat.Model.WaitingList;
import com.team15.webchat.Repositories.ChatRepository;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ChatViewModel extends ViewModel {
    private ChatRepository repository;
    private LiveData<User> getUser;
    LiveData<ActiveUser> data;
    LiveData<ChatListPaging> pagingLiveData;
    LiveData<ChatPag> chatPagLiveData;
    LiveData<List<WaitingList>> waitingList;

    public ChatViewModel() {
        super();
        repository = ChatRepository.getInstance();
    }

    public LiveData<ActiveUser> activeUser(String token, String appId, String currentPage) {
        data = repository.activeUser(token, appId, currentPage);
        return data;
    }

    public LiveData<ChatListPaging> getChatList(String token, String user_id, String currentPage) {
        pagingLiveData = repository.getChatList(token, user_id, currentPage);
        return pagingLiveData;
    }

    public LiveData<List<WaitingList>> getWaitingList(String token) {
        waitingList = repository.getWaitingList(token);
        return waitingList;
    }
    public LiveData<JsonObject> startChat(String token, String agentId, String user_id, String sellerId) {
        LiveData<JsonObject> objectLiveData;
        objectLiveData = repository.startChat(token,agentId,user_id,sellerId);
        return objectLiveData;
    }

    public LiveData<ChatPag> messageData(String token, String sender_id, String receiver_id, String page) {
        chatPagLiveData = repository.messageData(token, sender_id, receiver_id, page);
        return chatPagLiveData;
    }

    public void sendMessage(String token, String senderId, String receiverId, String message, String type,String user_type) {
        repository.sendMessage(token, senderId, receiverId, message, type,user_type);
    }

    public void seenMessage(String token, String senderId, String receiverId) {
        repository.seenMessage(token, senderId, receiverId);
    }

    public String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public File compressor(File file, Activity activity) {
        File compressedImageFile = null;
        try {
            compressedImageFile = new Compressor(activity).compressToFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressedImageFile;
    }

    public LiveData<ApiResponse> sendImageMessage(Activity activity, String token, Uri uri, String userId, String receiverId, String userType) {
        LiveData<ApiResponse> data1;
        String filePath = getRealPathFromURIPath(uri, activity);
        File file = new File(filePath);
        File compressedImageFile = compressor(file, activity);

        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), compressedImageFile);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), userId);
        RequestBody reId = RequestBody.create(MediaType.parse("text/plain"), receiverId);
        RequestBody appId = RequestBody.create(MediaType.parse("text/plain"), Config.APP_ID);
        RequestBody ty = RequestBody.create(MediaType.parse("text/plain"), "image");
        RequestBody user = RequestBody.create(MediaType.parse("text/plain"), userType);

        data1 = repository.sendImageMessage(token, fileToUpload, filename, id, reId, appId, ty,user);
        return data1;


    }

}
