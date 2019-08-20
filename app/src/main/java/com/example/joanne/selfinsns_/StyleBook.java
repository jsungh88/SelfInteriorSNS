package com.example.joanne.selfinsns_;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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

public class StyleBook extends Fragment {

    RecyclerView stylebookRecycler;
    StyleBookAdapter_main styleBookAdapterMain;
    StyleBookAdapter_mypage styleBookAdapterMypage;
    List<StyleBookItem> list;
    APIService service;
    StyleBookItem sb;


    private boolean fromMypage = false;

    private Integer no, no2;//사용자no값,작성자no값
    private UserInfo user, writer;
    private SharedPreferences sp, sp1, sp2, sp3; //로그인한 사용자정보 불러오기 ,sp3작성자정보의 활동내역인지?

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Toast.makeText(getContext(), "onCreateActivity", Toast.LENGTH_SHORT).show();
        View styleBook = inflater.inflate(R.layout.stylebook_activity, container, false);

        /*
         * mypage에 보여질 리스트인지 확인할 수 있는 변수값*/
        Intent i = getActivity().getIntent();
        fromMypage = i.getBooleanExtra("fromMypage", false);
        Log.e("intent_fromMypage", String.valueOf(fromMypage));

        /*
         로그인한 유저 정보 중 사용자'no' 값 가져오기
         - 마이페이지'스타일북'의 경우, 해당 사용자가 올린 글만 보여줘야 하기 때문에 'no' 필요함         *
         */
        sp = getActivity().getSharedPreferences("login_user", Activity.MODE_PRIVATE); //SharedPreference 객체 생성
        Gson gson = new Gson();
        String json = sp.getString("login", null);
        Type type = new TypeToken<UserInfo>() {
        }.getType();
        user = gson.fromJson(json, type);
        no = user.getNo();
        Log.e("로그인유저:", String.valueOf(user.getNo()));

        service = ApiUtils.getAPIService();

        stylebookRecycler = styleBook.findViewById(R.id.stylebook_list);
        stylebookRecycler.setHasFixedSize(true);
        list = new ArrayList<>(); //스타일북 data담을 리스트

        Data();
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), items.size(), GridLayoutManager.HORIZONTAL, false);
        if (!fromMypage) { //"메인"페이지에 보여주는 경우
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL);
            stylebookRecycler.setLayoutManager(staggeredGridLayoutManager);
        } else if (fromMypage) { //"마이"페이지에 보여주는 경우
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
            stylebookRecycler.setLayoutManager(gridLayoutManager);
        }

//        stylebookRecycler.addOnItemTouchListener(new StyleBookAdapter_main.RecyclerViewOnItemClickListener(getContext(), stylebookRecycler,
//                new StyleBookAdapter_main.RecyclerViewOnItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View v, int position) {
//
//                        sp2 = getActivity().getSharedPreferences("sb_detail", Activity.MODE_PRIVATE);
//
//                        Integer sb_no = list.get(position).getNo();
//                        String image = list.get(position).getImage();
//                        String content = list.get(position).getContent();
//                        String location = list.get(position).getLocation();
//                        String location_lat = list.get(position).getLocation_lat();
//                        String location_lng = list.get(position).getLocation_lng();
//                        String regdate = list.get(position).getRegdate();
//                        String writer_name = list.get(position).getWriter_name();
//                        String writer_image = list.get(position).getWriter_image();
//                        Integer writer_no = list.get(position).getWriter_no();
//                        Integer like_count = list.get(position).getLike();
//                        Integer comment_count = list.get(position).getComment();
//                        Log.e("sb_no", String.valueOf(list.get(position).getNo()));
//                        Log.e("like_count", String.valueOf(list.get(position).getLike()));
//                        Log.e("comment_count", String.valueOf(list.get(position).getComment()));
//                        sb = new StyleBookItem(sb_no,image,content,location,location_lat,location_lng,regdate,writer_name,writer_image,writer_no,like_count,comment_count);
//
//                        Gson gson = new Gson();
//                        String sb_item = gson.toJson(sb);
//                        SharedPreferences.Editor editor = sp2.edit();
//                        editor.putString("sb_item", sb_item);
//                        editor.commit();
//                        Log.e("shared_sb_item", sb_item);
//
////                        Intent toDetail = new Intent(getContext(), StyleBookDetailActivity.class);
////                        toDetail.putExtra("sb_no", list.get(position).getNo());
////                        toDetail.putExtra("image", list.get(position).getImage());
////                        toDetail.putExtra("content", list.get(position).getContent());
////                        toDetail.putExtra("location", list.get(position).getLocation());
////                        toDetail.putExtra("location_lat", list.get(position).getLocation_lat());
////                        toDetail.putExtra("location_lng", list.get(position).getLocation_lng());
////                        toDetail.putExtra("regdate", list.get(position).getRegdate());
////                        toDetail.putExtra("writer_name", list.get(position).getWriter_name());
////                        toDetail.putExtra("writer_image", list.get(position).getWriter_image());
////                        toDetail.putExtra("writer_no", list.get(position).getWriter_no());
////                        toDetail.putExtra("like_count", String.valueOf(list.get(position).getLike()));
////                        toDetail.putExtra("comment_count", String.valueOf(list.get(position).getComment()));
////
////                        Log.e("sb_no", String.valueOf(list.get(position).getNo()));
////                        Log.e("like_count", String.valueOf(list.get(position).getLike()));
////                        Log.e("comment_count", String.valueOf(list.get(position).getComment()));
////                        startActivity(toDetail);
//                    }
//
//                    @Override
//                    public void onItemLongClick(View v, int position) {
//
//                    }
//                }));


        return styleBook;
    }


    public void Data() {

//        final ProgressDialog progressDialog = new ProgressDialog(getContext());
//        progressDialog.setMessage("Loading data...");
//        progressDialog.show();

        if (!fromMypage) {//"메인"페이지에 보여주는 리스트일 경우,
            //+추가됨(0704 - 작성자페이지를 보여주는경우)
            sp3 = getActivity().getSharedPreferences("who", Activity.MODE_PRIVATE); //로그인한 유저 정보를 저장해놓은 셰어드(디테일에서저장)
            String who = sp3.getString("who", "loginUser");

            if (who.equals("writer")) { //마이페이지 - 작성자 (마이페이지꺼랑 같이써서 꼬임.. )
                Gson gson = new Gson();
                String json = sp3.getString("writer_user", null);
                Type type = new TypeToken<UserInfo>() {
                }.getType();
                writer = gson.fromJson(json, type);
                no2 = writer.getNo();
                Log.e("작성자no", String.valueOf(no2));

                Log.e("마이페이지", "에서 넘어왔지롱");
                Call<List<StyleBookItem>> call = service.sb_view_mypage(no2);
                call.enqueue(new Callback<List<StyleBookItem>>() {
                    @Override
                    public void onResponse(Call<List<StyleBookItem>> call, Response<List<StyleBookItem>> response) {
//                        progressDialog.dismiss();
                        //수정할때 다시 main으로 오기위한 표식?
                        //어떤스크린에서 출발했는지 저장하는 셰어드
                        sp1 = getActivity().getSharedPreferences("what_screen", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp1.edit();
                        editor.putString("what", "mypage");
                        editor.commit();
                        Log.e("what_screen", "스타일북(메인)");

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
                        styleBookAdapterMypage = new StyleBookAdapter_mypage(StyleBook.this, list);
                        stylebookRecycler.setAdapter(styleBookAdapterMypage);


                    }

                    @Override
                    public void onFailure(Call<List<StyleBookItem>> call, Throwable t) {

                    }
                });


            }else {
                Call<List<StyleBookItem>> call = service.sb_view_main(no);
                call.enqueue(new Callback<List<StyleBookItem>>() {
                    @Override
                    public void onResponse(Call<List<StyleBookItem>> call, Response<List<StyleBookItem>> response) {
//                        progressDialog.dismiss();

                        //수정할때 다시 main으로 오기위한 표식?
                        //어떤스크린에서 출발했는지 저장하는 셰어드
                        sp1 = getActivity().getSharedPreferences("what_screen", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp1.edit();
                        editor.putString("what", "main");
                        editor.commit();
                        Log.e("what_screen", "스타일북(메인)");

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
                        styleBookAdapterMain = new StyleBookAdapter_main(StyleBook.this, list);
                        stylebookRecycler.setAdapter(styleBookAdapterMain);
                        stylebookRecycler.setAdapter(new StyleBookAdapter_main(StyleBook.this, list));

                    }

                    @Override
                    public void onFailure(Call<List<StyleBookItem>> call, Throwable t) {

                    }
                });
            }
        } else if (fromMypage) {//"마이"페이지에 보여주는 리스트일 경우,
            //만약 작성자정보를 보여줘야하는 경우,
            //디테일에서 넘어온 경우, 로그인 유저가 아님. .
            sp3 = getActivity().getSharedPreferences("who", Activity.MODE_PRIVATE); //로그인한 유저 정보를 저장해놓은 셰어드이다!!(디테일에서저장)
            String who = sp3.getString("who", "loginUser");



            if (who.equals("writer")) { //마이페이지 - 작성자정보
                Gson gson = new Gson();
                String json = sp3.getString("writer_user", null);
                Type type = new TypeToken<UserInfo>() {
                }.getType();
                writer = gson.fromJson(json, type);
                no2 = writer.getNo();
                Log.e("작성자no", String.valueOf(no2));

                if(writer.getNo()==user.getNo()){//만약 로그인유저no와 작성자no가 같다면 "마이페이지"를 보여준다.
                    Call<List<StyleBookItem>> call = service.sb_view_mypage(no);
                    call.enqueue(new Callback<List<StyleBookItem>>() {
                        @Override
                        public void onResponse(Call<List<StyleBookItem>> call, Response<List<StyleBookItem>> response) {
//                            progressDialog.dismiss();

                            //수정할때 다시 main으로 오기위한 표식?
                            //어떤스크린에서 출발했는지 저장하는 셰어드
                            sp1 = getActivity().getSharedPreferences("what_screen", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp1.edit();
                            editor.putString("what", "main");
                            editor.commit();
                            Log.e("what_screen", "스타일북(메인)");

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
                            styleBookAdapterMain = new StyleBookAdapter_main(StyleBook.this, list);
                            stylebookRecycler.setAdapter(styleBookAdapterMain);
                            stylebookRecycler.setAdapter(new StyleBookAdapter_main(StyleBook.this, list));

                        }

                        @Override
                        public void onFailure(Call<List<StyleBookItem>> call, Throwable t) {

                        }
                    });
                }else {

                    Log.e("마이페이지", "에서 넘어왔지롱");
                    Call<List<StyleBookItem>> call = service.sb_view_mypage(no2);
                    call.enqueue(new Callback<List<StyleBookItem>>() {
                        @Override
                        public void onResponse(Call<List<StyleBookItem>> call, Response<List<StyleBookItem>> response) {
//                            progressDialog.dismiss();
                            //수정할때 다시 main으로 오기위한 표식?
                            //어떤스크린에서 출발했는지 저장하는 셰어드
                            sp1 = getActivity().getSharedPreferences("what_screen", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp1.edit();
                            editor.putString("what", "mypage");
                            editor.commit();
                            Log.e("what_screen", "스타일북(메인)");

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
                            styleBookAdapterMypage = new StyleBookAdapter_mypage(StyleBook.this, list);
                            stylebookRecycler.setAdapter(styleBookAdapterMypage);


                        }

                        @Override
                        public void onFailure(Call<List<StyleBookItem>> call, Throwable t) {

                        }
                    });
                }

            }else{//마이페이지 - 로그인유저
                Call<List<StyleBookItem>> call = service.sb_view_mypage(no);
                call.enqueue(new Callback<List<StyleBookItem>>() {
                    @Override
                    public void onResponse(Call<List<StyleBookItem>> call, Response<List<StyleBookItem>> response) {
//                        progressDialog.dismiss();

                        //수정할때 다시 main으로 오기위한 표식?
                        //어떤스크린에서 출발했는지 저장하는 셰어드
                        sp1 = getActivity().getSharedPreferences("what_screen", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp1.edit();
                        editor.putString("what", "main");
                        editor.commit();
                        Log.e("what_screen", "스타일북(메인)");

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
                        styleBookAdapterMain = new StyleBookAdapter_main(StyleBook.this, list);
                        stylebookRecycler.setAdapter(styleBookAdapterMain);
                        stylebookRecycler.setAdapter(new StyleBookAdapter_main(StyleBook.this, list));

                    }

                    @Override
                    public void onFailure(Call<List<StyleBookItem>> call, Throwable t) {

                    }
                });


            }

        }

    }
        @Override
        public void onPause () {
            super.onPause();
            Log.e("StyleBook", "onPause");

        }

        @Override
        public void onStart () {
            super.onStart();
            Log.e("StyleBook", "onStart");


        }

        @Override
        public void onResume () {
            super.onResume();
            Log.e("StyleBook", "onResume");

        }


        @Override
        public void onStop () {
            super.onStop();
            Log.e("StyleBook", "onStop");
        }

        @Override
        public void onDestroy () {
            super.onDestroy();
            Log.e("StyleBook", "onDestroy");
        }


    }
