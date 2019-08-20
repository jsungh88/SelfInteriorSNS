package com.example.joanne.selfinsns_;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.Layout;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.joanne.selfinsns_.retrofit.model.UserInfo;
import com.example.joanne.selfinsns_.retrofit.remote.APIService;
import com.example.joanne.selfinsns_.retrofit.remote.ApiUtils;
import com.example.joanne.selfinsns_.retrofit.remote.FileUtils;
import com.example.joanne.selfinsns_.utils.BitmapUtils;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gun0912.tedpicker.util.Util.rotate;

/*
  1. 클래스명 : 회원가입
  2. 설명 : 유저가 입력한 회원정보를 DB에 저장
  3. 기능 흐름 :
    (1) 유저가 입력한 회원정보를 서버로 전달한다.
    (2) 회원 정보가 DB에 존재하는지 확인하고, 결과를 클라이언트에 전달한다.
      (2-1) 정보가 존재할 경우, “이미 가입한 회원”이라는 메시지를 클라이언트에 전달한다.
      (2-2) 정보가 존재하지 않을 경우, 회원 정보를 DB에 저장 후 유저의 ‘email’을 클라이언트에 전달한다.
    (3) 서버 응답 값에 따라 화면을 이동시킨다.
      (3-1) “이미 가입한 회원”일 경우, 회원가입 화면에 머무른다.
      (3-2) ‘email’주소일 경우, 로그인 화면으로 이동한다.
 */

public class JoinActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "JoinActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int REQUEST_IMAGE_ALBUM = 200;
    private static final int REQUEST_IMAGE_CROP = 300;

    private static final int REQUEST_EXTERNAL_STORAGE = 2;
    private static final int REQUEST_CAMERA = 1;

    private String imageFilePath; //카메라이미지 저장경로
    private Uri photoUri, albumURI, imageUri = null; //카메라이미지 저장 URI
    boolean album = false;

    private CheckBox cAgree;
    private Button bJoin,bEdit, btnCamera, btnAlbum;
    private ImageView profile, bClose, bGender, bAge;
    private EditText eEmail, ePwd, eName;
    private TextView service_agree, personal_agree, tAge, tGender, titleJoin;

    private String gName, gEmail, gPwd;
    private String gGender = null;
    private String gAge = null;
//    private Uri imageUri;

    private APIService service;
    private UserInfo lu;
    static private File file = null;

    //편집모드 flag
    private boolean fromMypage = false;
    private boolean fromProfileEdit = false;
    private String editImage;

    private SharedPreferences sp;
    private UserInfo user;

    private Dialog dialog;

    private boolean albumm = false;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_activity);

        service = ApiUtils.getAPIService(); //retrofit 빌드생성

        try {
            Intent fromMp = getIntent();
            fromMypage = fromMp.getBooleanExtra("fromMypage", false);

            Intent fromPe = getIntent();
            fromProfileEdit = fromPe.getBooleanExtra("fromProfileEdit",false);
            Log.e("fromProfileEdit", String.valueOf(fromProfileEdit));
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnCamera = (Button) findViewById(R.id.camera);//카메라버튼
        btnAlbum = (Button) findViewById(R.id.photoAlbum);//앨범버튼

        bClose = findViewById(R.id.join_btn_close); //닫기버튼
        bJoin = (Button) findViewById(R.id.btn_join); //가입버튼
        bEdit = (Button) findViewById(R.id.btn_edit); //수정버튼
        cAgree = (CheckBox) findViewById(R.id.chk_agree); //약관동의
        LinearLayout serviceLayout = findViewById(R.id.service_layout);//서비스약관영역

        titleJoin = findViewById(R.id.join_title); //타이틀
        profile = findViewById(R.id.img_profile); //프로필이미지
        eEmail = findViewById(R.id.edit_email); //이메일입력란
        ePwd = findViewById(R.id.edit_pwd); //비밀번호입력란
        eName = findViewById(R.id.edit_name); //이름입력란

        bAge = findViewById(R.id.btn_age_range); //연령대선택
        tAge = findViewById(R.id.txt_age_range); //연령대표시란
        bGender = findViewById(R.id.btn_gender); //성별선택
        tGender = findViewById(R.id.txt_gender); //성별표시란

        service_agree = findViewById(R.id.service); //서비스 동의
        personal_agree = findViewById(R.id.personal); //개인정보 동의

        if(!fromMypage && !fromProfileEdit){
            //회원가입일 경우
            titleJoin.setText("회원 가입");
            bJoin.setVisibility(View.VISIBLE);
            bEdit.setVisibility(View.GONE);
            serviceLayout.setVisibility(View.VISIBLE);
        }else if(fromProfileEdit && !fromMypage){
            //마이페이지에서 수정왔을 경우
            titleJoin.setText("프로필 편집");
            bJoin.setVisibility(View.GONE);
            bEdit.setVisibility(View.VISIBLE);
            serviceLayout.setVisibility(View.GONE);
            bJoin.setText("수정 완료");

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

            //프로필이미지 셋
            Glide.with(this)
                    .load("http://13.209.108.67/uploads/" + user.getPicture())
                    .into(profile);
            profile.setBackground(new ShapeDrawable(new OvalShape()));
            if (Build.VERSION.SDK_INT >= 21) {
                profile.setClipToOutline(true);
            }
            profile.setScaleType(ImageView.ScaleType.CENTER_CROP);
            eEmail.setText(user.getEmail());
            ePwd.setText(user.getPw());
            ePwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD );
            ePwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            eName.setText(user.getName());
            tAge.setText(user.getAgeRange());
            tGender.setText(user.getGender());
        }else{
            titleJoin.setText("프로필 편집");
            //마이페이지에서 수정왔을 경우
            bJoin.setVisibility(View.GONE);
            bEdit.setVisibility(View.VISIBLE);
            serviceLayout.setVisibility(View.GONE);
            bJoin.setText("수정 완료");

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

            //프로필이미지 셋
            Glide.with(this)
                    .load("http://13.209.108.67/uploads/" + user.getPicture())
                    .into(profile);
            profile.setBackground(new ShapeDrawable(new OvalShape()));
            if (Build.VERSION.SDK_INT >= 21) {
                profile.setClipToOutline(true);
            }
            profile.setScaleType(ImageView.ScaleType.CENTER_CROP);
            eEmail.setText(user.getEmail());
            ePwd.setText(user.getPw());
            ePwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD );
            ePwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            eName.setText(user.getName());
            tAge.setText(user.getAgeRange());
            tGender.setText(user.getGender());
        }

        bClose.setOnClickListener(this);
        bAge.setOnClickListener(this);
        bGender.setOnClickListener(this);
        bJoin.setOnClickListener(this);
        bEdit.setOnClickListener(this);
//        btnAlbum.setOnClickListener(this);
//        btnCamera.setOnClickListener(this);

        /*
        회원정보 입력 기능 - 이름, 이메일, 비밀번호, 성별, 연령대, 프로필 사진
         */

        if(!fromMypage) {
            profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("!fromMypage","!fromMypage");
                    //이미지 주소값 가져오기
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 200);
                }
            });
        }else{
            Log.e("fromMypage","fromMypage");
            profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    //이미지 주소값 가져오기
//                    Intent toOpenCV = new Intent(JoinActivity.this, OpenCVActivity.class);
//                    toOpenCV.setAction(Intent.ACTION_VIEW);
//                    startActivity(toOpenCV);
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    View customLayout = View.inflate(JoinActivity.this, R.layout.cameraoralbum_dialog, null);
                    builder.setView(customLayout);
                    customLayout.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //이미지 주소값 가져오기
                            Intent toOpenCV = new Intent(JoinActivity.this, OpenCVActivity.class);
                            toOpenCV.setAction(Intent.ACTION_VIEW);
                            startActivity(toOpenCV);
                        }
                    });
                    customLayout.findViewById(R.id.photoAlbum).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("포토앨범","뭐지");
                            dialog.dismiss();
                            photoAlbum();
                            photoUri = null;
                        }
                    });

                    dialog = builder.create();
                    dialog.show();

                }
            });
        }

    }//oncreate 끝


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.join_btn_close: //닫기버튼 - login화면으로 이동
                onBackPressed();

                break;
            case R.id.btn_age_range: //연령대 선택 Dialog 띄우기
                age_DialogRadio();
                break;

            case R.id.btn_gender: //성별 선택 Dialog 띄우기
                gender_DialogRadio();
                break;

            case R.id.btn_join:
                /*********************************** 가 입 하 기 ***********************************
                 1. 가입하기 버튼을 탭한다.
                 2. 프로필이미지,이름,이메일,비밀번호,성별,연령대가 모두 입력되었는지 확인한다.
                 3. 모두 입력이 되었으면 프로필이미지,이름,이메일,비밀번호,성별,연령대 변수를 requestbody로 변환한다.
                 4. 서비스 이용약관, 개인정보 취급방침 동의여부 확인 후 서버로 DB 전송한다.
                 5. (서버) 유저정보의 회원정보DB 유무 체크후 없으면 DB에 저장한다.
                 6. (서버) 이메일 정보만 반환한다.
                 7. 이메일주소를 DB에 저장한다.
                 8. LoginActivity로 intent 한다. (가입정보를 미리 이메일주소란에 setting 하기 위함-편리용도)
                 **********************************************************************************/



                gName = eName.getText().toString().trim();
                gEmail = eEmail.getText().toString().trim();
                gPwd = ePwd.getText().toString().trim();
                Log.e("이름", gName);
                Log.e("이메일", gEmail);
                Log.e("패스워드", gPwd);
//                Log.e("성별", gGender);
//                Log.e("연령대", gAge);

                if (gEmail.isEmpty()) {
                    Log.e("이메일", gEmail);
                    Toast.makeText(JoinActivity.this, "이메일주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                if (gPwd.isEmpty()) {
                    Log.e("패스워드", gPwd);
                    Toast.makeText(JoinActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    //비밀번호 유효성
                    if (!Pattern.matches("^(?=.*[a-zA-Z]+)(?=.*[!@#$%^*+=-]|.*[0-9]+).{8,16}$", gPwd)) {
                        Toast.makeText(JoinActivity.this, "영문(대소문자 구분),숫자를 혼용하여 8~16자를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (gName.isEmpty()) {
                    Log.e("이름", gName);
                    Toast.makeText(JoinActivity.this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                if (gAge == null) {
                    Log.e("연령대", "null");
                    Toast.makeText(JoinActivity.this, "성별을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
                if (gGender == null) {
                    Log.e("성별", "null");
                    Toast.makeText(JoinActivity.this, "연령대를 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
                if (file == null) {
                    Toast.makeText(JoinActivity.this, "이미지를 등록해주세요.", Toast.LENGTH_SHORT).show();
                }

//                checkEmail(gEmail);

                try {
                    RequestBody req_name = RequestBody.create(MediaType.parse("text/plane"), gName);
                    RequestBody req_email = RequestBody.create(MediaType.parse("text/plane"), gEmail);
                    RequestBody req_pwd = RequestBody.create(MediaType.parse("text/plane"), gPwd);
                    RequestBody req_gender = RequestBody.create(MediaType.parse("text/plane"), gGender);
                    RequestBody req_age_range = RequestBody.create(MediaType.parse("text/plane"), gAge);
                    RequestBody requestfile = RequestBody.create(MediaType.parse("*/*"), file); //프로필 이미지파일
                    MultipartBody.Part upload_image = MultipartBody.Part.createFormData("picture", file.getName(), requestfile);
                    Log.e("file.getName():", "upload_image:" + file.getName());

                    Log.e("req_name", String.valueOf(req_name));
                    Log.e("req_email", String.valueOf(req_email));
                    Log.e("req_pwd", String.valueOf(req_pwd));
                    Log.e("req_gender", String.valueOf(req_gender));
                    Log.e("req_age_range", String.valueOf(req_age_range));
                    Log.e("upload_image", String.valueOf(upload_image));

//                        gName, gEmail, gPwd, gGender, gAge

                    if (!cAgree.isChecked()) {

                        Toast.makeText(JoinActivity.this, "서비스 이용약관, 개인정보 취급 방침의 동의여부를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        Call<UserInfo> call = service.join(req_name, req_email, req_pwd, req_gender, req_age_range, upload_image);
                        call.enqueue(new Callback<UserInfo>() {
                            @Override
                            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                                response.body();
                                Log.e("response.body", String.valueOf(response.body()));
                                Toast.makeText(JoinActivity.this, "가입을 축하합니다!", Toast.LENGTH_SHORT).show();

                                Intent toLogin = new Intent(JoinActivity.this, LoginActivity.class);
                                toLogin.putExtra("fromJoin", true);
                                toLogin.putExtra("email", response.body().getEmail());
                                startActivity(toLogin);
                                finish();
                            }

                            @Override
                            public void onFailure(Call<UserInfo> call, Throwable t) {
                                t.getMessage();
                                Log.e("에러메시지", t.getMessage());
                                Toast.makeText(JoinActivity.this, "정보를 다시 한 번 확인해주세요.!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e1) {
                    e1.getMessage();
                }
                break;

            case R.id.btn_edit:
                /*
                1. 사용자 정보를 셋팅한다.
                2. 수정할 경우를 대비해 입력하는 값을 셋팅하는 코드도 짠다.
                3. 수정한 정보를 서버로 보낸다.
                4. 서버에서는 수정한 정보를 update한다.
                5. 다시 마이페이지로 이동한다.
                 */
                //이미 ProfileEdit에서 저장했으므로, 서버 패스.
                if(albumm) {
                    RequestBody req_no = RequestBody.create(MediaType.parse("text/plane"), String.valueOf(user.getNo()));
                    RequestBody requestfile = RequestBody.create(MediaType.parse("*/*"), file); //프로필 이미지파일
                    MultipartBody.Part upload_image = MultipartBody.Part.createFormData("picture", file.getName(), requestfile);
                    Log.e("file:", String.valueOf(file));
                    Log.e("file.getName():", file.getName());
                    Log.e("upload_image", String.valueOf(upload_image));
                    Call<UserInfo> call = service.profile_edit(req_no, upload_image);
                    call.enqueue(new Callback<UserInfo>() {
                        @Override
                        public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                            Log.e("response.body", String.valueOf(response.body()));
                            //셰어드저장.
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

                            if (!uName.isEmpty()) {
                                //mypage로 이동
                                Intent toMypage = new Intent(JoinActivity.this, MyPageActivity.class);
                                toMypage.putExtra("fromProfileEdit", true);
                                startActivity(toMypage);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserInfo> call, Throwable t) {

                        }
                    });
                }else{
                    //mypage로 이동
                    Intent toMypage = new Intent(JoinActivity.this, MyPageActivity.class);
                    toMypage.setAction(Intent.ACTION_VIEW);
                    startActivity(toMypage);
                    finish();
                }



//                gName = user.getName();
//                gEmail = user.getEmail();
//                gPwd = user.getPw();
//                gGender = user.getGender();
//                gAge = user.getAgeRange();
//                Log.e("이름", gName);
//                Log.e("이메일", gEmail);
//                Log.e("패스워드", gPwd);
//                Log.e("성별", gGender);
//                Log.e("연령대", gAge);
//
//                if (gEmail.isEmpty()) {
//                    Log.e("이메일", gEmail);
//                    Toast.makeText(JoinActivity.this, "이메일주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
//                }
//                if (gPwd.isEmpty()) {
//                    Log.e("패스워드", gPwd);
//                    Toast.makeText(JoinActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
//                } else {
//                    //비밀번호 유효성
//                    if (!Pattern.matches("^(?=.*[a-zA-Z]+)(?=.*[!@#$%^*+=-]|.*[0-9]+).{8,16}$", gPwd)) {
//                        Toast.makeText(JoinActivity.this, "영문(대소문자 구분),숫자를 혼용하여 8~16자를 입력해주세요.", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                }
//                if (gName.isEmpty()) {
//                    Log.e("이름", gName);
//                    Toast.makeText(JoinActivity.this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
//                }
//                if (gAge == null) {
//                    Log.e("연령대", "null");
//                    Toast.makeText(JoinActivity.this, "성별을 선택해주세요.", Toast.LENGTH_SHORT).show();
//                }
//                if (gGender == null) {
//                    Log.e("성별", "null");
//                    Toast.makeText(JoinActivity.this, "연령대를 선택해주세요.", Toast.LENGTH_SHORT).show();
//                }
//                if (file == null) {
//                    Toast.makeText(JoinActivity.this, "이미지를 등록해주세요.", Toast.LENGTH_SHORT).show();
//                }
//
////                checkEmail(gEmail);
//
//                try {
//                    RequestBody req_no = RequestBody.create(MediaType.parse("text/plane"), String.valueOf(user.getNo()));
//                    RequestBody req_name = RequestBody.create(MediaType.parse("text/plane"), gName);
//                    RequestBody req_email = RequestBody.create(MediaType.parse("text/plane"), gEmail);
//                    RequestBody req_pwd = RequestBody.create(MediaType.parse("text/plane"), gPwd);
//                    RequestBody req_gender = RequestBody.create(MediaType.parse("text/plane"), gGender);
//                    RequestBody req_age_range = RequestBody.create(MediaType.parse("text/plane"), gAge);
//                    RequestBody requestfile = RequestBody.create(MediaType.parse("*/*"), file); //프로필 이미지파일
//                    MultipartBody.Part upload_image = MultipartBody.Part.createFormData("picture", file.getName(), requestfile);
//                    Log.e("file.getName():", "upload_image:" + file.getName());
//
//                    Log.e("req_name", String.valueOf(req_name));
//                    Log.e("req_email", String.valueOf(req_email));
//                    Log.e("req_pwd", String.valueOf(req_pwd));
//                    Log.e("req_gender", String.valueOf(req_gender));
//                    Log.e("req_age_range", String.valueOf(req_age_range));
//                    Log.e("upload_image", String.valueOf(upload_image));
//
////                        gName, gEmail, gPwd, gGender, gAge
//
//
//                        Call<UserInfo> call = service.profile_edit2(req_no, req_name, req_email, req_pwd, req_gender, req_age_range, upload_image);
//                        call.enqueue(new Callback<UserInfo>() {
//                            @Override
//                            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
//                                response.body();
//                                Log.e("response.body", String.valueOf(response.body()));
//                                Toast.makeText(JoinActivity.this, "가입을 축하합니다!", Toast.LENGTH_SHORT).show();
//
//                                Intent toLogin = new Intent(JoinActivity.this, LoginActivity.class);
//                                toLogin.putExtra("fromJoin", true);
//                                toLogin.putExtra("email", response.body().getEmail());
//                                startActivity(toLogin);
//                                finish();
//                            }
//
//                            @Override
//                            public void onFailure(Call<UserInfo> call, Throwable t) {
//                                t.getMessage();
//                                Log.e("에러메시지", t.getMessage());
//                                Toast.makeText(JoinActivity.this, "정보를 다시 한 번 확인해주세요.!", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                } catch (Exception e1) {
//                    e1.getMessage();
//                }
//


                break;
//            case R.id.camera:
//
//                break;
//            case R.id.photoAlbum:
//                Log.e("포토앨범","뭐지");
//                dialog.dismiss();
//                photoAlbum();
//                photoUri = null;
//                break;
        }

    }


    //앨범 이미지 가져오기
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("requestCode", String.valueOf(requestCode));
        if (requestCode == REQUEST_IMAGE_CAPTURE) {

            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            ExifInterface exif = null;

            Log.e("이미지파일패스", imageFilePath);
            Log.e("이미지비트맵", String.valueOf(bitmap));

            try {
                exif = new ExifInterface(imageFilePath);
                Log.e("이미지exif", String.valueOf(exif));
            } catch (IOException e) {
                e.printStackTrace();
            }

            int exifOrientation;
            int exifDegree;

            if (exif != null) {
                Log.e("null아님", "ㅇㅇ");
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = imageRotate(exifOrientation);

                Log.e("exifOrientation", String.valueOf(exifOrientation));
                Log.e("exifOrientation1", String.valueOf(exifDegree));
            } else {
                Log.e("null임", "ㅇㅇ");
                exifDegree = 0;
                Log.e("exifOrientation2", String.valueOf(exifDegree));
            }

            profile.setImageBitmap(rotate(bitmap, exifDegree)); //가져온 이미지 셋팅

        } else if (requestCode == REQUEST_IMAGE_ALBUM) {
            try {
                albumURI = data.getData();
                file = FileUtils.getFile(getApplicationContext(), albumURI);
                profile.setImageURI(albumURI);
                profile.setBackground(new ShapeDrawable(new OvalShape()));
                if (Build.VERSION.SDK_INT >= 21) {
                    profile.setClipToOutline(true);
                }
                profile.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Log.e("albumURI" + "2222", String.valueOf(albumURI));
                Log.e("file" + "2222", String.valueOf(file));
                albumm = true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            imageUri = data.getData();
            file = FileUtils.getFile(this, imageUri);
            profile.setImageURI(imageUri);
            Log.e("imageUri", String.valueOf(imageUri));
            Log.e("file", String.valueOf(file));
            profile.setScaleType(ImageView.ScaleType.CENTER_CROP);
            profile.setBackground(new ShapeDrawable(new OvalShape()));
            if (Build.VERSION.SDK_INT >= 21) {
                profile.setClipToOutline(true);
            }
        }

    }

    //연령대 선택하기
    private void age_DialogRadio() {
        final CharSequence[] arr = {"10대", "20대", "30대", "40대", "50대", "60대", "70대이상"};
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("연령대를 선택하세요.");
        alert.setSingleChoiceItems(arr, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(JoinActivity.this, arr[which], Toast.LENGTH_SHORT).show();

                tAge.setText(arr[which]);
                gAge = (String) arr[which];
                Log.e("연령대", gAge);
                dialog.cancel();
            }
        });
        AlertDialog aalert = alert.create();
        aalert.show();
    }

    //성별 선택하기
    private void gender_DialogRadio() {
        final CharSequence[] arr = {"여성", "남성"};
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("성별을 선택하세요.");
        alert.setSingleChoiceItems(arr, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(JoinActivity.this, arr[which], Toast.LENGTH_SHORT).show();

                tGender.setText(arr[which]);
                gGender = (String) arr[which];
                Log.e("성별", gGender);
                dialog.cancel();
            }
        });
        AlertDialog aalert = alert.create();
        aalert.show();
    }

//    private boolean checkEmail(String email)
//    {
//        String mail = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
//        Pattern p = Pattern.compile(mail);
//        Matcher m = p.matcher(email);
//        return m.matches();
//    }
//

    //앨범에서 가져오기 메소드
    private void photoAlbum() {
        //이미지 주소값 가져오기!!
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 200);

    }

    //이미지 파일을 생성해주는 메소드 - 이미지가 저장"된" 파일이 아닌, 이미지가 저장"될"파일을 만드는 메소드.
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {
            if (grantResults[0] == 0) {
                Toast.makeText(this, "카메라 권한이 승인됨", Toast.LENGTH_SHORT).show();
            } else {
                //권한이 거절된 경우
                Toast.makeText(this, "카메라 권한이 거절되었습니다. 카메라를 이용하려면 권한을 승낙해야 합니다.", Toast.LENGTH_SHORT).show();
            }
        }

        switch (requestCode){
            case REQUEST_EXTERNAL_STORAGE:
                for(int i=0; i <permissions.length; i++){
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if(permission.equals(android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                        if(grantResult == PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"읽기/쓰기 권한이 승인되었습니다. ",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(this,"읽기/쓰기 권한이 승인이 거절되었습니다. ",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case REQUEST_IMAGE_ALBUM:
                for(int i=0; i <permissions.length; i++){
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if(permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        if(grantResult == PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"읽기/쓰기 권한이 승인되었습니다. ",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(this,"읽기/쓰기 권한이 승인이 거절되었습니다. ",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }

    }

    //이미지 방향 정의 메소드
    private int imageRotate(int orientation) {
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        } else {
            return 0;
        }
    }

    //이미지 회전 메소드
    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    //이미지 크롭
    private void cropImage() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setDataAndType(photoUri, "image/*");
        cropIntent.putExtra("scale", true);

        if (album == false) {
            cropIntent.putExtra("output", photoUri); // 크롭 이미지를 해당 경로에 저장
        } else if (album == true) {
            cropIntent.putExtra("output", albumURI);
        }

        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);

    }
}
