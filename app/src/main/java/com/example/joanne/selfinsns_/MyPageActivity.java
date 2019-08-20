package com.example.joanne.selfinsns_;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.resource.bitmap.CircleCrop;
//import com.bumptech.glide.request.RequestOptions;
import com.example.joanne.selfinsns_.retrofit.model.UserInfo;
import com.example.joanne.selfinsns_.retrofit.remote.APIService;
import com.example.joanne.selfinsns_.retrofit.remote.API_Result;
import com.example.joanne.selfinsns_.retrofit.remote.ApiUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageActivity extends AppCompatActivity implements View.OnClickListener {

    /*
    1. 클래스명 : 마이페이지 or 특정 회원정보 보기
    2. 설명: 회원정보 수정 및 활동내역을 볼 수 있는 곳
    3. 기능:
     3-0. sharedpreference "who","writer"or"loginUser"로 구분 하여
    loginUser 이면, "login_user"를,
    writer 이면, "writer_user"정보를 서버로부터 불러와서 셋팅한다.

     3-1. 일반 내정보보기 용도
      1) 회원정보 수정
      2) 관심 글 보기
      3) 팔로잉 목록 보기
      4) 팔로워 목록 보기
      5) 내가 올린 글 탭별로 나눠 보기(노하우, 스타일북, Q&A)

    3-2. 특정 회원정보보기 용도
      1) 팔로잉하기
      2) 해당유저 관심 글 보기
      3) 팔로잉 목록보기
      4) 팔로워 목록보기
      5) 해당 유저 글 탭별로 나눠 보기(노하우, 스타일북, Q&A)
    */

    private ImageView btnBack;//공통

    //화면 셋팅정보 - 마이페이지
    private TextView title_mypage;
    private Button btnEdit, btnViewCall;
    private ImageView picture, btnNotification;//프로필 이미지
    private TextView no, name, email, pwd, gender, age, level, regdate;//유저정보표시
    private File file;
    private Uri uri;

    //화면 셋팅정보 - 회원정보
    private TextView title_writer;
    private Button btnFollow, btnFollowing;

    TabLayout mypageTabs;
    ViewPager mypagePage;

    private APIService service;
    private UserInfo user, writer;//로그인유저,작성자

    private SharedPreferences sp, sp1;//로그인한 사용자정보 불러오기(세션역할), 작성자 정보 불러오기

    private String follow;//팔로우?팔로잉?
    private Boolean fromProfileEdit = false;
    private String image;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_activity);

        //공통셋팅
        title_mypage = findViewById(R.id.mypage_title_mypage);
        title_writer = findViewById(R.id.mypage_title_writer);

        //공통 - 유저정보셋팅
        picture = findViewById(R.id.mypage_uImage);
        name = findViewById(R.id.mypage_uName);
        email = findViewById(R.id.mypage_uEmail);
        gender = findViewById(R.id.mypage_uGender);
        age = findViewById(R.id.mypage_uAge);

        //마이페이지
        btnEdit = (Button) findViewById(R.id.mypage_btn_uEdit);
        btnBack = findViewById(R.id.mypage_btn_back);
        btnNotification = findViewById(R.id.mypage_btn_notification);
//        btnViewCall = findViewById(R.id.mypage_btn_viewcall);
        btnBack.setOnClickListener(this);
        btnNotification.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
//        btnViewCall.setOnClickListener(this);

        //작성자정보보기
        btnFollow = findViewById(R.id.mypage_btn_follow);
        btnFollowing = findViewById(R.id.mypage_btn_following);
        btnFollowing.setOnClickListener(this);
        btnFollow.setOnClickListener(this);

        service = ApiUtils.getAPIService();

        TabLayout mypageTabs = (TabLayout) findViewById(R.id.mypage_tabs);
        ViewPager mypagePage = (ViewPager) findViewById(R.id.mypage_viewpager);

        mypageTabs.setupWithViewPager(mypagePage); //TabLayout과 ViewPager 연결
        setUpViewPager(mypagePage); //ViewPager에 Tablayout 셋업



        /**
         * 로그인한 유저 정보를 프로필 영역에 표시
         * 성별 또는 연령정보가 null인 경우, 해당 영역을 표시하지 않는다.
         *
         */
        sp = getSharedPreferences("login_user", Activity.MODE_PRIVATE); //로그인한 유저 정보를 저장해놓은 셰어드
        Gson gson = new Gson();
        String json = sp.getString("login", null);
        Type type = new TypeToken<UserInfo>() {
        }.getType();
        user = gson.fromJson(json, type);
        Log.e("셰어드 불러오기", "성공");
        //test log
        Log.e("로그인유저:", String.valueOf(user.getNo()));
        Log.e("로그인유저:", user.getEmail());
        Log.e("로그인유저:", user.getPicture());
        Log.e("로그인유저:", user.getName());
        Log.e("로그인유저:", user.getGender());
        Log.e("로그인유저:", user.getAgeRange());
        Log.e("로그인유저:", user.getLevel().toString());
        Log.e("로그인유저:", user.getType());

        try{
            Intent fromProEdit = getIntent();
            fromProfileEdit = fromProEdit.getBooleanExtra("fromProfileEdit",false);
            image = fromProEdit.getStringExtra("image");
        }catch(Exception e){

        }


        //디테일에서 넘어온 경우, 로그인 유저가 아님. .
        sp1 = getSharedPreferences("who", Activity.MODE_PRIVATE); //로그인한 유저 정보를 저장해놓은 셰어드(디테일에서저장)
        String who = sp1.getString("who", "loginUser");

        if (who.equals("writer")) { //작성자면, 셰어드를 불러온다.

            String json2 = sp1.getString("writer_user", null);
            writer = gson.fromJson(json2, type);
            Log.e("셰어드 불러오기", "성공");
            //test log
            Log.e("작성자:", String.valueOf(writer.getNo()));
            Log.e("작성자:", writer.getEmail());
            Log.e("작성자:", writer.getPicture());
            Log.e("작성자:", writer.getName());
            Log.e("작성자:", writer.getGender());
            Log.e("작성자:", writer.getAgeRange());
            Log.e("작성자:", writer.getLevel().toString());
            Log.e("작성자:", writer.getType());


            //**********작성자인데, 로그인유저 no와 같은경우, 마이페이지로 보여준다.**********/
            if (user.getNo() == writer.getNo()) {
                title_writer.setVisibility(View.GONE);
                title_mypage.setVisibility(View.VISIBLE);
                btnNotification.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.VISIBLE);

//                btnViewCall.setVisibility(View.VISIBLE);//////////////////////////////////
                btnFollow.setVisibility(View.GONE);
                btnFollowing.setVisibility(View.GONE);

                name.setText(user.getName());
                email.setText(user.getEmail());
                gender.setText(user.getGender());
                age.setText(user.getAgeRange());
//        level.setText( user.getLevel().toString() ); - 보류

                if (user.getGender().equals("null") || user.getAgeRange().equals("null")) {
                    gender.setVisibility(View.GONE);
                    age.setVisibility(View.GONE);
                }

                if (user.getType().equals("일반")) {
                    Glide.with(this)
                            .load("http://13.209.108.67/uploads/" + user.getPicture())
//                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(picture);
                }

            } else {


                //로그인회원과 팔로잉 관계인지 확인
                Call<API_Result> call = service.follow_view(user.getNo(), writer.getNo());
                call.enqueue(new Callback<API_Result>() {
                    @Override
                    public void onResponse(Call<API_Result> call, Response<API_Result> response) {
                        follow = response.body().getMessage();
                        if (follow.equals("팔로잉")) {
                            btnFollow.setVisibility(View.GONE);
                            btnFollowing.setVisibility(View.VISIBLE);
                            Log.e("팔로잉!!!", "팔로잉");


                        } else {
                            btnFollow.setVisibility(View.VISIBLE);
                            btnFollowing.setVisibility(View.GONE);
                            Log.e("팔로우!!!", "팔로우");

                        }
                    }

                    @Override
                    public void onFailure(Call<API_Result> call, Throwable t) {
                        t.getMessage();
                    }
                });


                unFollow();//언팔로우
                doFollow();//팔로우


                title_writer.setVisibility(View.VISIBLE); //회원정보보기 타이틀
                title_mypage.setVisibility(View.GONE); //마이페이지 타이틀
                btnNotification.setVisibility(View.GONE); //알림내역보기 버튼
                btnEdit.setVisibility(View.GONE); //프로필편집 버튼
                //                btnViewCall.setVisibility(View.VISIBLE);/////////////////////////////////

                name.setText(writer.getName());
                email.setText(writer.getEmail());
                gender.setText(writer.getGender());
                age.setText(writer.getAgeRange());
//        level.setText( user.getLevel().toString() ); - 보류

                if (writer.getGender().equals("null") || writer.getAgeRange().equals("null")) {
                    gender.setVisibility(View.GONE);
                    age.setVisibility(View.GONE);
                }

                if (writer.getType().equals("일반")) {
                    Glide.with(this)
                            .load("http://13.209.108.67/uploads/" + writer.getPicture())
                            .into(picture);
                }

            }
        } else {
            title_writer.setVisibility(View.GONE);
            title_mypage.setVisibility(View.VISIBLE);
            btnNotification.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.VISIBLE);

            btnFollow.setVisibility(View.GONE);
            btnFollowing.setVisibility(View.GONE);

            name.setText(user.getName());
            email.setText(user.getEmail());
            gender.setText(user.getGender());
            age.setText(user.getAgeRange());
//        level.setText( user.getLevel().toString() ); - 보류

            if (user.getGender().equals("null") || user.getAgeRange().equals("null")) {
                gender.setVisibility(View.GONE);
                age.setVisibility(View.GONE);
            }

            if (user.getType().equals("일반")) {
                Glide.with(this)
                        .load("http://13.209.108.67/uploads/" + user.getPicture())
                        .into(picture);
                picture.setBackground(new ShapeDrawable(new OvalShape()));
                if (Build.VERSION.SDK_INT >= 21) {
                    picture.setClipToOutline(true);
                }
                picture.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            Log.e("유저이미지,",user.getPicture());




        }


    }

    private void unFollow() {//언팔 - 팔로우삭제
        btnFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<API_Result> call = service.follow_unfollow(user.getNo(), writer.getNo(), "취소");
                call.enqueue(new Callback<API_Result>() {
                    @Override
                    public void onResponse(Call<API_Result> call, Response<API_Result> response) {
                        follow = response.body().getMessage();
                        if (follow.equals("팔로잉")) {
                            btnFollow.setVisibility(View.GONE);
                            btnFollowing.setVisibility(View.VISIBLE);
                            Log.e("팔로잉!!!", "팔로잉");
                        } else {
                            btnFollow.setVisibility(View.VISIBLE);
                            btnFollowing.setVisibility(View.GONE);
                            Log.e("팔로우!!!", "팔로우");
                        }
                    }

                    @Override
                    public void onFailure(Call<API_Result> call, Throwable t) {
                        t.getMessage();
                    }
                });


            }
        });
    }

    private void doFollow() {//팔로우

        Log.e("user.getno", String.valueOf(user.getNo()));
        Log.e("writer.getno", String.valueOf(writer.getNo()));
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<API_Result> call = service.follow_regit(user.getNo(), writer.getNo(), "등록");
                call.enqueue(new Callback<API_Result>() {
                    @Override
                    public void onResponse(Call<API_Result> call, Response<API_Result> response) {
                        follow = response.body().getMessage();
                        if (follow.equals("팔로잉")) {
                            btnFollow.setVisibility(View.GONE);
                            btnFollowing.setVisibility(View.VISIBLE);
                            Log.e("팔로잉!!!", "팔로잉");
                        } else {
                            btnFollow.setVisibility(View.VISIBLE);
                            btnFollowing.setVisibility(View.GONE);
                            Log.e("팔로우!!!", "팔로우");
                        }
                    }

                    @Override
                    public void onFailure(Call<API_Result> call, Throwable t) {
                        t.getMessage();
                    }
                });

            }
        });
    }

    //탭뷰페이저
    public void setUpViewPager(ViewPager viewPage) {

        List<Fragment> mypageFragment = new ArrayList<>();
        MypageViewPageAdapter adapter = new MypageViewPageAdapter(getSupportFragmentManager(), mypageFragment);

        adapter.addFragmentPage(new KnowHowActivity(), "노하우");
        adapter.addFragmentPage(new StyleBook(), "스타일북");
        adapter.addFragmentPage(new QnA(), "Q&A");

        viewPage.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mypage_btn_uEdit:
                Intent toJoinEdit = new Intent(MyPageActivity.this, JoinActivity.class);
                toJoinEdit.putExtra("fromMypage",true);
                startActivity(toJoinEdit);
                finish();
                break;
            case R.id.mypage_btn_back:
                onBackPressed();
                sp1 = getSharedPreferences("who", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp1.edit();
                editor.putString("who", "loginUser"); //로그인유저정보가 아니라고 저장.
                editor.commit();
                break;
            case R.id.mypage_btn_notification:
                Intent toNoti = new Intent(MyPageActivity.this, NotificationActivity.class);
                toNoti.setAction(Intent.ACTION_VIEW);
                startActivity(toNoti);
                break;

//            case R.id.mypage_btn_viewcall:
//
//                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sp1 = getSharedPreferences("who", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp1.edit();
        editor.putString("who", "loginUser"); //로그인유저정보가 아니라고 저장.
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sp1 = getSharedPreferences("who", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp1.edit();
        editor.putString("who", "loginUser"); //로그인유저정보가 아니라고 저장.
        editor.commit();
    }

    //뷰페이저 클래스
    public class MypageViewPageAdapter extends FragmentPagerAdapter {
        private List mypageFragment = new ArrayList<>();
        private List mypagePageTitle = new ArrayList<>();

        public MypageViewPageAdapter(FragmentManager fm, List<Fragment> mypageFragment) {
            super(fm);
            this.mypageFragment = mypageFragment;
        }

        public void addFragmentPage(Fragment frag, String title) {
            mypageFragment.add(frag);
            mypagePageTitle.add(title);
        }


        @Override
        public Fragment getItem(int position) {
            return (Fragment) mypageFragment.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return (CharSequence) mypagePageTitle.get(position);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        TabLayout mypageTabs = (TabLayout) findViewById(R.id.mypage_tabs);
        ViewPager mypagePage = (ViewPager) findViewById(R.id.mypage_viewpager);

        mypageTabs.setupWithViewPager(mypagePage); //TabLayout과 ViewPager 연결
        setUpViewPager(mypagePage); //ViewPager에 Tablayout 셋업
    }
}
