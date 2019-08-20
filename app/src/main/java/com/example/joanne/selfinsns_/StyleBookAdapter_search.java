package com.example.joanne.selfinsns_;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.RequiresApi;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.resource.bitmap.CircleCrop;
//import com.bumptech.glide.request.RequestOptions;
import com.example.joanne.selfinsns_.retrofit.model.StyleBookItem;
import com.example.joanne.selfinsns_.retrofit.model.UserInfo;
import com.example.joanne.selfinsns_.retrofit.remote.APIService;
import com.example.joanne.selfinsns_.retrofit.remote.ApiUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StyleBookAdapter_search extends RecyclerView.Adapter<StyleBookAdapter_search.ViewHolder> {

    Context context;
    List<StyleBookItem> items;
    private HashTagHelper mTextHashTagHelper;//해시태그 API

    private Integer loginUser_no, int_like_count, sb_no, writer_no;
    private UserInfo user;
    private SharedPreferences sp;
    private APIService service;

    public StyleBookAdapter_search(Context context, List<StyleBookItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.stylebook_item, parent, false);

        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final int Position = position;

        Glide.with(context)//이미지
                .load("http://13.209.108.67/uploads/" + items.get(position).getImage())
//                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(holder.image);

        holder.contents.setText(items.get(position).getContent()); //내용(+태그)
//        holder.contents.setMaxLines(1);
//        holder.contents.setEllipsize(TextUtils.TruncateAt.END);
        mTextHashTagHelper = HashTagHelper.Creator.create(context.getResources().getColor(R.color.colorHashTag), null);
        mTextHashTagHelper.handle(holder.contents);//태그 적용
//      List<String> allHashTags = mTextHashTagHelper.getAllHashTags(); //텍스트에서 모든 해시태그를 가져오는 코드.->태그누르면 해당 리스트 보여지게.

        Glide.with(context)//작성자 이미지
                .load("http://13.209.108.67/uploads/" + items.get(position).getWriter_image())
//                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(holder.writer_image);
        holder.writer_image.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= 21) {
            holder.writer_image.setClipToOutline(true);
        }
        holder.writer_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.writer_name.setText(items.get(position).getWriter_name()); //작성자 이름
        holder.like_count.setText(String.valueOf(items.get(position).getLike())); //좋아요수
        holder.comment_count.setText(String.valueOf(items.get(position).getComment())); //코멘트수
        if(items.get(position).getLike_shape().equals("true")){
            holder.btnlike.setChecked(true);
        }else{
            holder.btnlike.setChecked(false);
        }

        holder.btnlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service = ApiUtils.getAPIService();

                //로그인유저 정보 불러오기 .
                sp = context.getSharedPreferences("login_user", Activity.MODE_PRIVATE); //SharedPreference 객체 생성
                Gson gson = new Gson();
                String json = sp.getString("login", null);
                Type type = new TypeToken<UserInfo>() {
                }.getType();
                user = gson.fromJson(json, type);
                loginUser_no = user.getNo(); //좋아요 누르는 사람 no 추출.
                Log.e("로그인유저:", String.valueOf(user.getNo()));

                int_like_count = items.get(position).getLike(); //좋아요
                sb_no = items.get(position).getNo();//글id
                writer_no = items.get(position).getWriter_no();//글을 작성한 사람 : 이후에, 알림 내역을 보내주는데 필요하기때문에 db에 함께 저장.
                Log.e("좋아요수1:", String.valueOf(int_like_count));

                if (holder.btnlike.isChecked()) { //좋아요 등록

                    //서버 전송 - 좋아요등록
                    Call<Integer> call_like = service.sb_like(sb_no, loginUser_no,writer_no);
                    call_like.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            int_like_count = response.body();
                            Log.e("response.body", String.valueOf(int_like_count));

                            holder.like_count.setText(String.valueOf(int_like_count));//셋팅
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            Log.e("좋아요 등록 실패", t.getMessage());
                            Toast.makeText(context, "좋아요 등록 실패", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else { //좋아요 취소
                    Log.e("눌리긴하니?", "좋아요버튼2");

                    //서버 전송 - 좋아요삭제
                    Call<Integer> call_unlike = service.sb_unlike(sb_no, loginUser_no);
                    call_unlike.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            int_like_count = response.body();
                            Log.e("response.body", String.valueOf(int_like_count));

                            holder.like_count.setText(int_like_count.toString());//셋팅
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            Log.e("좋아요취소실패", t.getMessage());
                            Toast.makeText(context, "좋아요 취소 실패", Toast.LENGTH_SHORT).show();
                        }
                    });

                }



            }
        });

//            holder.writer_image.setOnClickListener(new View.OnClickListener() { //작성자 이미지 클릭했을 경우, 작성자의 스타일북 페이지로 넘어가게
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
//
//            holder.image.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });

    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        LinearLayout stylebookLayout;
        ImageView image;
        TextView contents;
        ImageView writer_image;
        TextView writer_name;
        TextView like_count;
        TextView comment_count;
        CheckBox btnlike;

        public ViewHolder(View itemView) {
            super(itemView);

            stylebookLayout = itemView.findViewById(R.id.stylebook_item_layout);
            image = itemView.findViewById(R.id.sb_image);
            contents = itemView.findViewById(R.id.sb_tags);
            writer_image = itemView.findViewById(R.id.sb_uimage);
            writer_name = itemView.findViewById(R.id.sb_uname);
            like_count = itemView.findViewById(R.id.sb_like_count);
            comment_count = itemView.findViewById(R.id.sb_comments_count);
            btnlike = itemView.findViewById(R.id.sb_btn_like);
        }

        @Override
        public void onClick(View v) {

        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }

        private void addItem(int position, StyleBookItem item) {
            items.add(position, item);
            notifyItemInserted(position);
        }

    }

    public static class RecyclerViewOnItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
        private OnItemClickListener mListener;
        private GestureDetector mGestureDetector;
        public RecyclerViewOnItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
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
