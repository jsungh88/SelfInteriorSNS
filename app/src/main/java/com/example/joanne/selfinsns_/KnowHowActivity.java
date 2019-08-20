package com.example.joanne.selfinsns_;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.joanne.selfinsns_.retrofit.model.KnowHowItem;
import com.example.joanne.selfinsns_.retrofit.model.StyleBookItem;
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

/**
 * Created by Joanne on 2018-05-11.
 */

public class KnowHowActivity extends Fragment {

    RecyclerView knowHowRecycler;
    KnowHowAdapter knowHowAdapter;
    List<KnowHowItem> items,image_item, img_description_item;

    APIService service;
    SharedPreferences sp,sp1;
    private UserInfo user;
    private Integer no, image_count, img_description_count;
    private int i;

    private Integer order;

    public KnowHowActivity(){};


    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View pageOne = inflater.inflate( R.layout.knowhow_activity, container,false );

        service = ApiUtils.getAPIService();
        sp = getActivity().getSharedPreferences("login_user", Activity.MODE_PRIVATE); //SharedPreference 객체 생성
        Gson gson = new Gson();
        String json = sp.getString("login", null);
        Type type = new TypeToken<UserInfo>() {
        }.getType();
        user = gson.fromJson(json, type);
        no = user.getNo();
        Log.e("로그인유저:", String.valueOf(user.getNo()));



        knowHowRecycler = pageOne.findViewById( R.id.knowhow_list );
        knowHowRecycler.setHasFixedSize( true );
        items= new ArrayList<>(  );
        image_item = new ArrayList<>();
        Data();

//        knowHowRecycler.addOnItemTouchListener(new KnowHowAdapter.RecyclerViewOnItemClickListener(getActivity(), knowHowRecycler,
//                new KnowHowAdapter.RecyclerViewOnItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View v, int position) {
//                        sp1 = getActivity().getSharedPreferences("kh_detail", Activity.MODE_PRIVATE);
//
//                        final Integer kh_id = items.get(position).getId();
//                        String subject = items.get(position).getSubject();
//                        String section = items.get(position).getSection();
//                        String style = items.get(position).getStyle();
//                        String space =items.get(position).getSpace();
//                        String desc = items.get(position).getDesc();
//                        String tag = items.get(position).getTag();
//                        String image = items.get(position).getImage();
//                        String writer_image = items.get(position).getWriter_image();
//                        Integer writer_no = items.get(position).getWriterNo();
//                        String writer_name = items.get(position).getWriter();
//                        String regdate = items.get(position).getRegdate();
//                        Integer like_count = items.get(position).getLike_count();
//                        Integer comment_count = items.get(position).getComment_count();
//                        Log.e("kh_shared", String.valueOf(kh_id));
//                        Log.e("kh_shared",subject);
//                        Log.e("kh_shared",section);
//                        Log.e("kh_shared",style);
//                        Log.e("kh_shared",space);
//                        Log.e("kh_shared",desc);
//                        Log.e("kh_shared",tag);
//                        Log.e("kh_shared",image);
//                        Log.e("kh_shared",writer_image);
//                        Log.e("kh_shared", String.valueOf(writer_no));
//                        Log.e("kh_shared",writer_name);
//                        Log.e("kh_shared", String.valueOf(like_count));
//                        Log.e("kh_shared", String.valueOf(comment_count));
//
//                        KnowHowItem kh = new KnowHowItem(kh_id,subject,section,style,space,desc,tag,writer_image,writer_no,writer_name,regdate,image,like_count,comment_count);
//                        Gson gson = new Gson();
//                        String kh_item = gson.toJson(kh);
//                        SharedPreferences.Editor editor = sp.edit();
//                        editor.putString("kh_item", kh_item);
//                        editor.commit();
//                        Log.e("shared_kh_image_item", kh_item);
//
//
//                        //이미지불러와서 셰어드저장.
//                        Call<API_Result> call = service.kh_view_image_count(kh_id);
//                        call.enqueue(new Callback<API_Result>() {
//                            @Override
//                            public void onResponse(Call<API_Result> call, Response<API_Result> response) {
//                                order = Integer.valueOf(response.body().getMessage());
//                                Log.e("order", String.valueOf(order));
//
//                                for(int i = 0; i<order; i++){
//
//                                    Call<KnowHowItem> call_images = service.kh_view_images(kh_id,i);
//                                    final int finalI = i;
//                                    call_images.enqueue(new Callback<KnowHowItem>() {
//                                        @Override
//                                        public void onResponse(Call<KnowHowItem> call, Response<KnowHowItem> response) {
//                                            KnowHowItem item = response.body();
//                                            image_item.add(item);
//                                            Log.e("ilist", String.valueOf(item.getImage()));
//                                            Log.e("ilist", String.valueOf(item.getImage_desc()));
//                                            Log.e("ilist", String.valueOf(image_item.get(0).getImage()));
//                                            Log.e("ilist", String.valueOf(image_item.get(0).getImage_desc()));
//
//                                            sp1 = getActivity().getSharedPreferences("kh_detail_image", Activity.MODE_PRIVATE);
//                                            Gson gson = new Gson();
//                                            String kh_item = gson.toJson(image_item);
//                                            SharedPreferences.Editor editor = sp1.edit();
//                                            editor.putString("kh_item_image", kh_item);
//                                            editor.commit();
//                                            Log.e("shared_kh_item", kh_item);
//
//
//
//                                        }
//
//                                        @Override
//                                        public void onFailure(Call<KnowHowItem> call, Throwable t) {
//
//                                        }
//                                    });
//                                }
//
//                            }
//
//                            @Override
//                            public void onFailure(Call<API_Result> call, Throwable t) {
//
//                            }
//                        });
//
//
//
//
//
//                        Intent toDetail = new Intent(getActivity(), KnowHowDetailActivity.class);
////                        toDetail.putExtra("count",like_count);
////                        toDetail.putExtra("fragment",1);
//                        toDetail.setAction( Intent.ACTION_VIEW );
//                        getActivity().startActivity(toDetail);
//                    }
//
//                    @Override
//                    public void onItemLongClick(View v, int position) {
//
//                    }
//                }));


        return pageOne;
    }

    public void Data(){

        Call<List<KnowHowItem>> call = service.kh_view_main(no);
        call.enqueue(new Callback<List<KnowHowItem>>() {
            @Override
            public void onResponse(Call<List<KnowHowItem>> call, Response<List<KnowHowItem>> response) {
                items = response.body();
                int index = 0;
                for(KnowHowItem k : items){
                    int ind = index++;

                    Log.e("id", String.valueOf(k.getId()));
                    Log.e("subject", String.valueOf(k.getSubject()));
                    Log.e("section", String.valueOf(k.getSection()));

                }
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), items.size(),GridLayoutManager.HORIZONTAL,false);
                knowHowAdapter = new KnowHowAdapter(KnowHowActivity.this, items);
                knowHowRecycler.setAdapter(knowHowAdapter);





            }

            @Override
            public void onFailure(Call<List<KnowHowItem>> call, Throwable t) {

            }
        });



    }



}
