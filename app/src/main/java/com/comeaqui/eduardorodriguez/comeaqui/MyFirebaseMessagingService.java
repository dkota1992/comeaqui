package com.comeaqui.eduardorodriguez.comeaqui;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.comeaqui.eduardorodriguez.comeaqui.App.*;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private NotificationManagerCompat notificationManager;


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("NEW_TOKEN",s);
        // sendRegistrationToServer(token);
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        notificationManager = NotificationManagerCompat.from(this);
        String channel = remoteMessage.getData().get("channel");

        switch (channel){
            case MESSAGES_CHANNEL_ID:
                Intent intent = new Intent(remoteMessage.getNotification().getClickAction());
                intent.putExtra("chatId", remoteMessage.getData().get("chatId"));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_ONE_SHOT);
                Notification messageNotification = new NotificationCompat.Builder(this, MESSAGES_CHANNEL_ID)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setContentIntent(pendingIntent)
                        .build();
                notificationManager.notify(0, messageNotification);
                break;

            case NOTIFICATIONS_CHANNEL_ID:
                Intent intent2 = new Intent(remoteMessage.getNotification().getClickAction());
                intent2.putExtra("tab", remoteMessage.getData().get("tab"));
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent2 = PendingIntent.getActivity(this,0, intent2, PendingIntent.FLAG_ONE_SHOT);
                Notification notificationNotification = new NotificationCompat.Builder(this, NOTIFICATIONS_CHANNEL_ID)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_EVENT)
                        .setContentIntent(pendingIntent2)
                        .build();
                notificationManager.notify(2, notificationNotification);
                break;
        }
        super.onMessageReceived(remoteMessage);
    }
}