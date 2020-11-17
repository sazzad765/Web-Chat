package com.team15.webchat.App;

import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.team15.webchat.ChatActivity;
import com.team15.webchat.FCM.MyFirebaseMessagingService;
import com.team15.webchat.MainActivity;
import com.team15.webchat.Model.Chat;
import com.team15.webchat.Model.Message;
import com.team15.webchat.Repositories.ChatRepository;
import com.team15.webchat.Session.SessionManager;

import java.util.HashMap;

public class MyBroadcastReceiver extends BroadcastReceiver {
    ChatRepository chatRepository;
    SessionManager sessionManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        sessionManager = new SessionManager(context);
        HashMap<String, String> userInfo = sessionManager.get_user();

        String api = userInfo.get(SessionManager.API_KEY);
        String userId = userInfo.get(SessionManager.USER_ID);
        String userType = userInfo.get(SessionManager.USER_TYPE);

        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            CharSequence replyText = remoteInput.getCharSequence("key_text_reply");
            String receiver_id = intent.getStringExtra("sender");
            String sender = intent.getStringExtra("receiver");

            Message message = new Message(replyText,"userId","Me","id");
            MyFirebaseMessagingService.MESSAGES.add(message);
            MyFirebaseMessagingService.sendChannel1Notification(context);
            if (sessionManager.isLogin()) {
                chatRepository = ChatRepository.getInstance();
                chatRepository.sendMessage("Bearer " + api, userId, receiver_id, replyText.toString(), "text",userType);
            }
        }

    }

}