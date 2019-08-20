package com.example.joanne.selfinsns_;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.resource.bitmap.CircleCrop;
//import com.bumptech.glide.request.RequestOptions;
import com.example.joanne.selfinsns_.retrofit.model.CommentItem;
import com.example.joanne.selfinsns_.retrofit.model.StyleBookItem;
import com.example.joanne.selfinsns_.retrofit.model.UserInfo;
import com.example.joanne.selfinsns_.retrofit.remote.APIService;
import com.example.joanne.selfinsns_.retrofit.remote.API_Result;
import com.example.joanne.selfinsns_.retrofit.remote.ApiUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 1. 클래스명: 스타일북 상세페이지
 * 2. 설명: 해당 글과 관련된 작성자, 댓글, 좋아요에 대한 정보를 확인할 수 있다.
 * 3. 기능흐름 :
 * 1) 정보 셋팅하기.
 * 1-1) (클라)StyleBook.class로 부터 받아온 '글ID'를 서버로 보낸다.
 * 1-2) (서버)'글ID'에 해당하는 글 정보(이미지,작성자,내용,댓글수,좋아요수,등록일자,위치정보)를 클라이언트로 전달한다.
 * 1-3) (클라) 서버로부터 받아온 글 정보를 각 위치에 셋팅한다.
 * 2) 글 수정하기.
 * 2-1) 글의 내용을 수정하기 페이지로 보낸다.(intent)
 * 2-2) 이 창을 닫는다 (이후 수정절차는 글 수정페이지에서 이루어짐)
 * 3) 글 삭제하기.
 * 3-1) (클라)'이 글을 삭제하시겠습니까?' 라는 팝업창이 뜬다.
 * 3-2) (클라)'예'를 탭하면, 해당 글id가 서버로 넘어간다.
 * 3-3) (서버)글이 삭제되고, 성공 메세지가 날아온다.
 * 3-4) (클라)마이페이지에서 넘어왔을 경우 마이페이지, 메인에서 넘어왔을 경우 메인페이지로 이동한다.
 */

public class StyleBookDetailActivity extends AppCompatActivity implements View.OnClickListener {

    //화면 셋팅정보
    private CheckBox bLike;
    private ImageView sImage, sWriterImage, bComments;
    private ImageView btnMore;
    private TextView sWriter, sLocation, sTags, sLike, sComments, sRegdate;


    //StyleBookList로부터 받아온 정보
    private Integer sb_no, writer_no, loginUser_no,like_count;
    private String image, content, location, location_lat, location_lng, regdate, writer_name, writer_image, comment_count, like_shape;
    final Context context = this;

    private UserInfo user;
    private SharedPreferences sp, sp1,sp2,sp3;//어떤스크린?,로그인유저?,스타일북아이템,마이페이지인지아닌지,
    private APIService service;


    //어떤화면에서 넘어왔니?
    String whatScreen;


    private HashTagHelper mTextHashTagHelper;//해시태그 API

    private StyleBookItem item;//글 불러오기할때..
    private int flag = 0;



    @SuppressLint({"WrongViewCast", "LongLogTag"})
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stylebook_detail_activity);
        Log.e("StyleBookDetail", "onCreate");

        service = ApiUtils.getAPIService(); // retrofit 빌드 생성

        sp1 = getSharedPreferences("what_screen", Activity.MODE_PRIVATE);//메인 or 마이페이지? 어디서 넘어왔는지. 수정/삭제시 넘어온 곳으로 다시 보내줘야하기 때문에 필요.
        whatScreen = sp1.getString("what", null);

        sp = getSharedPreferences("login_user", Activity.MODE_PRIVATE); //SharedPreference 객체 생성
        Gson gson = new Gson();
        String json = sp.getString("login", null);
        Type type = new TypeToken<UserInfo>() {
        }.getType();
        user = gson.fromJson(json, type);
        loginUser_no = user.getNo();
        Log.e("로그인유저:", String.valueOf(user.getNo()));
        Log.e("로그인유저:", String.valueOf(user.getName()));

        /* 요소 변수 선언*/
        sImage = findViewById(R.id.sb_image);
        sWriterImage = findViewById(R.id.sb_uimage);
        sWriter = findViewById(R.id.sb_uname);
        sTags = findViewById(R.id.sb_tags);
        sLocation = findViewById(R.id.sb_location);
        bComments = (ImageView) findViewById(R.id.comments);
        sComments = (TextView) findViewById(R.id.sb_comments);
        sLike = (TextView) findViewById(R.id.detail_sb_like);
        bLike = findViewById(R.id.sb_btn_like);
        sRegdate = findViewById(R.id.sb_regdate);
        btnMore = findViewById(R.id.btn_edit_delete);
        sLike.setOnClickListener(this);
        bLike.setOnClickListener(this);
        btnMore.setOnClickListener(this);
        bComments.setOnClickListener(this);

        /* 상세정보 가져오기*/
//        Intent fromSbAdapter = getIntent();
//        like_count = fromSbAdapter.getIntExtra("count",0);
//        sLike.setText(like_count.toString());//좋아요수

        sp2 = getSharedPreferences("sb_detail", Activity.MODE_PRIVATE); //SharedPreference 객체 생성
        String sb_item = sp2.getString( "sb_item",null );
        Log.e("detail_shared_item", String.valueOf(sb_item));
        Type type2 = new TypeToken<StyleBookItem>(){}.getType();
        item = gson.fromJson( sb_item,type2 );
        Log.e("detail_shared_item", String.valueOf(item));

        sb_no = item.getNo();
        image = item.getImage();
        content = item.getContent();
        location = item.getLocation();
        location_lat = item.getLocation_lat();
        location_lng = item.getLocation_lng();
        regdate = item.getRegdate();
        writer_name = item.getWriter_name();
        writer_image = item.getWriter_image();
        writer_no = item.getWriter_no();
        like_count = item.getLike();
        comment_count = String.valueOf(item.getComment());
        like_shape = item.getLike_shape();
        Log.e("StyleBookDetailActivity", "sb_no:" + String.valueOf(sb_no));
        Log.e("StyleBookDetailActivity", "image:" + image);
        Log.e("StyleBookDetailActivity", "content:" + content);
        Log.e("StyleBookDetailActivity", "location:" + location);
        Log.e("StyleBookDetailActivity", "location_lat:" + location_lat);
        Log.e("StyleBookDetailActivity", "location_lng:" + location_lng);
        Log.e("StyleBookDetailActivity", "regdate:" + regdate);
        Log.e("StyleBookDetailActivity", "writer_name:" + writer_name);
        Log.e("StyleBookDetailActivity", "writer_image:" + writer_image);
        Log.e("StyleBookDetailActivity", "writer_no:" + String.valueOf(writer_no));
        Log.e("StyleBookDetailActivity", "like_count:" + String.valueOf(like_count));
        Log.e("StyleBookDetailActivity", "comment_count:" + String.valueOf(comment_count));
        Log.e("StyleBookDetailActivity", "like_shape:" + like_shape);


        Log.e("writer_no", String.valueOf(writer_no));
        Log.e("loginUser_no", String.valueOf(loginUser_no));
        if (loginUser_no.equals(writer_no)) { // 로그인 유저와 일치하는 경우에만 'more'버튼(수정/삭제)을 보여준다.
            btnMore.setVisibility(View.VISIBLE);
        } else if (!loginUser_no.equals(writer_no)) {
            btnMore.setVisibility(View.GONE);
        }

        /* 화면 요소에 각각 정보 셋팅*/
        Glide.with(this)//이미지
                .load("http://13.209.108.67/uploads/" + image)
                .into(sImage);
        sImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this)//작성자 이미지
                .load("http://13.209.108.67/uploads/" + writer_image)
//                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(sWriterImage);
        sWriterImage.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT >= 21) {
            sWriterImage.setClipToOutline(true);
        }

        sWriter.setText(writer_name);//작성자이름
        sTags.setText(content);//내용
        sLocation.setText(location);//지역
        sComments.setText(comment_count);//코멘트수
        sLike.setText(like_count.toString());//좋아요수
        sRegdate.setText(regdate);//등록일시
        if(like_shape.equals("true")){  //채워진 하트인지 아닌지 여부
            bLike.setChecked(true);
        }else{
            bLike.setChecked(false);
        }

        //작성자 정보 보기
        sWriterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //작성자 정보를 서버로 부터 불러온 후, 셰어드 프리퍼런스("user_info")에 저장한다.
                //로그인 유저가 아니라는 것도 셰어드 프리퍼런스("who")에 저장한다.
                //그리고 마이페이지로 이동.

                Call<UserInfo> call_userInfo = service.member_info(writer_no);
                call_userInfo.enqueue(new Callback<UserInfo>() {
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

                        //마이페이지에서 로그인유저정보가 아닌, 작성자 정보를 보여줘야 한다는 표식
                        sp3 = getSharedPreferences("who", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp3.edit();

                        //로그인유저정보 SharedPreference저장(key:"login_user")
                        Gson gson = new Gson();
                        String user_info = gson.toJson(lu);
                        editor.putString("writer_user", user_info); //회원정보 저장.
                        editor.putString("who", "writer"); //로그인유저정보가 아니라고 저장.
                        editor.commit();

                        //작성자정보 보기 페이지로 이동
                        Intent toMypage = new Intent(StyleBookDetailActivity.this, MyPageActivity.class);
                        toMypage.setAction(Intent.ACTION_VIEW);
                        startActivity(toMypage);

                    }

                    @Override
                    public void onFailure(Call<UserInfo> call, Throwable t) {
                        t.getMessage();
                    }
                });

            }
        });


        //해시태그 선택시 검색페이지로이동 -> 키워드를 인텐트로 보내서 키워드 관련 게시글이 보여지도록 한다.
        mTextHashTagHelper = HashTagHelper.Creator.create(context.getResources().getColor(R.color.colorHashTag), new HashTagHelper.OnHashTagClickListener() {
            @Override
            public void onHashTagClicked(String hashTag) {
                Log.e("해쉬태그", hashTag);
                Intent toHash = new Intent(StyleBookDetailActivity.this, SearchActivity.class);
                toHash.putExtra("hashTag", hashTag);
                Log.e("디테일샷.hashTag", hashTag);
                startActivity(toHash);
                finish();
            }
        });

        mTextHashTagHelper.handle(sTags);//태그 적용
        bLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //좋아요부분 다시.
                if (bLike.isChecked()) { //좋아요 등록
                    //서버 전송 - 좋아요등록
                    Call<Integer> call_like = service.sb_like(sb_no, loginUser_no,writer_no);
                    call_like.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            like_count = response.body();
                            Log.e("response.body", String.valueOf(like_count));


                            sLike.setText(like_count.toString());//셋팅
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            Log.e("좋아요 등록 실패", t.getMessage());
                            Toast.makeText(StyleBookDetailActivity.this, "좋아요 등록 실패", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else { //좋아요 취소
                    Log.e("눌리긴하니?", "좋아요버튼2");

                    //서버 전송 - 좋아요삭제
                    Call<Integer> call_unlike = service.sb_unlike(sb_no, loginUser_no);
                    call_unlike.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            like_count = response.body();
                            Log.e("response.body", String.valueOf(like_count));

                            sLike.setText(like_count.toString());//셋팅
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            Log.e("좋아요취소실패", t.getMessage());
                            Toast.makeText(StyleBookDetailActivity.this, "좋아요 취소 실패", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });





    }


    /*
    <더보기 버튼 설명>
    버튼 탭시, 수정/삭제 여부선택 다이아로그가 보여짐.
    수정 선택시 -> 수정(등록) 화면으로 이동
    삭제 선택시 -> 해당 글이 삭제 처리 되고, 원래 있던 곳(메인 or 마이페이지)로 이동.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_edit_delete:
                final CharSequence[] items = {"글 수정하기", "글 삭제하기"};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                alertDialogBuilder.setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (items[id].equals("글 수정하기")) {
                                    /*수정하기 위해,수정화면으로 글 내용요소 전달*/
                                    Intent toEdit = new Intent(StyleBookDetailActivity.this, StyleBookRegister.class);
                                    toEdit.putExtra("fromEdit", true);
                                    toEdit.putExtra("sb_no", sb_no);
                                    toEdit.putExtra("image", image);
                                    toEdit.putExtra("content", content);
                                    toEdit.putExtra("location", location);
                                    toEdit.putExtra("location_lat", location_lat);
                                    toEdit.putExtra("location_lng", location_lng);
                                    toEdit.putExtra("writer_no", writer_no);
                                    Log.e("sb_no", String.valueOf(sb_no));
                                    Log.e("image", image);
                                    Log.e("content", content);
                                    Log.e("location", location);
                                    Log.e("location_lat", location_lat);
                                    Log.e("location_lng", location_lng);
                                    Log.e("writer_no", String.valueOf(writer_no));
                                    startActivity(toEdit);
                                    finish();
                                } else if (items[id].equals("글 삭제하기")) {
                                    Dialog();//삭제확인 팝업.
                                }
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;

            case R.id.comments:
                Intent toComment = new Intent(StyleBookDetailActivity.this, CommentsActivity.class);
                toComment.putExtra("writer_no", writer_no);
                toComment.putExtra("writer_image", writer_image);
//                toComment.putExtra("writer_id",writer_name);
                toComment.putExtra("sb_id", sb_no);
                Log.e("writer_image", String.valueOf(writer_image));
//                Log.e("writer_id", String.valueOf(writer_name));
                Log.e("writer_no", String.valueOf(writer_no));
                Log.e("sb_id", String.valueOf(sb_no));
                startActivity(toComment);

                break;

            case R.id.detail_sb_like:
                //좋아요리스트로 글id와 로그인유저no를 넘겨준다.
                Intent toLikeList = new Intent(StyleBookDetailActivity.this, LikeListActivity.class);
                toLikeList.putExtra("sb_no",sb_no);
                toLikeList.putExtra("user_no",loginUser_no);

                Log.e("디테일", String.valueOf(loginUser_no));
                startActivity(toLikeList);
                break;
        }
    }

    public void Dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("글 삭제하기");
        builder.setMessage("정말 삭제하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //삭제처리
                        Call<API_Result> call_edit = service.sb_delete(String.valueOf(sb_no));
                        call_edit.enqueue(new Callback<API_Result>() {
                            @Override
                            public void onResponse(Call<API_Result> call, Response<API_Result> response) {
                                response.message();
                                Log.e("response.body", String.valueOf(response.message()));
                                onBackPressed();
//                                Log.e("whatScreen",whatScreen);
//                                if(whatScreen.equals("main")){
//                                    Intent toMain = new Intent(StyleBookDetailActivity.this,MainActivity.class);
//                                    startActivity(toMain);
//                                    finish();
//                                }else if(whatScreen.equals("mypage")){
//                                    Intent toMypage = new Intent(StyleBookDetailActivity.this,MyPageActivity.class);
//                                    startActivity(toMypage);
//                                    finish();
//                                }
                            }

                            @Override
                            public void onFailure(Call<API_Result> call, Throwable t) {
                                Log.e("글수정", t.getMessage());
                                Toast.makeText(StyleBookDetailActivity.this, "정보를 다시 한 번 확인해주세요.!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Toast.makeText(getApplicationContext(), "해당 글이 삭제되었습니다.", Toast.LENGTH_LONG).show();
                    }
                });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //취소: 다이아로그 종료
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("StyleBookDetail", "onRestart");

        Log.e("퍼즈 sb_no", String.valueOf(sb_no));
        Call<StyleBookItem> call = service.sb_view_sub(sb_no,loginUser_no);
        call.enqueue(new Callback<StyleBookItem>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<StyleBookItem> call, Response<StyleBookItem> response) {

                item = response.body();
                Log.e("no", String.valueOf(item.getNo()));
                Log.d("image", item.getImage());
                Log.d("content", item.getContent());
                Log.d("location", item.getLocation());
                Log.d("location_lat", item.getLocation_lat());
                Log.d("location_lng", item.getLocation_lng());
                Log.d("regdate", item.getRegdate());
                Log.d("writer_no", String.valueOf(item.getWriter_no()));
                Log.d("writer_name", item.getWriter_name());
                Log.d("writer_image", item.getWriter_image());
                Log.d("like", String.valueOf(item.getLike()));
                Log.d("comment", String.valueOf(item.getComment()));
                Log.d("like_shape", String.valueOf(item.getLike_shape()));

                sb_no = item.getNo();
                image = item.getImage();
                content = item.getContent();
                location = item.getLocation();
                location_lat = item.getLocation_lat();
                location_lng = item.getLocation_lng();
                regdate = item.getRegdate();
                writer_name = item.getWriter_name();
                writer_image = item.getWriter_image();
                writer_no = item.getWriter_no();
                like_count =item.getLike();
                comment_count = String.valueOf(item.getComment());
                like_shape = item.getLike_shape();

                /* 화면 요소에 각각 정보 셋팅*/
                Glide.with(getApplicationContext())//이미지
                        .load("http://13.209.108.67/uploads/" + image)
                        .into(sImage);
                sImage.setBackground(new ShapeDrawable(new OvalShape()));
                if(Build.VERSION.SDK_INT >= 21) {
                    sImage.setClipToOutline(true);
                }
                sImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(getApplicationContext())//작성자 이미지
                        .load("http://13.209.108.67/uploads/" + writer_image)
                        .into(sWriterImage);
                if (Build.VERSION.SDK_INT >= 21) {
                    sWriterImage.setClipToOutline(true);
                }
                sWriterImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                sWriterImage.setBackground(new ShapeDrawable(new OvalShape()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    sWriterImage.setBackground(new ShapeDrawable(new OvalShape()));
                }
                sWriter.setText(writer_name);//작성자이름
                sTags.setText(content);//내용
                sLocation.setText(location);//지역
                sComments.setText(comment_count);//코멘트수
                sLike.setText(String.valueOf(like_count));//좋아요수
                sRegdate.setText(regdate);//등록일시
                if(like_shape.equals("true")){//좋아요 - 사용자의 좋아요 체크 여부에 따라 채워진하트 or 빈하트
                    bLike.setChecked(true);
                }else{
                    bLike.setChecked(false);
                }

                //해시태그 선택시 검색페이지로이동 -> 키워드를 인텐트로 보내서 키워드 관련 게시글이 보여지도록 한다.
                mTextHashTagHelper = HashTagHelper.Creator.create(context.getResources().getColor(R.color.colorHashTag), new HashTagHelper.OnHashTagClickListener() {
                    @Override
                    public void onHashTagClicked(String hashTag) {
                        Log.e("해쉬태그", hashTag);
                        Intent toHash = new Intent(StyleBookDetailActivity.this, SearchActivity.class);
                        toHash.putExtra("hashTag", hashTag);
                        Log.e("디테일샷.hashTag", hashTag);
                        startActivity(toHash);
                        finish();
                    }
                });
                mTextHashTagHelper.handle(sTags);//태그 적용

            }

            @Override
            public void onFailure(Call<StyleBookItem> call, Throwable t) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("StyleBookDetail", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("StyleBookDetail", "onResume");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.e("StyleBookDetail", "onStop");
        sp3 = getSharedPreferences("who", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp3.edit();
        editor.putString("who", "loginUser"); //로그인유저정보가 아니라고 저장.
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("StyleBookDetail", "onDestroy");

        sp3 = getSharedPreferences("who", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp3.edit();
        editor.putString("who", "loginUser"); //로그인유저정보가 아니라고 저장.
        editor.commit();
    }


}
