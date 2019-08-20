package com.example.joanne.selfinsns_;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.joanne.selfinsns_.retrofit.model.KnowHowItem;
import com.example.joanne.selfinsns_.retrofit.model.StyleBookItem;
import com.example.joanne.selfinsns_.retrofit.model.UserInfo;
import com.example.joanne.selfinsns_.retrofit.remote.APIService;
import com.example.joanne.selfinsns_.retrofit.remote.API_Result;
import com.example.joanne.selfinsns_.retrofit.remote.ApiUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KnowHowDetailActivity extends AppCompatActivity implements View.OnClickListener{


    private TextView setSubject, setTags, setSection, setSpace, setStyle, setDesc;
    private RecyclerView recyclerView_images;
    private ImageView setUImage,setComment;
    private TextView setUName, setUGender, setUAgeRange, setUEmail;
    private Button btnFollow,btnChat;
    private CheckBox btnLike;
    private TextView setLikeCount, setRegdate,setCommentCount;

    private String strSubject, strTags, strSection, strSpace, strStyle, strDexc,strRegdate;
    private String strUimage;
    private String strUName, strUGender, strUAgeRange, strUEmail;
    private Integer strLikeCount, strCommentCount;

    private Integer kh_id, loginUser_no,writer_no;


    private KnowHowItem item;
    private HashTagHelper mTextHashTagHelper;//해시태그 API

    private UserInfo user,lu;
    Integer uNo,uLevel,order;
    String uEmail,uGender,uAgeArange,uName,uPwd,uPic,uDate,uType;
    private SharedPreferences sp,sp1,sp2,sp3;//로그인유저정보,글정보,작성자정보,이미지들
    private APIService service;

    private KnowHowDetailAdapter_Images knowHowDetailAdapter_images;
    private KnowHowItem item_image;
    private List<KnowHowItem> iList;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.knowhow_detail);




        sp1  = getSharedPreferences("kh_detail", Activity.MODE_PRIVATE); //SharedPreference 객체 생성
        String kh_item = sp1.getString( "kh_item",null );
        Log.e("detail_shared_item", String.valueOf(kh_item));
        Gson gson = new Gson();
        Type type2 = new TypeToken<KnowHowItem>(){}.getType();
        item = gson.fromJson( kh_item,type2 );
        Log.e("detail_shared_item", String.valueOf(item));

        service = ApiUtils.getAPIService(); // retrofit 빌드 생성
        iList = new ArrayList<>();

        Data();

        sp = getSharedPreferences("login_user", Activity.MODE_PRIVATE); //SharedPreference 객체 생성

        String json1 = sp.getString("login", null);
        Type type3 = new TypeToken<UserInfo>() {
        }.getType();
        user = gson.fromJson(json1, type3);
        loginUser_no = user.getNo();
        Log.e("로그인유저:", String.valueOf(user.getNo()));
        Log.e("로그인유저:", String.valueOf(user.getName()));

//        sp3 = getSharedPreferences("kh_detail_image", Activity.MODE_PRIVATE);
//        String json = sp3.getString("kh_item_image", null);
//        Gson gson = new Gson();
//        Type type = new TypeToken<List<KnowHowItem>>() {
//        }.getType();
//        iList = gson.fromJson(json, type);

        kh_id = item.getId();
        strSubject = item.getSubject();
        strSection = item.getSection();
        strStyle = item.getStyle();
        strSpace = item.getSpace();
        strDexc = item.getDesc();
        strTags = item.getTag();
        writer_no = item.getWriterNo();
        strUimage = item.getWriter_image();
        strUName = item.getWriter();
        strRegdate = item.getRegdate();
        strLikeCount = item.getLike_count();
        strCommentCount = item.getComment_count();
        Log.e("kh_shared", String.valueOf(kh_id));
        Log.e("kh_shared",strSubject);
        Log.e("kh_shared",strSection);
        Log.e("kh_shared",strStyle);
        Log.e("kh_shared",strSpace);
        Log.e("kh_shared",strDexc);
        Log.e("kh_shared",strTags);
        Log.e("kh_shared",strUimage);
        Log.e("kh_shared", String.valueOf(writer_no));
        Log.e("kh_shared",strUName);
        Log.e("kh_shared",strRegdate);
        Log.e("kh_shared", String.valueOf(strLikeCount));
        Log.e("kh_shared", String.valueOf(strCommentCount));

        //작성자 정보를 서버로 부터 불러온 후, 셰어드 프리퍼런스("user_info")에 저장한다.
        //로그인 유저가 아니라는 것도 셰어드 프리퍼런스("who")에 저장한다.
        //그리고 마이페이지로 이동.



        setSubject = findViewById(R.id.kh_detail_subject);
        setTags = findViewById(R.id.kh_detail_tags);
        setSection = findViewById(R.id.kh_detail_section);
        setSpace = findViewById(R.id.kh_detail_space);
        setStyle = findViewById(R.id.kh_detail_style);
        setDesc = findViewById(R.id.kh_detail_desc);
        recyclerView_images = findViewById(R.id.knowhowdetail_body);
        setDesc = findViewById(R.id.kh_detail_desc);
        setUImage = findViewById(R.id.kh_detail_uimage);
        setUName = findViewById(R.id.kh_detail_user_name);
        setUGender = findViewById(R.id.kh_detail_user_gender);
        setUAgeRange = findViewById(R.id.kh_detail_user_age_range);
        setRegdate = findViewById(R.id.kh_detail_regdate);
        setUEmail = findViewById(R.id.kh_detail_user_email);
        btnFollow = findViewById(R.id.kh_detail_btn_folling);
        btnChat = findViewById(R.id.kh_detail_btn_chat);
        btnLike = findViewById(R.id.kh_detail_btn_like);
        setLikeCount = (TextView)findViewById(R.id.detail_kh_like);
        setComment = findViewById(R.id.kh_detail_comments);
        setCommentCount = findViewById(R.id.kh_detail_comments_count);
        setSubject = findViewById(R.id.kh_detail_regdate);
        btnFollow.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        btnLike.setOnClickListener(this);
        setComment.setOnClickListener(this);


        Glide.with(this)//작성자 이미지
                .load("http://13.209.108.67/uploads/" + strUimage)
//                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(setUImage);
        setUImage.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT >= 21) {
            setUImage.setClipToOutline(true);
        }

        setSubject.setText(strSubject);
        setSection.setText(strSection);
        setStyle.setText(strStyle);
        setDesc.setText(strDexc);
        setTags.setText(strTags);
        setUName.setText(strUName);
        setUEmail.setText(uEmail);
//        Log.e("이메일",uEmail);
        setUAgeRange.setText(uAgeArange);
//        Log.e("이메일",uAgeArange);
        setUGender.setText(uGender);
//        Log.e("이메일",uGender);
        setRegdate.setText(strRegdate);
        setLikeCount.setText(strLikeCount.toString());
        setCommentCount.setText(strCommentCount.toString());
//        if(like_shape.equals("true")){  //채워진 하트인지 아닌지 여부
//            bLike.setChecked(true);
//        }else{
//            bLike.setChecked(false);
//        }
        //글 보기



        //작성자 정보 보기
        setUImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //마이페이지에서 로그인유저정보가 아닌, 작성자 정보를 보여줘야 한다는 표식
                sp2 = getSharedPreferences("who", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp2.edit();

                //로그인유저정보 SharedPreference저장(key:"login_user")
                Gson gson = new Gson();
                String user_info = gson.toJson(lu);
                editor.putString("writer_user", user_info); //회원정보 저장.
                editor.putString("who", "writer"); //로그인유저정보가 아니라고 저장.
                editor.commit();

                //작성자정보 보기 페이지로 이동
                Intent toMypage = new Intent(KnowHowDetailActivity.this, MyPageActivity.class);
                toMypage.setAction(Intent.ACTION_VIEW);
                startActivity(toMypage);



            }
        });

        //해시태그 선택시 검색페이지로이동 -> 키워드를 인텐트로 보내서 키워드 관련 게시글이 보여지도록 한다.
        mTextHashTagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.colorHashTag), new HashTagHelper.OnHashTagClickListener() {
            @Override
            public void onHashTagClicked(String hashTag) {
                Log.e("해쉬태그", hashTag);
                Intent toHash = new Intent(KnowHowDetailActivity.this, SearchActivity.class);
                toHash.putExtra("hashTag", hashTag);
                Log.e("디테일샷.hashTag", hashTag);
                startActivity(toHash);
                finish();
            }
        });
        mTextHashTagHelper.handle(setTags);//태그 적용


    }

    private void Data() {


//        Log.e("이미지정보:", String.valueOf(iList.get(0).getImage()));
//        Log.e("이미지정보:", String.valueOf(iList.get(0).getImage_desc()));

//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), iList.size(), GridLayoutManager.HORIZONTAL,false);
//        knowHowDetailAdapter_images = new KnowHowDetailAdapter_Images(KnowHowDetailActivity.this, iList);
//        recyclerView_images.setAdapter(knowHowDetailAdapter_images);


        //이 글의 id가 포함된 이미지,이미지설명리스트를 order 순서대로 가져온다.
//        Call<API_Result> call = service.kh_view_image_count(kh_id);
//        call.enqueue(new Callback<API_Result>() {
//            @Override
//            public void onResponse(Call<API_Result> call, Response<API_Result> response) {
//                order = Integer.valueOf(response.body().getMessage());
//                Log.e("order", String.valueOf(order));
//
//                for(int i = 0; i<order; i++){
//
//                    Call<KnowHowItem> call_images = service.kh_view_images(kh_id,i);
//                    final int finalI = i;
//                    call_images.enqueue(new Callback<KnowHowItem>() {
//                        @Override
//                        public void onResponse(Call<KnowHowItem> call, Response<KnowHowItem> response) {
//                            KnowHowItem item = response.body();
//                            iList.add(item);
//                            Log.e("ilist", String.valueOf(item.getImage()));
//                            Log.e("ilist", String.valueOf(item.getImage_desc()));
//
//                            if(order==finalI-1){
//                                GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), iList.size(), GridLayoutManager.HORIZONTAL,false);
//                                knowHowDetailAdapter_images = new KnowHowDetailAdapter_Images(KnowHowDetailActivity.this, iList);
//                                recyclerView_images.setAdapter(knowHowDetailAdapter_images);
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<KnowHowItem> call, Throwable t) {
//
//                        }
//                    });
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<API_Result> call, Throwable t) {
//
//            }
//        });


    }





    @Override
    public void onClick(View v) {

    }
}
