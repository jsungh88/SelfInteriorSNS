package com.example.joanne.selfinsns_;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;

public class FCMListenerService extends com.google.firebase.messaging.FirebaseMessagingService{

    private static final String TAG = "FCM";

    int mLastId = 0;
    ArrayList<Integer> mActiveIdLst = new ArrayList<Integer>();
    NotificationManager nm;

    //[START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //메시지를 받았을 때 동작하는 메소드
        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");
        Log.e(TAG,"from:"+remoteMessage.getFrom());
        Log.e(TAG,"title:"+ title);
        Log.e(TAG,"message:"+message);

        sendPushNotification(title,message);
    }

    private void createNotificationId(){
        int id = ++mLastId;
        mActiveIdLst.add(id);
    }

    public void sendPushNotification(String title, String message){
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(mLastId);
        createNotificationId();

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setSound(defaultSoundUri)
                .setLights(000000255,500,2000)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentText(message);

        Intent popupIntent = new Intent(getApplicationContext(), Popup_Noti.class);
        popupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        popupIntent.putExtra("msg",title);
        popupIntent.putExtra("LastId",mLastId);
        startActivity(popupIntent); //메시지 팝업창을 바로 띄운다.

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,0,popupIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        nm.notify(mLastId,mBuilder.build());
    }
}
