package com.example.joanne.selfinsns_;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.joanne.selfinsns_.retrofit.model.NotificationItem;
import com.example.joanne.selfinsns_.retrofit.remote.APIService;
import com.example.joanne.selfinsns_.retrofit.remote.API_Result;
import com.example.joanne.selfinsns_.retrofit.remote.ApiUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_LIKE = 0;
    public static final int VIEW_TYPE_FOLLOW = 1;

    private List<NotificationItem> nlist;
    private Context context;

    private APIService service;

    public interface OnItemClickListener {
        public void onItemClick(View viwe, int position);
    }

    public NotificationAdapter(Context context, List<NotificationItem> nlist) {
        this.context = context;
        this.nlist = nlist;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LIKE) {
            View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item_like, parent, false);
            return new ViewHolder1(v1);
        } else {
            View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item_follow, parent, false);
            return new ViewHolder2(v2);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (nlist.get(position).getCategory().equals("댓글") || nlist.get(position).getCategory().equals("좋아요")) {

            return VIEW_TYPE_LIKE;
        } else {
            return VIEW_TYPE_FOLLOW;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        NotificationItem item = nlist.get(position);
        if (nlist.get(position).getCategory().equals("댓글") || nlist.get(position).getCategory().equals("좋아요")) {
            //작성자이미지 셋팅하기
            Glide.with(context)
                    .load("http://13.209.108.67/uploads/" + nlist.get(position).getuImage())
//                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(((ViewHolder1) holder).uimage);
            ((ViewHolder1) holder).uimage.setBackground(new ShapeDrawable(new OvalShape()));
            if (Build.VERSION.SDK_INT >= 21) {
                ((ViewHolder1) holder).uimage.setClipToOutline(true);
            }
            ((ViewHolder1) holder).uimage.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            ((ViewHolder1) holder).sender.setText(nlist.get(position).getuName());
            ((ViewHolder1) holder).desc.setText(nlist.get(position).getNoti_desc());
            ((ViewHolder1) holder).regdate.setText(nlist.get(position).getRegdate());
            Glide.with(context)
                    .load("http://13.209.108.67/uploads/" + nlist.get(position).getImage())
                    .into(((ViewHolder1) holder).picture);

        } else if (nlist.get(position).getCategory().equals("팔로우")) {
            //작성자이미지 셋팅하기
            Glide.with(context)
                    .load("http://13.209.108.67/uploads/" + nlist.get(position).getuImage())
//                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(((ViewHolder2) holder).uimage);
            ((ViewHolder2) holder).uimage.setBackground(new ShapeDrawable(new OvalShape()));
            if (Build.VERSION.SDK_INT >= 21) {
                ((ViewHolder2) holder).uimage.setClipToOutline(true);
            }
            ((ViewHolder2) holder).uimage.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            ((ViewHolder2) holder).sender.setText(nlist.get(position).getuName());
            ((ViewHolder2) holder).desc.setText(nlist.get(position).getNoti_desc());
            ((ViewHolder2) holder).regdate.setText(nlist.get(position).getRegdate());
            if (nlist.get(position).getFollow().equals("팔로우")) {
                ((ViewHolder2) holder).follow.setVisibility(View.VISIBLE);
                ((ViewHolder2) holder).following.setVisibility(View.GONE);
            } else {
                ((ViewHolder2) holder).follow.setVisibility(View.GONE);
                ((ViewHolder2) holder).following.setVisibility(View.VISIBLE);
            }

//            ((ViewHolder2) holder).follow.setOnClickListener(new View.OnClickListener() { //팔로우!!!
//
//                Integer sender_no = nlist.get(position).getSender_no();
//                Integer receiver_no = nlist.get(position).getReceiver_no();
//                @Override
//                public void onClick(View v) {
//                    service = ApiUtils.getAPIService();
//                    Call<API_Result> call = service.follow_regit(receiver_no,sender_no,"등록");
//                    call.enqueue(new Callback<API_Result>() {
//                        @Override
//                        public void onResponse(Call<API_Result> call, Response<API_Result> response) {
//                            String follow = response.body().getMessage();
//                            if(follow.equals("팔로잉")){
//                                ((ViewHolder2) holder).follow.setVisibility(View.GONE);
//                                ((ViewHolder2) holder).following.setVisibility(View.VISIBLE);
//                                Log.e("팔로잉!!!","팔로잉");
//                            }else{
//                                ((ViewHolder2) holder).follow.setVisibility(View.VISIBLE);
//                                ((ViewHolder2) holder).following.setVisibility(View.GONE);
//                                Log.e("팔로우!!!","팔로우");
//                            }
//                        }
//                        @Override
//                        public void onFailure(Call<API_Result> call, Throwable t) {
//                            t.getMessage();
//                        }
//                    });
//                }
//            });
//
//            ((ViewHolder2) holder).following.setOnClickListener(new View.OnClickListener() { //언팔로우!!!
//                Integer sender_no = nlist.get(position).getSender_no();
//                Integer receiver_no = nlist.get(position).getReceiver_no();
//                @Override
//                public void onClick(View v) {
//                    service = ApiUtils.getAPIService();
//                    Call<API_Result> call = service.follow_unfollow(receiver_no,sender_no,"취소");
//                    call.enqueue(new Callback<API_Result>() {
//                        @Override
//                        public void onResponse(Call<API_Result> call, Response<API_Result> response) {
//                            String follow = response.body().getMessage();
//                            if(follow.equals("팔로잉")){
//                                ((ViewHolder2) holder).follow.setVisibility(View.GONE);
//                                ((ViewHolder2) holder).following.setVisibility(View.VISIBLE);
//                                Log.e("팔로잉!!!","팔로잉");
//                            }else{
//                                ((ViewHolder2) holder).follow.setVisibility(View.VISIBLE);
//                                ((ViewHolder2) holder).following.setVisibility(View.GONE);
//                                Log.e("팔로우!!!","팔로우");
//                            }
//                        }
//                        @Override
//                        public void onFailure(Call<API_Result> call, Throwable t) {
//                            t.getMessage();
//                        }
//                    });
//                }
//            });


        }

    }




    private NotificationItem getItem(int position) {
        return nlist.get(position);
    }

    @Override
    public int getItemCount() {
        return nlist.size();
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout likeLayout;
        private ImageView uimage, picture;//유저이미지,글이미지
        private TextView sender, desc, regdate;//보낸사람,설명,등록일자..

        public ViewHolder1(View itemView) {
            super(itemView);
            likeLayout = itemView.findViewById(R.id.noti_layout_like);
            uimage = itemView.findViewById(R.id.noti_uimage);
//            sender = itemView.findViewById(R.id.noti_sender);
            desc = itemView.findViewById(R.id.noti_desc);
            regdate = itemView.findViewById(R.id.noti_regdate);
            picture = itemView.findViewById(R.id.noti_picture);

            itemView.setOnClickListener(this);
        }

        private ImageView getBtnImage() {
            return this.picture;
        }

        @Override
        public void onClick(View v) {
            //여기 뭐 들어와야되고 .. .

        }

    }

    public class ViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout followLayout;
        private ImageView uimage, picture;//유저이미지,글이미지
        private TextView sender, desc, regdate;//보낸사람,설명,등록일자..
        private Button follow, following;//팔로우,팔로잉

        public ViewHolder2(View itemView) {
            super(itemView);
            followLayout = itemView.findViewById(R.id.noti_layout_follow);
            uimage = itemView.findViewById(R.id.noti_uimage);
//            sender = itemView.findViewById(R.id.noti_sender);
            desc = itemView.findViewById(R.id.noti_desc);
            regdate = itemView.findViewById(R.id.noti_regdate);
            follow = itemView.findViewById(R.id.noti_btn_follow);
            following = itemView.findViewById(R.id.noti_btn_following);

            itemView.setOnClickListener(this);
        }

        private Button getBtnFollow() {
            return this.follow;
        }

        private Button getBtnFollowing() {
            return this.following;
        }

        @Override
        public void onClick(View v) {
            //여기 뭐 들어와야되고 .. .

        }

    }
}
