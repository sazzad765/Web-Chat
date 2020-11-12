package com.team15.webchat.FCM;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.team15.webchat.Api.APIClient;
import com.team15.webchat.Api.APIInterface;
import com.team15.webchat.App.CheckBackground;
import com.team15.webchat.App.Config;
import com.team15.webchat.App.MyBroadcastReceiver;
import com.team15.webchat.MainActivity;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.Chat;
import com.team15.webchat.Model.Message;
import com.team15.webchat.R;
import com.team15.webchat.Session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.team15.webchat.App.App.CHANNEL_1_ID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    SessionManager sessionManager;
    APIInterface apiInterface;
    public static List<Message> MESSAGES = new ArrayList<>();


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        sessionManager = new SessionManager(this);

        if (sessionManager.isLogin()) {
            HashMap<String, String> userInfo = sessionManager.get_user();
            String user_id = userInfo.get(SessionManager.USER_ID);
            String api_key = userInfo.get(SessionManager.API_KEY);

            apiInterface = APIClient.getRetrofitInstance().create(APIInterface.class);
            Call<ApiResponse> call = apiInterface.updateToken("Bearer " + api_key, s, user_id);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String messageBody = remoteMessage.getData().get("message");
        sessionManager = new SessionManager(this);

        String receiverId = remoteMessage.getData().get("reciver_id");
        String sender_name = remoteMessage.getData().get("sender_name");
        String senderId = remoteMessage.getData().get("sender_id");
        String message = remoteMessage.getData().get("message");
        int seen = Integer.parseInt(remoteMessage.getData().get("seen"));
        String createdAt = remoteMessage.getData().get("created_at");
        String type = remoteMessage.getData().get("type");

        Chat chat = new Chat(receiverId, senderId, message, Integer.parseInt(Config.APP_ID), seen, createdAt, type);
        Message message1 = new Message(message, senderId, sender_name);
        MESSAGES.clear();
        MESSAGES.add(message1);
        //            if (CheckBackground.isAppIsInBackground(this)) {
        sendChannel1Notification(this);
//            }else {
//                playNotificationSound(this);
//            }

        if (sessionManager.isLogin()) {
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("type", Config.PUSH_TYPE_USER);
            pushNotification.putExtra("chat", chat);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
        }
    }


    public static void sendChannel1Notification(Context context) {


        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, activityIntent, 0);
        RemoteInput remoteInput = new RemoteInput.Builder("key_text_reply")
                .setLabel("Your answer...")
                .build();

        Intent replyIntent;
        PendingIntent replyPendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            replyIntent = new Intent(context, MyBroadcastReceiver.class);
            replyIntent.putExtra("id", MESSAGES.get(0).getSender());
            replyPendingIntent = PendingIntent.getBroadcast(context,
                    0, replyIntent, FLAG_UPDATE_CURRENT);
        } else {
            //start chat activity instead (PendingIntent.getActivity)
            //cancel notification with notificationManagerCompat.cancel(id)
        }

        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_launcher_foreground,
                "Reply",
                replyPendingIntent
        ).addRemoteInput(remoteInput).build();
        NotificationCompat.MessagingStyle messagingStyle =
                new NotificationCompat.MessagingStyle("Me");
        messagingStyle.setConversationTitle("New Message");
        for (Message chatMessage : MESSAGES) {
            NotificationCompat.MessagingStyle.Message notificationMessage =
                    new NotificationCompat.MessagingStyle.Message(
                            chatMessage.getText(),
                            chatMessage.getTimestamp(),
                            chatMessage.getName()
                    );
            messagingStyle.addMessage(notificationMessage);
        }
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setStyle(messagingStyle)
                .addAction(replyAction)
                .setColor(Color.BLUE)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                    "Chat",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            notification.setVibrate(new long[0]);
        }
        notificationManager.notify(1, notification.build());
    }

    public void playNotificationSound(Context context) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "Notification";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("New Message")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}
