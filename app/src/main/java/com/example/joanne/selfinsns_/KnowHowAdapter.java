package com.example.joanne.selfinsns_;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.joanne.selfinsns_.retrofit.model.KnowHowItem;
import com.example.joanne.selfinsns_.retrofit.model.UserInfo;
import com.example.joanne.selfinsns_.retrofit.remote.APIService;
import com.example.joanne.selfinsns_.retrofit.remote.API_Result;
import com.example.joanne.selfinsns_.retrofit.remote.ApiUtils;
import com.google.gson.Gson;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Joanne on 2018-05-11.
 */

public class KnowHowAdapter extends RecyclerView.Adapter<KnowHowAdapter.ViewHolder>{

    KnowHowActivity context;
    List<KnowHowItem> items;
    private HashTagHelper mTextHashTagHelper;//해시태그 API

    private Integer loginUser_no, int_like_count, kh_id, writer_no;
    private UserInfo user;
    private SharedPreferences sp,sp1;
    private APIService service;

    private Integer order;
    private List<KnowHowItem> image_item;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        public void onItemClick(View view, int position);
    }

    public KnowHowAdapter(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public KnowHowAdapter(KnowHowActivity context, List<KnowHowItem> items){
        this.context = context;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View view = layoutInflater.inflate( R.layout.knowhow_item,parent, false );

        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(KnowHowAdapter.ViewHolder holder, final int position) {
        service = ApiUtils.getAPIService();
        final KnowHowItem item = items.get( position );
        holder.name.setText( items.get( position ).getWriter() );
        holder.subject.setText( items.get( position ).getSubject() );
        mTextHashTagHelper = HashTagHelper.Creator.create(context.getResources().getColor(R.color.colorHashTag), null);
        mTextHashTagHelper.handle(holder.tag);//태그 적용
        holder.tag.setText( items.get( position ).getTag() );
        holder.like_count.setText( items.get( position ).getLike_count().toString());
        holder.comment_count.setText( items.get( position ).getComment_count().toString() );
        Glide.with(context)//이미지
                .load("http://13.209.108.67/uploads/" + items.get(position).getImage())
                .into(holder.image);
        Glide.with(context)//작성자이미지
                .load("http://13.209.108.67/uploads/" + items.get(position).getWriter_image())
                .into(holder.uimage);
        holder.uimage.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= 21) {
            holder.uimage.setClipToOutline(true);
        }
        holder.uimage.setScaleType(ImageView.ScaleType.CENTER_CROP);

//        sp = context.getActivity().getSharedPreferences("kh_detail", Activity.MODE_PRIVATE);
//
//                final Integer kh_id = items.get(position).getId();
//                String subject = items.get(position).getSubject();
//                String section = items.get(position).getSection();
//                String style = items.get(position).getStyle();
//                String space =items.get(position).getSpace();
//                String desc = items.get(position).getDesc();
//                String tag = items.get(position).getTag();
//                String image = items.get(position).getImage();
//                String writer_image = items.get(position).getWriter_image();
//                Integer writer_no = items.get(position).getWriterNo();
//                String writer_name = items.get(position).getWriter();
//                String regdate = items.get(position).getRegdate();
//                Integer like_count = items.get(position).getLike_count();
//                Integer comment_count = items.get(position).getComment_count();
//                Log.e("kh_shared", String.valueOf(kh_id));
//                Log.e("kh_shared",subject);
//                Log.e("kh_shared",section);
//                Log.e("kh_shared",style);
//                Log.e("kh_shared",space);
//                Log.e("kh_shared",desc);
//                Log.e("kh_shared",tag);
//                Log.e("kh_shared",image);
//                Log.e("kh_shared",writer_image);
//                Log.e("kh_shared", String.valueOf(writer_no));
//                Log.e("kh_shared",writer_name);
//                Log.e("kh_shared", String.valueOf(like_count));
//                Log.e("kh_shared", String.valueOf(comment_count));
//
//                KnowHowItem kh = new KnowHowItem(kh_id,subject,section,style,space,desc,tag,writer_image,writer_no,writer_name,regdate,image,like_count,comment_count);
//                Gson gson = new Gson();
//                String kh_item = gson.toJson(kh);
//                SharedPreferences.Editor editor = sp.edit();
//                editor.putString("kh_item", kh_item);
//                editor.commit();
//                Log.e("shared_sb_item", kh_item);

        holder.knowhowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp = context.getActivity().getSharedPreferences("kh_detail", Activity.MODE_PRIVATE);

                final Integer kh_id = items.get(position).getId();
                String subject = items.get(position).getSubject();
                String section = items.get(position).getSection();
                String style = items.get(position).getStyle();
                String space =items.get(position).getSpace();
                String desc = items.get(position).getDesc();
                String tag = items.get(position).getTag();
                String image = items.get(position).getImage();
                String writer_image = items.get(position).getWriter_image();
                Integer writer_no = items.get(position).getWriterNo();
                String writer_name = items.get(position).getWriter();
                String regdate = items.get(position).getRegdate();
                Integer like_count = items.get(position).getLike_count();
                Integer comment_count = items.get(position).getComment_count();
                Log.e("kh_shared", String.valueOf(kh_id));
                Log.e("kh_shared",subject);
                Log.e("kh_shared",section);
                Log.e("kh_shared",style);
                Log.e("kh_shared",space);
                Log.e("kh_shared",desc);
                Log.e("kh_shared",tag);
                Log.e("kh_shared",image);
                Log.e("kh_shared",writer_image);
                Log.e("kh_shared", String.valueOf(writer_no));
                Log.e("kh_shared",writer_name);
                Log.e("kh_shared", String.valueOf(like_count));
                Log.e("kh_shared", String.valueOf(comment_count));

                KnowHowItem kh = new KnowHowItem(kh_id,subject,section,style,space,desc,tag,writer_image,writer_no,writer_name,regdate,image,like_count,comment_count);
                Gson gson = new Gson();
                String kh_item = gson.toJson(kh);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("kh_item", kh_item);
                editor.commit();
                Log.e("shared_sb_item", kh_item);


                Intent toDetail = new Intent(context.getActivity(), KnowHowDetailActivity.class);
                toDetail.putExtra("count",like_count);
                toDetail.putExtra("fragment",1);
                toDetail.setAction( Intent.ACTION_VIEW );
                context.getActivity().startActivity(toDetail);

                //이미지불러와서 셰어드저장.
                        Call<API_Result> call = service.kh_view_image_count(kh_id);
                        call.enqueue(new Callback<API_Result>() {
                            @Override
                            public void onResponse(Call<API_Result> call, Response<API_Result> response) {
                                order = Integer.valueOf(response.body().getMessage());
                                Log.e("order", String.valueOf(order));
                                image_item = new ArrayList<>();
                                for(int i = 0; i<order; i++){

                                    Call<KnowHowItem> call_images = service.kh_view_images(kh_id,i);
                                    final int finalI = i;
                                    call_images.enqueue(new Callback<KnowHowItem>() {
                                        @Override
                                        public void onResponse(Call<KnowHowItem> call, Response<KnowHowItem> response) {
                                            KnowHowItem item = response.body();
                                            image_item.add(item);
                                            Log.e("ilist", String.valueOf(item.getImage()));
                                            Log.e("ilist", String.valueOf(item.getImage_desc()));
                                            Log.e("ilist", String.valueOf(image_item.get(0).getImage()));
                                            Log.e("ilist", String.valueOf(image_item.get(0).getImage_desc()));

                                            sp1 = context.getActivity().getSharedPreferences("kh_detail_image", Activity.MODE_PRIVATE);
                                            Gson gson = new Gson();
                                            String kh_item = gson.toJson(image_item);
                                            SharedPreferences.Editor editor = sp1.edit();
                                            editor.putString("kh_item_image", kh_item);
                                            editor.commit();
                                            Log.e("shared_kh_item", kh_item);



                                        }

                                        @Override
                                        public void onFailure(Call<KnowHowItem> call, Throwable t) {

                                        }
                                    });
                                }

                            }

                            @Override
                            public void onFailure(Call<API_Result> call, Throwable t) {

                            }
                        });


            }
        });



    }

    @Override
    public int getItemCount() {

        return this.items.size();
    }

    private void Data2() {



    }
    protected class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout knowhowLayout;
        ImageView image,uimage;
        TextView tag;
        TextView subject;
        TextView name;
        TextView like_count;
        TextView comment_count;
        CheckBox btnLike;


        public ViewHolder(View itemView) {
            super( itemView );
            knowhowLayout = itemView.findViewById( R.id.knowhow_item_layout );
            image = itemView.findViewById( R.id.kh_picture );
            tag = itemView.findViewById( R.id.kh_tag );
            subject = itemView.findViewById( R.id.kh_subject );
            uimage = itemView.findViewById( R.id.kh_uimage );
            name = itemView.findViewById( R.id.kh_uname );
            like_count = itemView.findViewById( R.id.kh_like_count );
            comment_count = itemView.findViewById( R.id.kh_comment_count );
            btnLike = itemView.findViewById(R.id.kh_like);


        }
    }

    public static class RecyclerViewOnItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
        private KnowHowAdapter.RecyclerViewOnItemClickListener.OnItemClickListener mListener;
        private GestureDetector mGestureDetector;
        public RecyclerViewOnItemClickListener(Context context, final RecyclerView recyclerView, KnowHowAdapter.RecyclerViewOnItemClickListener.OnItemClickListener listener) {
            this.mListener = listener; mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
                @Override public void onLongPress(MotionEvent e) {
                    View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if(childView != null && mListener != null){
                        mListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                    }
                }
            });
        }




        @Override public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(child, rv.getChildAdapterPosition(child));

                return true;
            }
            return false;
        }

        public interface OnItemClickListener {
            void onItemClick(View v, int position);
            void onItemLongClick(View v, int position);
        }
    }
}
