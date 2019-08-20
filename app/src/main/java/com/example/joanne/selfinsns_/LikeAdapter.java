package com.example.joanne.selfinsns_;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
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
//import com.bumptech.glide.load.resource.bitmap.CircleCrop;
//import com.bumptech.glide.request.RequestOptions;
import com.example.joanne.selfinsns_.retrofit.model.LikerItem;

import java.util.List;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ViewHolder> {

    private List<LikerItem> lList;
    private Context context;

    public LikeAdapter(Context context, List<LikerItem> items) {
        this.lList = items;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.like_item, parent, false);
        Log.e("onCreateViewHolder","댓글");
        return new ViewHolder(v);
    }


    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        LikerItem item = lList.get(position);

        Glide.with(context)//이미지
                .load("http://13.209.108.67/uploads/" + lList.get(position).getPicture())
//                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(holder.uImage);


        holder.uName.setText(lList.get(position).getName());
        holder.uEmail.setText(lList.get(position).getEmail());
        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//팔로우하기


            }
        });
        holder.btnFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//팔로우 취소하기

            }
        });
    }

    private LikerItem getItem(int position){
        return lList.get(position);
    }


    @Override
    public int getItemCount() {
        return lList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private LinearLayout likeLayout;
        private ImageView uImage;//작성자프로필이미지
        private TextView uName, uEmail;//좋아요한사람이름, 좋아요이메일
        private Button btnFollow,btnFollowing;//팔로우버튼
//        ItemLongClickListener itemLongClickListener;

        public ViewHolder(View v) {
            super(v);
            likeLayout = v.findViewById(R.id.like_linearlayout);
            uImage = v.findViewById(R.id.like_uimage);
            uName = v.findViewById(R.id.like_uname);
            uEmail = v.findViewById(R.id.like_uemail);
            btnFollow = (Button)v.findViewById(R.id.like_btn_follow);
            btnFollowing = (Button)v.findViewById(R.id.btn_following);
//            v.setOnLongClickListener(this);
        }

        private void addItem(int position, LikerItem item) {
            lList.add(position, item);
            notifyItemInserted(position);
        }

        @Override
        public void onClick(View v) {

        }

        public Button getBtnFollow(){
            return this.btnFollow;
        }
        public Button getBtnFollowing(){
            return this.btnFollowing;
        }
//        public void setItemLongClickListener(ItemLongClickListener ic)
//        {
//            this.itemLongClickListener=ic;
//        }
//
//        @Override
//        public boolean onLongClick(View v) {
//            this.itemLongClickListener.onItemLongClick(v,getLayoutPosition());
//            return false;
//        }
    }


}
