package com.example.joanne.selfinsns_;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.joanne.selfinsns_.retrofit.model.NotificationItem;
import com.example.joanne.selfinsns_.retrofit.model.UserInfo;
import com.example.joanne.selfinsns_.retrofit.remote.APIService;
import com.example.joanne.selfinsns_.retrofit.remote.ApiUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    /**
     * 클래스명: 알림내역 리스트
     * 설명: 좋아요,댓글,팔로잉 리스트가 보여진다.
     * 기능:
     * 1.서버로부터 로그인 유저no의 알림리스트를 불러온다.
     * 2.팔로잉일 때와 댓글,좋아요일 때의 itemView를 다르게하여 리스트에 뿌려준다.     *
     */

    //화면요소셋팅
    private ImageView btnClose;
    private RecyclerView nRecyclerView;

    //알림내역리사이클러뷰
    private List<NotificationItem> items;
    private NotificationAdapter nAdapter;

    //서버
    private APIService service;

    //로그인유저 정보
    private SharedPreferences sp;
    private UserInfo user;
    private Integer user_no;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notification_activity);

        service = ApiUtils.getAPIService();

        //닫기버튼
        btnClose = findViewById(R.id.noti_btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //로그인유저 정보 가져오기
        sp = getSharedPreferences("login_user", Activity.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString("login", null);
        Type type = new TypeToken<UserInfo>() {
        }.getType();
        user = gson.fromJson(json, type);
        Log.e("로그인유저:", String.valueOf(user.getNo()));//로그인유저 no = 작성자 no
        user_no = user.getNo();

        //알림내역 리스트
        nRecyclerView = findViewById(R.id.notification_recyclerview);
        getNotiData();


    }

    private void getNotiData() {
        Log.e("로그인회원", String.valueOf(user_no));
        Call<List<NotificationItem>> call = service.notification_view(user_no);
        call.enqueue(new Callback<List<NotificationItem>>() {
            @Override
            public void onResponse(Call<List<NotificationItem>> call, Response<List<NotificationItem>> response) {
                items = response.body();
                Log.e("노티가져오기", "성공");
                Log.e("노티가져오기", String.valueOf(items));
                for (NotificationItem n : items) {

                    Log.e("id", String.valueOf(n.getId())); //노티id
                    try {
                        Log.e("group", n.getGroup());
                    } catch (Exception e) {
                        e.getMessage();
                    }
                    Log.e("category", n.getCategory());
                    Log.e("noti_desc", n.getNoti_desc());
                    Log.e("sender_no", String.valueOf(n.getSender_no()));
                    Log.e("receiver_no", String.valueOf(n.getReceiver_no()));
                    try {
                        Log.e("sb_id", String.valueOf(n.getSb_id()));
                    } catch (Exception e) {
                        e.getMessage();
                    }
                    Log.e("regdate", n.getRegdate());
                    Log.e("image", n.getImage());
                    Log.e("user_image", n.getuImage());
                    Log.e("user_name", n.getuName());
                    Log.e("follow", n.getFollow());

                }
                //어댑터
                nAdapter = new NotificationAdapter(NotificationActivity.this, items);
                nRecyclerView.setAdapter(nAdapter);

            }

            @Override
            public void onFailure(Call<List<NotificationItem>> call, Throwable t) {

            }
        });

    }
}
