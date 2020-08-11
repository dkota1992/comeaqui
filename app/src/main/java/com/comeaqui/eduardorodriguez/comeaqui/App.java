package com.comeaqui.eduardorodriguez.comeaqui;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.comeaqui.eduardorodriguez.comeaqui.objects.User;

public class App extends Application {

    public static User USER;
    public static int MAX_CONNECTIONS_TRIES = 4;
    public static final String MESSAGES_CHANNEL_ID = "messages";
    public static final String NOTIFICATIONS_CHANNEL_ID = "notifications";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel messagesChannel = new NotificationChannel(
                    MESSAGES_CHANNEL_ID,
                    "Messages",
                    NotificationManager.IMPORTANCE_HIGH
            );
            messagesChannel.setDescription("This is the channel for chat messages");

            NotificationChannel notificationsChannel = new NotificationChannel(
                    NOTIFICATIONS_CHANNEL_ID,
                    "Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationsChannel.setDescription("This is the channel for notifitications");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(messagesChannel);
            manager.createNotificationChannel(notificationsChannel);
        }
    }
}
