package com.example.joanne.selfinsns_.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.joanne.selfinsns_.MainActivity;
import com.example.joanne.selfinsns_.NotificationActivity;
import com.example.joanne.selfinsns_.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private LocalBroadcastManager broadcastManager;

    public MyFirebaseMessagingService() { }

    @Override
    public void onCreate() {
        super.onCreate();

        //broadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        broadcastManager = LocalBroadcastManager.getInstance(this);

        String from = remoteMessage.getFrom();
        Map<String, String> data = remoteMessage.getData();
        String title = "팔로우";
        String msg = "_";

        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        android.support.v4.app.NotificationCompat.Builder builder = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "one_channel";
            String channelName = "My Channel One";
            String channelDescription = "My Channel One Description";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(channelDescription);

            manager.createNotificationChannel(channel);
            builder = new android.support.v4.app.NotificationCompat.Builder(this, channelId);
        }
        else{
            builder = new android.support.v4.app.NotificationCompat.Builder(this);
        }

        builder.setSmallIcon(android.R.drawable.screen_background_light);
        //builder.setContentTitle("메이트 신청");
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        if(data.containsKey("title")){
            builder.setContentTitle("팔로우 요청");
            builder.setContentTitle(data.get("message"));
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.stylebook01));
            builder.setSmallIcon(R.drawable.stylebook01);
            builder.setContentIntent(pendingIntent);
//            intent = new Intent("NotificationActivity");

            Log.e("FCM","흠3");
        }else if(data.containsKey("like")){
            builder.setContentTitle("좋아요");
            builder.setContentTitle(data.get("message"));
            builder.setSmallIcon(R.drawable.stylebook01);
            builder.setContentIntent(pendingIntent);

        }

        builder.setWhen(System.currentTimeMillis());
        builder.setContentText(msg);
        builder.setAutoCancel(true);

        manager.notify(222, builder.build());

        broadcastManager.sendBroadcast(intent);



    }

    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.stylebook01))
                .setSmallIcon(R.drawable.stylebook01)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}