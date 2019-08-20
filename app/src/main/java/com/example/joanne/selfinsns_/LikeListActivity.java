package com.example.joanne.selfinsns_;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.joanne.selfinsns_.retrofit.model.LikerItem;
import com.example.joanne.selfinsns_.retrofit.remote.APIService;
import com.example.joanne.selfinsns_.retrofit.remote.ApiUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikeListActivity extends AppCompatActivity implements View.OnClickListener{

    /**
     * 클래스명: 좋아요 리스트
     * 설명: 특정 글을 좋아요한 사람들 리스트만 볼 수 있는 화면
     * 기능:
     * 0. 상세보기 화면에서 좋아요 숫자를 누르면, 해당글의 id 가 intent로 넘어온다.
     * 1. 글id를 받아 서버로 넘겨 해당글의 '좋아요개수'를 받아온다.
     * 2. 좋아요개수를 화면에 셋팅시킨다.
     * 3. 글id와 로그인유저no를 서버로 보내어 해당글의 좋아요리스트를 받아온다.
     * 4. 리사이클러뷰로 뿌려준다. 끝.
     */


    //화면구성요소셋팅
    private ImageView btnClose;
    private TextView likeCount;
    private RecyclerView lRecyclerView;

    //좋아요리사이클러뷰
    private List<LikerItem> items;
    private LikeAdapter lAdapter;

    //서버
    private APIService service;

    //상세화면에서 받아오는 글 id, 로그인유저no
    private Integer sb_no, loginUser_no;
    private Integer txtLikeCount;//좋아요개수

    private String name,email,picture,follow;//좋아요한사람,이메일,사진,팔로우여부(팔로잉/팔로우)

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.like_activity);

        service = ApiUtils.getAPIService(); // retrofit 빌드 생성

        btnClose = findViewById(R.id.like_btn_close);
        likeCount = findViewById(R.id.txt_like_count);
        lRecyclerView = findViewById(R.id.like_recyclerview);

        btnClose.setOnClickListener(this);
        //상세글로부터 글id, 로그인한유저no 받아오기
        //좋아요리스트로 글id와 로그인유저no를 넘겨준다.
        Intent fromSBdetail = getIntent();
        sb_no= fromSBdetail.getIntExtra("sb_no",0);
        loginUser_no= fromSBdetail.getIntExtra("user_no",0);

        //좋아요수 받아오기
        getLikeCount();//likecount수 받아와서 셋팅.
        items = new ArrayList<>();
        LikeData();

    }

    private void LikeData() {
        Call<List<LikerItem>> call = service.sb_liker_view(sb_no,loginUser_no);
        call.enqueue(new Callback<List<LikerItem>>() {
            @Override
            public void onResponse(Call<List<LikerItem>> call, Response<List<LikerItem>> response) {
                items = response.body();
                for(LikerItem l : items){
                    Log.e("name",l.getName());
                    Log.e("email",l.getEmail());
                    Log.e("picture",l.getPicture());
                    Log.e("follow",l.getFollow());
                }
                lAdapter = new LikeAdapter(LikeListActivity.this,items);
                lRecyclerView.setAdapter(lAdapter);
                lRecyclerView.setAdapter(new LikeAdapter(LikeListActivity.this,items));

            }

            @Override
            public void onFailure(Call<List<LikerItem>> call, Throwable t) {
                Log.e("에러",t.getMessage());
                //좋아요 한 사람이 없습니다.
            }
        });
    }

    private void getLikeCount() {
        Call<Integer> call = service.sb_liker_count(sb_no);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                txtLikeCount = response.body();
                likeCount.setText(String.valueOf(txtLikeCount));
                //좋아요수
                Log.e("좋아요수", String.valueOf(txtLikeCount));

            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e("에러",t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.like_btn_close: //뒤로 혹은 닫기
                onBackPressed();

                break;

        }
    }
}
