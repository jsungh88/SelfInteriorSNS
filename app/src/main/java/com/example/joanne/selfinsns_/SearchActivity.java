package com.example.joanne.selfinsns_;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joanne.selfinsns_.retrofit.model.StyleBookItem;
import com.example.joanne.selfinsns_.retrofit.model.UserInfo;
import com.example.joanne.selfinsns_.retrofit.remote.APIService;
import com.example.joanne.selfinsns_.retrofit.remote.ApiUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener{

    //화면구성요소
    private Button btnSearch;
    private EditText eSearch;
    private ImageView iconSearch;
    private TextView noResult;

    private String hashTag=null;
    private APIService service;
    private List<StyleBookItem> list;

    RecyclerView sbSearchRecycler;
    StyleBookAdapter_search styleBookAdapter;

    //로그인사용자정보
    private SharedPreferences sp;
    private UserInfo user;
    private Integer no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stylebook_hashlist);

        //화면구성요소
        btnSearch = (Button)findViewById(R.id.sb_btn_search);
        eSearch = findViewById(R.id.sb_edit_search);
        btnSearch.setOnClickListener(this);
        iconSearch = findViewById(R.id.icon_search);
        noResult = findViewById(R.id.noresult_search);
        iconSearch.setVisibility(View.GONE);//noresult 초기화
        noResult.setVisibility(View.GONE);//noresult 초기화


        Intent fromsbDetail = getIntent();
        hashTag = fromsbDetail.getStringExtra("hashTag");
        try {
            Log.e("hashTag", hashTag);
        }catch(Exception e){
            e.getMessage();
        }
        //hashTag가 넘어온 경우 해시태그문자열을 검색란에 셋팅.
        if(hashTag!=null){
            eSearch.setText(hashTag);
        }

        service = ApiUtils.getAPIService();

        /*
         로그인한 유저 정보 중 사용자'no' 값 가져오기
         - 마이페이지'스타일북'의 경우, 해당 사용자가 올린 글만 보여줘야 하기 때문에 'no' 필요함         *
         */
        sp = getSharedPreferences("login_user", Activity.MODE_PRIVATE); //SharedPreference 객체 생성
        Gson gson = new Gson();
        String json = sp.getString("login", null);
        Type type = new TypeToken<UserInfo>() {
        }.getType();
        user = gson.fromJson(json, type);
        no = user.getNo();
        Log.e("로그인유저:", String.valueOf(user.getNo()));

        sbSearchRecycler = (RecyclerView)findViewById(R.id.stylebook_hashlist);
        sbSearchRecycler.setHasFixedSize(true);
        list= new ArrayList<>(); //data담을 리스트
        if(hashTag!=null){
            Data();
        }

//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), list.size(), GridLayoutManager.HORIZONTAL, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        sbSearchRecycler.setLayoutManager(gridLayoutManager);

        sbSearchRecycler.addOnItemTouchListener(new StyleBookAdapter_main.RecyclerViewOnItemClickListener(this, sbSearchRecycler,
                new StyleBookAdapter_main.RecyclerViewOnItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Intent toDetail = new Intent(SearchActivity.this, StyleBookDetailActivity.class);
                        toDetail.putExtra("sb_no", list.get(position).getNo());
                        toDetail.putExtra("image",list.get(position).getImage());
                        toDetail.putExtra("content",list.get(position).getContent());
                        toDetail.putExtra("location",list.get(position).getLocation());
                        toDetail.putExtra("regdate",list.get(position).getRegdate());
                        toDetail.putExtra("writer_name",list.get(position).getWriter_name());
                        toDetail.putExtra("writer_image",list.get(position).getWriter_image());
                        toDetail.putExtra("writer_no",list.get(position).getWriter_no());
                        toDetail.putExtra("like_count",String.valueOf(list.get(position).getLike()));
                        toDetail.putExtra("comment_count",String.valueOf(list.get(position).getComment()));

                        Log.e("sb_no", String.valueOf(list.get(position).getNo()));
                        Log.e("like_count", String.valueOf(list.get(position).getLike()));
                        Log.e("comment_count", String.valueOf(list.get(position).getComment()));
                        startActivity(toDetail);
//                        onBackPressed();
                    }

                    @Override
                    public void onItemLongClick(View v, int position) {

                    }
                }));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sb_btn_search:
                hashTag = eSearch.getText().toString().trim();//검색값
                Call<List<StyleBookItem>> call = service.sb_tagsview(hashTag,no);
                call.enqueue(new Callback<List<StyleBookItem>>() {
                    @Override
                    public void onResponse(Call<List<StyleBookItem>> call, Response<List<StyleBookItem>> response) {
                        response.message();
                        Log.e("response.body", String.valueOf(response.message()));
                        iconSearch.setVisibility(View.GONE);//noresult 초기화
                        noResult.setVisibility(View.GONE);//noresult 초기화
                        sbSearchRecycler.setVisibility(View.VISIBLE);//검색화면 다시 보이게
                        list = response.body();
                        for (StyleBookItem s : list) {
                            Log.e("no", String.valueOf(s.getNo()));
                            Log.d("image", s.getImage());
                            Log.d("content", s.getContent());
                            Log.d("location", s.getLocation());
                            Log.d("regdate", s.getRegdate());
                            Log.d("writer_name", s.getWriter_name());
                            Log.d("writer_image", s.getWriter_image());
                            Log.d("like", String.valueOf(s.getLike()));
                            Log.d("comment", String.valueOf(s.getComment()));
                        }

                        styleBookAdapter = new StyleBookAdapter_search(SearchActivity.this,list);
                        sbSearchRecycler.setAdapter(styleBookAdapter);

                    }
                    @Override
                    public void onFailure(Call<List<StyleBookItem>> call, Throwable t) {
                        sbSearchRecycler.setVisibility(View.GONE);

                        iconSearch.setVisibility(View.VISIBLE);
                        noResult.setVisibility(View.VISIBLE);
                        noResult.setText("'"+hashTag+"'"+"에 대한 검색결과가 없습니다.");
                        Log.e("검색실패",t.getMessage());
                    }
                });

                break;
        }
    }

    public void Data(){
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Loading data...");
//        progressDialog.show();

        Call<List<StyleBookItem>> call = service.sb_tagsview(hashTag,no);
        call.enqueue(new Callback<List<StyleBookItem>>() {
            @Override
            public void onResponse(Call<List<StyleBookItem>> call, Response<List<StyleBookItem>> response) {
                response.message();
                Log.e("response.body", String.valueOf(response.message()));
                list = response.body();
                for (StyleBookItem s : list) {
                    Log.e("no", String.valueOf(s.getNo()));
                    Log.d("image", s.getImage());
                    Log.d("content", s.getContent());
                    Log.d("location", s.getLocation());
                    Log.d("location_lat", s.getLocation_lat());
                    Log.d("location_lng", s.getLocation_lng());
                    Log.d("regdate", s.getRegdate());
                    Log.d("writer_name", s.getWriter_name());
                    Log.d("writer_image", s.getWriter_image());
                    Log.d("like", String.valueOf(s.getLike()));
                    Log.d("comment", String.valueOf(s.getComment()));
                    Log.d("like_shape", String.valueOf(s.getLike_shape()));
                }

                styleBookAdapter = new StyleBookAdapter_search(SearchActivity.this,list);
                sbSearchRecycler.setAdapter(styleBookAdapter);
//                sbSearchRecycler.setAdapter(new StyleBookAdapter_search(SearchActivity.this,list));

            }
            @Override
            public void onFailure(Call<List<StyleBookItem>> call, Throwable t) {
                Log.e("글수정",t.getMessage());
                Toast.makeText(SearchActivity.this, "정보를 다시 한 번 확인해주세요.!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
