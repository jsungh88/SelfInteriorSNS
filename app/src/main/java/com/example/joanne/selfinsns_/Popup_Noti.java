package com.example.joanne.selfinsns_;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Popup_Noti extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //키 잠금 해제 및 화면 켜기
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        |WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        |WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        AlertDialog.Builder builder = new AlertDialog.Builder(Popup_Noti.this);
        builder.setTitle(R.string.app_name); //앱 이름
        builder.setMessage(getIntent().getStringExtra("msg"));//넘겨받은 메시지 제목
        builder.setCancelable(false);
        builder.setPositiveButton("내용보기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Popup_Noti.this,Notice.class);
                startActivityForResult(intent,0);
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                nm.cancel(getIntent().getIntExtra("LastId",-1));
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
