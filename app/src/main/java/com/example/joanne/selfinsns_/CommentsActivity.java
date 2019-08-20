package com.example.joanne.selfinsns_;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.joanne.selfinsns_.retrofit.model.CommentItem;
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

public class CommentsActivity extends AppCompatActivity implements View.OnClickListener {

    //댓글 리사이클러뷰
    private List<CommentItem> clist;//아이템리스트.
    private RecyclerView cRecyclerView;
    private CommentAdapter cAdapter;
    private CommentItem itme;

    //화면구성요소
    private ImageView setWriterImage;//작성자이미지
    private EditText setComments;//댓글입력란
    private TextView setbtnComments;//게시버튼

    //상세화면에서 가져오는 요소
    private Integer sb_no, writer_no;
    private String writer_image;

    //댓글입력
    private String insertComments;

    //서버
    private APIService service;
    private UserInfo user;

    //대댓글 달 때, 댓글 정보 가져오기
    private SharedPreferences sp,sp1;
    private Integer sb_id1,writer_no1,depth1,group1,id1;//해당글id,작성자no,뎁스,그룹id,id

    Context context;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_register);

        Log.e("생명주기","onCreate");

        service = ApiUtils.getAPIService(); // retrofit 빌드 생성

        //화면 구성요소 셋팅
        setWriterImage = findViewById(R.id.image_comment);
        setComments = findViewById(R.id.edit_comment);
        setbtnComments = findViewById(R.id.btn_comment);
        setbtnComments.setOnClickListener(this);

        //상세화면에서 필요한 정보 받아오기
        Intent fromSbDetail = getIntent();
        sb_no = fromSbDetail.getIntExtra("sb_id", 0);
        writer_no = fromSbDetail.getIntExtra("writer_no", 0);
        writer_image = fromSbDetail.getStringExtra("writer_image");
        Log.e("Intent_sb_no", String.valueOf(sb_no));
        Log.e("Intent_writer_no", String.valueOf(writer_no));
        Log.e("Intent_writer_image", String.valueOf(writer_image));

        sp = getSharedPreferences("comment", Activity.MODE_PRIVATE); //답글달기 한 댓글의 정보:게시글id, 작성자no, depth
        sp1 = getSharedPreferences("login_user", Activity.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp1.getString( "login",null );
        Type type = new TypeToken<UserInfo>(){}.getType();
        user = gson.fromJson( json,type );
        Log.e( "로그인유저:", String.valueOf( user.getNo() ) );//로그인유저 no = 작성자 no
        writer_no = user.getNo();
        writer_image = user.getPicture();



        //작성자이미지 셋팅하기
        Glide.with(this)
                .load("http://13.209.108.67/uploads/" + writer_image)
//                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(setWriterImage);
        setWriterImage.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= 21) {
            setWriterImage.setClipToOutline(true);
        }
        setWriterImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

        cRecyclerView = findViewById(R.id.comment_recyclerview);//리사이클러뷰
        cRecyclerView.setHasFixedSize(true);

        //댓글 리사이클러뷰
        clist = new ArrayList<>();
        CommentsData();
        cAdapter = new CommentAdapter(CommentsActivity.this, clist);
        cRecyclerView.setItemAnimator(new DefaultItemAnimator());

        cRecyclerView.setAdapter(cAdapter);
        cRecyclerView.setAdapter(new CommentAdapter(CommentsActivity.this, clist));
        cRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));



        cRecyclerView.addOnItemTouchListener(new CommentAdapter.RecyclerViewOnItemClickListener(this,cRecyclerView,
                new CommentAdapter.RecyclerViewOnItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
//                        Toast.makeText(CommentsActivity.this,"뿅"+position,Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onItemLongClick(View v, int position) {
//                        댓글id 를 셰어드에 저장한다.
                        id1 = clist.get(position).getId();
                        sb_id1 = clist.get(position).getSbId();

                        dialogShow(); //댓글삭제 확인 팝업: 예를 누르면 DB삭제됨. 아니오 누르면 무반응.
                    }
                }));
    }

    //삭제 Dialog
    public void dialogShow()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("댓글 삭제");
        builder.setMessage("댓글을 삭제하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //삭제메소드

                        //롱탭한 댓글의 id를 가져온다.
                        Log.e("get_id", String.valueOf(sb_id1));
                        Log.e("get_id", String.valueOf(id1));

                        Call<List<CommentItem>> call = service.comment_delete(sb_id1,id1);
                        call.enqueue(new Callback<List<CommentItem>>() {
                            @Override
                            public void onResponse(Call<List<CommentItem>> call, Response<List<CommentItem>> response) {
                                clist= response.body();

                                for (CommentItem c : clist) {
                                    Log.d("id", String.valueOf(c.getId()));
                                    Log.d("sb_id", String.valueOf(c.getSbId()));
                                    Log.d("depth", String.valueOf(c.getDepth()));
                                    Log.d("order", String.valueOf(c.getOrder()));
                                    Log.d("comment", c.getComment());
                                    Log.d("regdate", c.getRegdate());
                                    Log.d("group", String.valueOf(c.getGroup()));
                                    Log.d("writer_no", String.valueOf(c.getWriterNo()));
                                    Log.d("writer_id", String.valueOf(c.getWriterId()));
                                    Log.d("writer_image", c.getWriter_image());
                                }
                                cAdapter = new CommentAdapter(CommentsActivity.this, clist);
                                cRecyclerView.setAdapter(cAdapter);


                            }

                            @Override
                            public void onFailure(Call<List<CommentItem>> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "댓글이 삭제되었습니다.",Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"'아니오'를 선택했습니다.",Toast.LENGTH_LONG).show();
                        //무반응

                    }
                });
        builder.show();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_comment: //댓글 달기 기능
                /*
                설명:
                case1. depth =0 : 댓글
                case2. depth > 0 : 대댓글

                case1일 경우,
                (클라)depth,sb_no,insertComment(뎁스,글id,댓글)을 서버로 전달한다.
                (서버)댓글관련 정보를 댓글DB에 저장한다.
                (서버)댓글 리스트를 다시 response 한다.
                (클라)리사이클러뷰 adapter를 갱신한다.

                case2일 경우,
                (클라)depth,sb_no,group,insertComment(뎁스,글id,그룹id,댓글)을 서버로 전달한다.
                (클라)대댓글은 뎁스가 깊어지는 것이므로, depth+1 하여 넘긴다.
                (서버)댓글 관련 정보를 댓글DB에 저장한다.
                (서버)댓글 리스트를 다시 response 한다.
                (클라)리사이클러뷰 adapter를 갱신한다.

                */

                sb_id1 = sp.getInt("sb_id", 0);
                depth1 = sp.getInt("depth", 0);
                group1 = sp.getInt("group",0);
                Log.e("get_sb_id", String.valueOf(sb_id1));
                Log.e("get_depth", String.valueOf(depth1));
                Log.e("get_group1", String.valueOf(group1));


                Integer depth;
                insertComments = setComments.getText().toString().trim(); //댓글입력정보

                if(depth1!=0){//대댓글일 때,
                    depth = depth1;
                    Log.e("대댓글 depth", String.valueOf(depth));

                    Log.e("댓글 depth", String.valueOf(depth));
                    Log.e("댓글등록sb_no", String.valueOf(sb_id1));
                    Log.e("댓글등록depth", String.valueOf(depth));
                    Log.e("댓글등록group1", String.valueOf(group1));
                    Log.e("댓글등록insertComments", String.valueOf(insertComments));

                    Call<List<CommentItem>> call = service.cofc_register(sb_id1,writer_no,depth,group1,insertComments);
                    call.enqueue(new Callback<List<CommentItem>>() {
                        @Override
                        public void onResponse(Call<List<CommentItem>> call, Response<List<CommentItem>> response) {
                            clist = response.body();
                            Log.e("성공", String.valueOf(response.body()));
                            for (CommentItem c : clist) {
                                Log.d("id", String.valueOf(c.getId()));
                                Log.d("sb_id", String.valueOf(c.getSbId()));
                                Log.d("depth", String.valueOf(c.getDepth()));
                                Log.d("order", String.valueOf(c.getOrder()));
                                Log.d("comment", c.getComment());
                                Log.d("regdate", c.getRegdate());
                                Log.d("group", String.valueOf(c.getGroup()));
                                Log.d("writer_no", String.valueOf(c.getWriterNo()));
                                Log.d("writer_id", String.valueOf(c.getWriterId()));
                                Log.d("writer_image", c.getWriter_image());
                            }
                            cAdapter = new CommentAdapter(CommentsActivity.this, clist);
                            cRecyclerView.setAdapter(cAdapter);
                            cRecyclerView.setAdapter(new CommentAdapter(CommentsActivity.this, clist));

                            //댓글입력란 초기화
                            setComments.setText("");

                            //키보드 숨기기
                            InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                            //화면 최 하단으로 이동
                            cRecyclerView.getLayoutManager().scrollToPosition(cAdapter.getItemCount()-1);


                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt("sb_id", 0);
                            editor.putInt("depth", 0);
                            editor.putInt("group", 0);
                            editor.commit();


                        }

                        @Override
                        public void onFailure(Call<List<CommentItem>> call, Throwable t) {

                        }
                    });


                }else{//댓글일 때
                    depth = 0; //댓글일땐 뎁스 0임.
                    Log.e("댓글 depth", String.valueOf(depth));
                    Log.e("댓글등록sb_no", String.valueOf(sb_no));
                    Log.e("댓글등록writer_no", String.valueOf(writer_no));
                    Log.e("댓글등록depth", String.valueOf(depth));
                    Log.e("댓글등록insertComments", String.valueOf(insertComments));

                    Call<List<CommentItem>> call = service.comments_register(sb_no,writer_no,depth,insertComments);
                    call.enqueue(new Callback<List<CommentItem>>() {
                        @Override
                        public void onResponse(Call<List<CommentItem>> call, Response<List<CommentItem>> response) {
                            clist = response.body();
                            Log.e("성공", String.valueOf(response.body()));
                            for (CommentItem c : clist) {
                                Log.d("id", String.valueOf(c.getId()));
                                Log.d("sb_id", String.valueOf(c.getSbId()));
                                Log.d("depth", String.valueOf(c.getDepth()));
                                Log.d("order", String.valueOf(c.getOrder()));
                                Log.d("comment", c.getComment());
                                Log.d("regdate", c.getRegdate());
                                Log.d("group", String.valueOf(c.getGroup()));
                                Log.d("writer_no", String.valueOf(c.getWriterNo()));
                                Log.d("writer_id", String.valueOf(c.getWriterId()));
                                Log.d("writer_image", c.getWriter_image());
                            }
                            cAdapter = new CommentAdapter(CommentsActivity.this, clist);
                            cRecyclerView.setAdapter(cAdapter);
                            cRecyclerView.setAdapter(new CommentAdapter(CommentsActivity.this, clist));

                            //댓글입력란 초기화
                            setComments.setText("");

                            //키보드 숨기기
                            InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                            //화면 최 하단으로 이동
                            cRecyclerView.getLayoutManager().scrollToPosition(cAdapter.getItemCount()-1);


                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt("sb_id", 0);
                            editor.putInt("depth", 0);
                            editor.putInt("group", 0);
                            editor.commit();


                        }

                        @Override
                        public void onFailure(Call<List<CommentItem>> call, Throwable t) {

                        }
                    });

                }


                break;
        }

    }

    private void CommentsData() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("잠시만 기다려주세요.");
        progressDialog.show();

        Log.e("Data_sb_no", String.valueOf(sb_no));
        Log.e("Data_writer_no", String.valueOf(writer_no));

        Call<List<CommentItem>> call = service.comments_view(sb_no,writer_no);
        call.enqueue(new Callback<List<CommentItem>>() {
            @Override
            public void onResponse(Call<List<CommentItem>> call, Response<List<CommentItem>> response) {
                progressDialog.dismiss();

                clist = response.body();
                for(CommentItem c : clist){
                    Log.d("sb_id", String.valueOf(c.getSbId()));
                    Log.d("writer_no", String.valueOf(c.getWriterNo()));
                    Log.d("depth", String.valueOf(c.getDepth()));
                    Log.d("order", String.valueOf(c.getOrder()));
                    Log.d("comment", c.getComment());
                    Log.d("regdate", c.getRegdate());
                    Log.d("group", String.valueOf(c.getGroup()));
                    Log.d("writer_id", String.valueOf(c.getWriterId()));
                    Log.d("writer_image", c.getWriter_image());
                }

                cAdapter = new CommentAdapter(CommentsActivity.this,clist);
                cRecyclerView.setAdapter(cAdapter);
                cRecyclerView.setAdapter(new CommentAdapter(CommentsActivity.this,clist));


            }

            @Override
            public void onFailure(Call<List<CommentItem>> call, Throwable t) {
                Log.e("fail",t.getMessage());
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //키보드 숨기기
        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("생명주기","onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("생명주기","onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("생명주기","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("생명주기","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("생명주기","onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("생명주기","onRestart");
    }

}
