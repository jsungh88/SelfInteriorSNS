package com.example.joanne.selfinsns_;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.joanne.selfinsns_.firebase.MyFirebaseInstanceIDService;
import com.example.joanne.selfinsns_.retrofit.model.UserInfo;
import com.example.joanne.selfinsns_.retrofit.remote.APIService;
import com.example.joanne.selfinsns_.retrofit.remote.API_Result;
import com.example.joanne.selfinsns_.retrofit.remote.ApiUtils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private APIService service;

    private ImageView picture;//프로필 이미지
    private TextView no, name, email, pwd, gender, age, level, regdate;//유저정보표시
    private File file;
    private Uri uri;

    private UserInfo user;

    private SharedPreferences sp, sp1;//로그인한 사용자정보 불러오기(세션역할)

    TabLayout mainTabs;//슬라이딩 메뉴
    ViewPager mainPage;//슬라이딩 메뉴

    MyFirebaseInstanceIDService myFirebaseInstanceIDService;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.e("Main", "onCreate");


        mainTabs = findViewById(R.id.main_tabs);
        mainPage = findViewById(R.id.main_page);


        mainTabs.setupWithViewPager(mainPage); //TabLayout과 ViewPager 연결
        setUpViewPager(mainPage);//ViewPager에 TabLayout 셋업
        switchFragment(1);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //drawer 상단 프로필 정보 셋팅
        View headview = navigationView.getHeaderView(0);
        picture = headview.findViewById(R.id.profile_pic);
        name = headview.findViewById(R.id.profile_name);
        email = headview.findViewById(R.id.profile_email);
        gender = headview.findViewById(R.id.profile_gender);
        age = headview.findViewById(R.id.profile_age);
//        level = headview.findViewById( R.id.profile_level );
        Button logout = headview.findViewById(R.id.btn_logout);

        /**
         * 로그인한 유저 정보를 프로필 영역에 표시
         * 성별 또는 연령정보가 null인 경우, 해당 영역을 표시하지 않는다.
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

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        String userToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("userToken",userToken);
        Log.e("userNo", String.valueOf(user.getNo()));

        service = ApiUtils.getAPIService();
        Call<String> call = service.updateToken(userToken,user.getNo());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                response.body();
                Log.e("토큰업데이트",response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("토큰업데이트",t.getMessage());
            }
        });


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
        }

        picture.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= 21) {
            picture.setClipToOutline(true);
        }
        picture.setScaleType(ImageView.ScaleType.FIT_CENTER);
        /**
         * 로그아웃
         * 1. 페이스북, 구글 로그아웃
         * 2. 자동로그인 체크 해제
         * 3. facebook,google 로그인일 경우, email,pw 자동셋팅 해제
         * 4. 로그인 화면으로 이동
         */
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //자동로그인 : 셰어드 "auto_login"값을 "false"로 변경한다.
                Boolean auto_login = false;
                Log.e("auto_login_체크안함", String.valueOf(auto_login));
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("auto_login", auto_login);
                editor.commit();
                Log.e("shared_auto", String.valueOf(auto_login));

                sp = getSharedPreferences("login_user", Activity.MODE_PRIVATE); //로그인한 유저 정보를 저장해놓은 셰어드
                editor.putString("login", null);
                editor.commit();


                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
                finish();

            }
        });


    }

    //page전환
    void switchFragment(int target) {
        mainPage.setCurrentItem(target);
    }


    public void styleRegister(View view) {
        Intent i = new Intent(this, StyleBookRegister.class);
        startActivity(i);
        finish();
    }
    public void knowhowRegister(View view) {
        Intent i = new Intent(this, KnowHowWriteActivity.class);
        startActivity(i);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_search) {
            Intent toSearch = new Intent(MainActivity.this, SearchActivity.class);
            toSearch.setAction(Intent.ACTION_VIEW);
            startActivity(toSearch);

        } else if (id == R.id.nav_chat) { //채팅
            Intent toChat = new Intent(MainActivity.this, ChatRoomListActivity.class);
            toChat.setAction(Intent.ACTION_VIEW);
            startActivity(toChat);


        } else if (id == R.id.nav_mypage) {
            Intent toMypage = new Intent(MainActivity.this, MyPageActivity.class);
            toMypage.putExtra("fromMypage", true);
            startActivity(toMypage);
            Log.e("main_fromMypage", String.valueOf(true));
        } else if (id == R.id.nav_notification) {
            Intent toNoti = new Intent(MainActivity.this, NotificationActivity.class);
            toNoti.setAction(Intent.ACTION_VIEW);
            startActivity(toNoti);
        } else if (id == R.id.nav_setting) {



        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //탭뷰페이저
    public void setUpViewPager(ViewPager viewPage) {

        List<Fragment> mainFragment = new ArrayList<>();
        MainViewPageAdapter adapter = new MainViewPageAdapter(getSupportFragmentManager(), mainFragment);


        adapter.addFragmentPage(new KnowHowActivity(), "  노하우  ");
        adapter.addFragmentPage(new StyleBook(), "  스타일북  ");
//        MainViewPageAdapter.AddFragmentPage( new JaLang(), "자랑하기" );
        adapter.addFragmentPage(new QnA(), "  QnA  ");
        adapter.addFragmentPage(new Expert(), "  전문가  ");

        viewPage.setAdapter(adapter);

    }


    public class MainViewPageAdapter extends FragmentPagerAdapter {
        private List mainFragment = new ArrayList<>();
        private List mainPageTitle = new ArrayList<>();

        public MainViewPageAdapter(FragmentManager fm, List<Fragment> mainFragment) {
            super(fm);
            this.mainFragment = mainFragment;
        }

        public void addFragmentPage(Fragment frag, String title) {
            mainFragment.add(frag);
            mainPageTitle.add(title);
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return (android.support.v4.app.Fragment) mainFragment.get(position);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return (CharSequence) mainPageTitle.get(position);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Main", "onResume");

        TabLayout mainTabs = findViewById(R.id.main_tabs);
        ViewPager mainPage = findViewById(R.id.main_page);

        mainTabs.setupWithViewPager(mainPage); //TabLayout과 ViewPager 연결
        setUpViewPager(mainPage);//ViewPager에 TabLayout 셋업
//        switchFragment(1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("Main", "onStart");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Main", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Main", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Main", "onDestroy");
    }
}
