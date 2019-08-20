package com.example.joanne.selfinsns_;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatInviteUserActivity extends AppCompatActivity{

    private ImageView btnBack;
    private TextView txtTitle;
    private Button btnOK;
    private RecyclerView userRecyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_invite_user_list);

        btnBack = findViewById(R.id.invite_btnback); //뒤로가기
        txtTitle = findViewById(R.id.invite_title); //타이틀
        btnOK = findViewById(R.id.invite_btnOk); //확인버튼
        userRecyclerview = findViewById(R.id.chat_inviteuserList); //초대리스트





    }
}
