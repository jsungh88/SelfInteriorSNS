package com.example.joanne.selfinsns_;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.joanne.selfinsns_.retrofit.model.ChatItem;
import com.example.joanne.selfinsns_.retrofit.model.UserInfo;
import com.example.joanne.selfinsns_.retrofit.remote.APIService;
import com.example.joanne.selfinsns_.retrofit.remote.API_Result;
import com.example.joanne.selfinsns_.retrofit.remote.ApiUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatMakeRookActivity extends AppCompatActivity{

    private ImageView btnClose;
    private Button btnMakeRoom;
    private EditText editName;
    private String roomName;
    private ChatItem item;

    //서버
    private APIService service;
    private UserInfo user;
    private SharedPreferences sp;//로그인한 사용자정보 불러오기(세션역할)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_make_room);

        service = ApiUtils.getAPIService(); // retrofit 빌드 생성

        btnClose = findViewById(R.id.chat_btnClose);
        btnMakeRoom = findViewById(R.id.btn_makeRoom);
        editName = findViewById(R.id.edit_roomName);

        /**
         * 로그인한 유저 정보를 가져온다.
         */
        sp = getSharedPreferences("login_user", Activity.MODE_PRIVATE); //로그인한 유저 정보를 저장해놓은 셰어드
        Gson gson = new Gson();
        String json = sp.getString("login", null);
        Type type = new TypeToken<UserInfo>() {
        }.getType();
        user = gson.fromJson(json, type);
        Log.e("로그인유저:", String.valueOf(user.getNo()));
        Log.e("로그인유저:", user.getEmail());
        Log.e("로그인유저:", user.getPicture());
        Log.e("로그인유저:", user.getName());
        Log.e("로그인유저:", user.getGender());
        Log.e("로그인유저:", user.getAgeRange());
        Log.e("로그인유저:", user.getLevel().toString());
        Log.e("로그인유저:", user.getType());

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnMakeRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //채팅방 이름 입력하기
                roomName = editName.getText().toString().trim();
                Log.e("roomName",roomName);
                Call<ChatItem> call = service.make_chatroom(roomName,user.getNo());
                call.enqueue(new Callback<ChatItem>() {
                    @Override
                    public void onResponse(Call<ChatItem> call, Response<ChatItem> response) {
                        item = response.body();
                        Integer id = item.getRoomId();
                        String name = item.getRoomName();
                        String leader = item.getLeader();
                        Log.e("채팅방만들기", String.valueOf(id));
                        Log.e("채팅방만들기", name);
                        Log.e("채팅방만들기", leader);

                        Intent toChatRoom = new Intent(ChatMakeRookActivity.this, ChattingActivity.class);
                        toChatRoom.setAction(Intent.ACTION_VIEW);
                        toChatRoom.putExtra("fromMakeRoom",true);
                        toChatRoom.putExtra("roomId",id);
                        toChatRoom.putExtra("roomName",name);
                        toChatRoom.putExtra("leaderName",leader);
                        startActivity(toChatRoom);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ChatItem> call, Throwable t) {
                        Log.e("채팅방만들기","실패");
//                        t.getMessage();
                        Toast.makeText(getApplicationContext(), "동명 채팅방이 존재합니다. 채팅방 이름을 다시 입력해주세요.",Toast.LENGTH_LONG).show();
                    }
                });



            }
        });

    }
}
