package com.example.joanne.selfinsns_;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joanne.selfinsns_.retrofit.model.UserInfo;
import com.example.joanne.selfinsns_.retrofit.remote.ApiUtils;
import com.example.joanne.selfinsns_.retrofit.remote.APIService;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;




/*
  1. 클래스명 : 로그인
  2. 설명 : 유저가 입력한 회원정보를 이용한 가입여부 및 유효성 체크
  3. 기능 흐름 :
   (1) 입력한 이메일, 비밀번호의 유효성을 체크한다.
   (2) 입력한 이메일, 비밀번호가 DB에 존재하는지 확인하고, 결과를 클라이언트에 전달한다.
    (2-1) 정보가 존재할 경우, 해당 회원정보를 클라이언트에 전달한다.
    (2-2) 정보가 존재하지 않을 경우, “존재하지 않는 회원”이라는 메시지를 클라이언트에 전달한다.
   (3) 서버 응답 값에 따라 화면을 이동시킨다.
    (3-1) “이미 가입한 회원”일 경우, 회원가입 화면에 머무른다.
    (3-2) 회원정보일 경우, 해당 정보를 셰어드프리퍼런스에 저장 후, 메인화면으로 이동한다.
 */


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText eEmail, ePwd;//이메일,비밀번호 입력상자

    private CheckBox cKeepLogin;//로그인유지 체크
    private TextView tKeepLogin, bJoin;//로그인유지 텍스트

    private Button bLogin;//로그인 버튼

    private String gEmail, gPwd;
    private String jEmail;//가입후, response된 email이자 로그인 페이지로 intent할 email정보
    private APIService service;
    private SharedPreferences sp,sp1;

    private Boolean fromJoin = false;//가입 직후 넘어왔는지 여부

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        //이메일,비밀번호 입력상자 정의
        eEmail = findViewById(R.id.login_edit_email);
        ePwd = findViewById(R.id.login_edit_pwd);

        //로그인 유지 체크, 텍스트 정의
        cKeepLogin = findViewById(R.id.chk_keep);
        tKeepLogin = findViewById(R.id.login_keep);

        //로그인 버튼 정의
        bLogin = (Button)findViewById(R.id.btn_login);
        bJoin = findViewById(R.id.txt_join);

        //retrofit연결
        service = ApiUtils.getAPIService();
        //SharedPreference 객체생성
        sp = getSharedPreferences("login_user", Activity.MODE_PRIVATE);


        //회원가입 직 후 넘어온 경우 'email' 자동셋팅
        fromJoin = getIntent().getBooleanExtra("fromJoin", false);
        gEmail = getIntent().getStringExtra("email");

        if (fromJoin) { //fromJoin=true 일 경우,
            eEmail.setText(gEmail);
        }

        //스타일북에서 날아온 경우 스타일북으로 미리 이동 되어있게.. 못하나?;



        bLogin.setOnClickListener(this);
        bJoin.setOnClickListener(this);


        /* 자동 로그인 : '로그인유지'를 체크했을 경우, 앱 실행시 바로 main 화면으로 넘어간다.
           1. 체크 했을 경우, sharedpreference에 'auto_login' 값을 true 로 저장한다 (true or false)
           2. 이후 앱을 실행시켰 때, sharedpreference '자동로그인' 값이 true 일 경우, 바로 main화면으로 넘어간다.
           3. main화면에서 'logout'할 경우, sharedpreference 'auto_login'값을 false로 변경한다.
        */

        Boolean auto_login = sp.getBoolean( "auto_login",false );
        if(auto_login){
            Intent main = new Intent(LoginActivity.this, MainActivity.class);
            main.setAction(Intent.ACTION_VIEW);
            startActivity(main);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:

                  /* 로그인 기능
                    1. 사용자에게 입력받은 email,pwd정보를 필드에 저장한다.
                    2. 회원가입 버튼을 tab시, 해당정보를 서버에 post한다.
                    3. response 값을 변수에 저장한다.
                  */
                gEmail = eEmail.getText().toString().trim();
                gPwd = ePwd.getText().toString().trim();

                Log.e("email", gEmail);
                Log.e("pwd", gPwd);

                    Call<UserInfo> call = service.login(gEmail, gPwd);
                    call.enqueue(new Callback<UserInfo>() {
                        @Override
                        public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                            UserInfo lu = response.body();
                            Log.e("연결상태", "성공");
                            Integer uNo = lu.getNo();
                            String uName = lu.getName();
                            String uEmail = lu.getEmail();
                            String uPwd = lu.getPw();
                            String uPic = lu.getPicture();
                            String uGender = lu.getGender();
                            String uAgeArange = lu.getAgeRange();
                            Integer uLevel = lu.getLevel();
                            String uDate = lu.getRegdate();
                            String uType = lu.getType();
                            Log.e("번호", String.valueOf(uNo));
                            Log.e("이름", uName);
                            Log.e("이메일", uEmail);
                            Log.e("비밀번호", uPwd);
                            Log.e("이미지", uPic);
                            Log.e("성별", uGender);
                            Log.e("연령대", uAgeArange);
                            Log.e("레벨", String.valueOf(uLevel));
                            Log.e("가입일", uDate);
                            Log.e("가입종류", uType);

                            //로그인유저정보 SharedPreference저장(key:"login_user")
                            Gson gson = new Gson();
                            String login_info = gson.toJson(lu);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("login", login_info);
                            editor.commit();
                            Log.e("shared_login", login_info);


                            //shared "auto_login"값 "true"로 저장.
                            if(cKeepLogin.isChecked()){
                                Boolean auto_login = true;
                                editor.putBoolean("auto_login",auto_login);
                                editor.commit();
                                Log.e("shared_auto", String.valueOf(auto_login));
                            }

                            //메인화면으로 이동
                            Intent main = new Intent(LoginActivity.this, MainActivity.class);
                            main.setAction(Intent.ACTION_VIEW);
                            startActivity(main);

                        }

                        @Override
                        public void onFailure(Call<UserInfo> call, Throwable t) {
                            Log.e("연결상태", "실패");
                            Log.e("에러코드", t.getMessage());
                        }
                    });


                break;

            case R.id.txt_join:
                Intent join = new Intent(LoginActivity.this, JoinActivity.class);
                join.setAction(Intent.ACTION_VIEW);
                startActivity(join);

                break;

//            case R.id.btn_facebook:
//
//                break;
//            case R.id.btn_google:
//
//                break;

        }
    }
}
