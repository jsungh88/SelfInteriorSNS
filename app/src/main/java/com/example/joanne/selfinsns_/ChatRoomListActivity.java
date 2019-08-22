package com.example.joanne.selfinsns_;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joanne.selfinsns_.retrofit.model.ChatItem;
import com.example.joanne.selfinsns_.retrofit.model.ChatRoomItem;
import com.example.joanne.selfinsns_.retrofit.model.KnowHowItem;
import com.example.joanne.selfinsns_.retrofit.model.UserInfo;
import com.example.joanne.selfinsns_.retrofit.remote.APIService;
import com.example.joanne.selfinsns_.retrofit.remote.API_Result;
import com.example.joanne.selfinsns_.retrofit.remote.ApiUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoomListActivity extends AppCompatActivity {

    //    SwipeRefreshLayout mSwipeRefreshLayout1, mSwipeRefreshLayout2;
    RecyclerView roomRecyclerView_in, roomRecyclerView_out;
    ChatRoomAdapter cAdapter_in, cAdapter_out;
    List<ChatRoomItem> items_in, items_out;

    FloatingActionButton fab;//채팅방생성

    TextView title;
    ImageView btnClose;

    private APIService service;
    private UserInfo user;
    private SharedPreferences sp;//로그인한 사용자정보 불러오기(세션역할)
    List<ChatItem> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chatroom_list);
        service = ApiUtils.getAPIService();

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


        title = findViewById(R.id.chat_title);
        btnClose = findViewById(R.id.chat_btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        mSwipeRefreshLayout1 = (SwipeRefreshLayout)findViewById(R.id.swipe_layout1);
//        mSwipeRefreshLayout2 = (SwipeRefreshLayout)findViewById(R.id.swipe_layout2);
//        mSwipeRefreshLayout1.setOnRefreshListener(this);
//        mSwipeRefreshLayout2.setOnRefreshListener(this);
//        mSwipeRefreshLayout1.setColorSchemeResources(
//                android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light
//        );

        roomRecyclerView_in = findViewById(R.id.chatroomlist_in);   //참여중인 채팅방
        roomRecyclerView_out = findViewById(R.id.chatroomlist_out);   //참여중이지 않은 채팅방
        roomRecyclerView_in.setHasFixedSize(true);
        roomRecyclerView_out.setHasFixedSize(true);
        fab = findViewById(R.id.fab_newchat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMakeRoom = new Intent(ChatRoomListActivity.this, ChatMakeRookActivity.class);
                toMakeRoom.setAction(Intent.ACTION_VIEW);
                startActivity(toMakeRoom);
            }
        });

        items_in = new ArrayList<>();
        items_out = new ArrayList<>();
        getRoomList_in(); //서버로부터 참여한채팅방리스트를 가져온다.
        getRoomList_out(); //서버로부터 참여하지않은채팅방리스트를 가져온다.

        roomRecyclerView_in.addOnItemTouchListener(new ChatRoomAdapter.RecyclerViewOnItemClickListener(this, roomRecyclerView_in,
                new ChatRoomAdapter.RecyclerViewOnItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Integer userNo = user.getNo();
                        Integer roomId = items_in.get(position).getGroup_id();
                        String roomName = items_in.get(position).getGroup_name();
                        String first = items_in.get(position).getFirst();
                        //서버 방참여자DB 저장.
                        Call<API_Result> call = service.chatroom_in(userNo, roomId);
                        call.enqueue(new Callback<API_Result>() {
                            @Override
                            public void onResponse(Call<API_Result> call, Response<API_Result> response) {
                                Log.e("방참여DB저장", "성공");

                            }

                            @Override
                            public void onFailure(Call<API_Result> call, Throwable t) {
                                Log.e("방참여DB저장", "실패");
                            }
                        });

                        Intent toChatting = new Intent(ChatRoomListActivity.this, ChattingActivity.class);
                        toChatting.putExtra("roomId", roomId);
                        toChatting.putExtra("roomName", roomName);
                        toChatting.putExtra("first", first);
                        startActivity(toChatting);
//                        toChatting.putExtra("roomLeader",items_in.get(position).getRoomLeader());
                    }

                    @Override
                    public void onItemLongClick(View v, int position) {

                    }
                }));

        roomRecyclerView_out.addOnItemTouchListener(new ChatRoomAdapter.RecyclerViewOnItemClickListener(this, roomRecyclerView_out,
                new ChatRoomAdapter.RecyclerViewOnItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {

                        Integer userNo = user.getNo();
                        Integer roomId = items_out.get(position).getGroup_id();
                        String roomName = items_out.get(position).getGroup_name();
                        String first = items_out.get(position).getFirst();
                        //서버 방참여자DB 저장.
                        Call<API_Result> call = service.chatroom_in(userNo, roomId);
                        call.enqueue(new Callback<API_Result>() {
                            @Override
                            public void onResponse(Call<API_Result> call, Response<API_Result> response) {
                                Log.e("방참여DB저장", "성공");
                            }

                            @Override
                            public void onFailure(Call<API_Result> call, Throwable t) {
                                Log.e("방참여DB저장", "실패");
                            }
                        });

                        Intent toChatting = new Intent(ChatRoomListActivity.this, ChattingActivity.class);
                        toChatting.putExtra("roomId", roomId);
                        toChatting.putExtra("roomName", roomName);
                        toChatting.putExtra("first",first);
                        startActivity(toChatting);
//                        toChatting.putExtra("roomLeader",items_in.get(position).getRoomLeader());
                    }

                    @Override
                    public void onItemLongClick(View v, int position) {

                    }
                }));
    }

    //사용자no를 전달해서 사용자가 참여한 방리스트를 불러온다.
    public void getRoomList_in() {
        Call<List<ChatRoomItem>> call = service.chatroom_view(user.getNo(), "in");
        call.enqueue(new Callback<List<ChatRoomItem>>() {
            @Override
            public void onResponse(Call<List<ChatRoomItem>> call, Response<List<ChatRoomItem>> response) {
                items_in = response.body();
                for (ChatRoomItem c : items_in) {
                    Log.e("in_roomId", String.valueOf(c.getGroup_id()));
                    Log.e("in_roomName", c.getGroup_name());
//                    Log.e("roomImage", c.getChatroom_groupimage());
                    Log.e("in_방장이미지", c.getLeaderImage());
                    Log.e("in_방장이름", c.getLeaderName());
                    Log.e("in_방장id", String.valueOf(c.getLeaderId()));
                    Log.e("in_참여자수", String.valueOf(c.getCount()));
//                    Log.e("in_room최근메시지", c.getMessage());
                    Log.e("in_room최근보낸메시지시간", c.getTime());
                    Log.e("in_room첫입장여부", c.getFirst());
                }
                //여기 리스트 적용.
                cAdapter_in = new ChatRoomAdapter(ChatRoomListActivity.this, items_in); //참여한 채팅방 리스트
                roomRecyclerView_in.setLayoutManager(new LinearLayoutManager(ChatRoomListActivity.this, LinearLayout.VERTICAL, false));
                roomRecyclerView_in.setAdapter(cAdapter_in);

            }

            @Override
            public void onFailure(Call<List<ChatRoomItem>> call, Throwable t) {

            }
        });
    }

    //사용자no를 전달해서 사용자가 참여하지 않은 방리스트를 불러온다.
    public void getRoomList_out() {
        Call<List<ChatRoomItem>> call = service.chatroom_view(user.getNo(), "out");
        call.enqueue(new Callback<List<ChatRoomItem>>() {
            @Override
            public void onResponse(Call<List<ChatRoomItem>> call, Response<List<ChatRoomItem>> response) {
                items_out = response.body();
                for (ChatRoomItem c : items_out) {
                    Log.e("out_roomId", String.valueOf(c.getGroup_id()));
                    Log.e("out_roomName", c.getGroup_name());
//                    Log.e("roomImage", c.getChatroom_groupimage());
                    Log.e("out_방장이미지", c.getLeaderImage());
                    Log.e("out_방장이름", c.getLeaderName());
                    Log.e("out_방장id", String.valueOf(c.getLeaderId()));
                    Log.e("out_참여자수", String.valueOf(c.getCount()));
                    Log.e("in_room첫입장여부", c.getFirst());

                }
                //여기 리스트 적용.
                cAdapter_out = new ChatRoomAdapter(ChatRoomListActivity.this, items_out); //참여하지않은 채팅방 리스트
                roomRecyclerView_out.setLayoutManager(new LinearLayoutManager(ChatRoomListActivity.this, LinearLayout.VERTICAL, false));
                roomRecyclerView_out.setAdapter(cAdapter_out);
            }

            @Override
            public void onFailure(Call<List<ChatRoomItem>> call, Throwable t) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getRoomList_in(); //서버로부터 참여한채팅방리스트를 가져온다.
        getRoomList_out(); //서버로부터 참여하지않은채팅방리스트를 가져온다.
    }

//    @Override
//    public void onRefresh() { //리스트 갱신
//        getRoomList_in(); //서버로부터 참여한채팅방리스트를 가져온다.
//        getRoomList_out(); //서버로부터 참여하지않은채팅방리스트를 가져온다.
//        Toast.makeText(this,"안녕",Toast.LENGTH_SHORT).show();
//        // 새로고침 완료
//        mSwipeRefreshLayout1.setRefreshing(false);
//        mSwipeRefreshLayout2.setRefreshing(false);
//    }
}
