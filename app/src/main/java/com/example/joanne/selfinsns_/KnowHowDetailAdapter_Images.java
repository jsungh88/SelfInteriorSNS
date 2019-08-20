package com.example.joanne.selfinsns_;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.joanne.selfinsns_.retrofit.model.KnowHowItem;

import java.util.ArrayList;
import java.util.List;

public class KnowHowDetailAdapter_Images extends RecyclerView.Adapter<KnowHowDetailAdapter_Images.Holder> {

    private List<KnowHowItem> iList;
    private Context context;
    public KnowHowDetailAdapter_Images(Context context, List<KnowHowItem> iList) {
        this.iList = iList;
        this.context = context;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.knowhow_write_itemmodel, parent, false);
        Log.e("onCreateViewHolder","댓글");
        return new Holder(v1);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        KnowHowItem item = iList.get(position);
        Glide.with(context)
                .load("http://13.209.108.67/uploads/" + iList.get(position).getWriter_image())
                .centerCrop()
                .into(holder.knowhow_image);
        holder.knowhow_desc.setText(iList.get(position).getImage_desc());

    }


    @Override
    public int getItemCount() {
        return iList.size();
    }

    private KnowHowItem getItem(int position){
        return iList.get(position);
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private LinearLayout knowhowLayout;
        private ImageView knowhow_image;
        private TextView knowhow_desc;

        public Holder(View v) {
            super(v);

            knowhowLayout = itemView.findViewById(R.id.knowhow_write_item_layout);
            knowhow_image = itemView.findViewById(R.id.khwrite_image);
            knowhow_desc = itemView.findViewById(R.id.image_desc);

        }

        @Override
        public void onClick(View v) {

        }
    }


}
