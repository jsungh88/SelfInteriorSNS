package com.example.joanne.selfinsns_;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.joanne.selfinsns_.retrofit.model.ChatItem;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final int VIEW_TYPE_RECEIVER = 0;
    public static final int VIEW_TYPE_SENDER = 1;
    public static final int VIEW_TYPE_NOTICE = 2;
    public static final int VIEW_TYPE_RECEIVER_IMAGE = 3;
    public static final int VIEW_TYPE_SENDER_IMAGE = 4;
    public List<ChatItem> chatList;
    private Context context;


    public ChatAdapter(Context context, List<ChatItem> chatList){
        this.context = context;
        this.chatList = chatList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_RECEIVER) {
            View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_received_message_item, parent, false);
            return new ReceiverHolder(v1);
        } else if(viewType == VIEW_TYPE_SENDER){
            View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sent_message_item, parent, false);
            return new SenderHolder(v2);
        } else if(viewType == VIEW_TYPE_NOTICE) {
            View v3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_notice_message_item, parent, false);
            return new NoticeHolder(v3);
        } else if(viewType == VIEW_TYPE_RECEIVER_IMAGE){
            View v4 = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_received_image_item, parent, false);
            return new ReceiverImageHolder(v4);
        } else{
            View v5 = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sent_image_item, parent, false);
            return new SenderImageHolder(v5);
        }
    }

    @Override
    public int getItemViewType(int position) {
         if(chatList.get(position).getWho().equals("receiver")){

            return VIEW_TYPE_RECEIVER;
        }else if(chatList.get(position).getWho().equals("sender")){
            return VIEW_TYPE_SENDER;
        }else if(chatList.get(position).getWho().equals("notice")){
            return VIEW_TYPE_NOTICE;
        }else if(chatList.get(position).getWho().equals("receiver_image")){
             return VIEW_TYPE_RECEIVER_IMAGE;
         }else{
             return VIEW_TYPE_SENDER_IMAGE;
         }
    }

    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatItem item = chatList.get(position);
        try {
            Log.e("item", item.getWho());
            Log.e("item", item.getUserName());
            Log.e("item", item.getMessage());
            Log.e("item", item.getTime());
        }catch(Exception e){

        }

        if (chatList.get(position).getWho().equals("receiver")) {
            Log.e("onBindViewHolder-sender",chatList.get(position).getUserName());
            Log.e("onBindViewHolder-sender",chatList.get(position).getMessage());
            Log.e("onBindViewHolder-sender",chatList.get(position).getTime());
            ((ReceiverHolder) holder).name.setText(chatList.get(position).getUserName());
            ((ReceiverHolder) holder).body.setText(chatList.get(position).getMessage());
            ((ReceiverHolder) holder).time.setText(chatList.get(position).getTime());
            //작성자이미지 셋팅하기
            Glide.with(context)
                    .load("http://13.209.108.67/uploads/" + chatList.get(position).getUserImage())
                    .centerCrop()
                    .into(((ReceiverHolder) holder).profile);

            //작성자이미지 둥글게 만들기
            ((ReceiverHolder) holder).profile.setBackground(new ShapeDrawable(new OvalShape()));
            if (Build.VERSION.SDK_INT >= 21) {
                ((ReceiverHolder) holder).profile.setClipToOutline(true);
            }
            ((ReceiverHolder) holder).profile.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else if (chatList.get(position).getWho().equals("sender")){
            Log.e("onBindViewHolder-receiver",chatList.get(position).getMessage());
            Log.e("onBindViewHolder-receiver",chatList.get(position).getTime());
            ((SenderHolder) holder).body.setText(chatList.get(position).getMessage());
            ((SenderHolder) holder).time.setText(chatList.get(position).getTime());
        }else if(chatList.get(position).getWho().equals("notice")){
            ((NoticeHolder)holder).notice.setText(chatList.get(position).getMessage());
        }else if(chatList.get(position).getWho().equals("receiver_image")){
            ((ReceiverImageHolder) holder).name.setText(chatList.get(position).getUserName());
            //이미지 셋팅하기
            Glide.with(context)
                    .load("http://13.209.108.67/uploads/" + chatList.get(position).getMessage())
                    .centerCrop()
                    .into(((ReceiverImageHolder) holder).image);
            Log.e("aaa이미지",chatList.get(position).getMessage());
            ((ReceiverImageHolder) holder).time.setText(chatList.get(position).getTime());
            //이미지 셋팅하기
            Glide.with(context)
                    .load("http://13.209.108.67/uploads/" + chatList.get(position).getUserImage())
                    .centerCrop()
                    .into(((ReceiverImageHolder) holder).profile);
            Log.e("aa이미지",chatList.get(position).getUserImage());
            //작성자이미지 둥글게 만들기
            ((ReceiverImageHolder) holder).profile.setBackground(new ShapeDrawable(new OvalShape()));
            if (Build.VERSION.SDK_INT >= 21) {
                ((ReceiverImageHolder) holder).profile.setClipToOutline(true);
            }
            ((ReceiverImageHolder) holder).profile.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }else{
            //이미지 셋팅하기
            Glide.with(context)
                    .load("http://13.209.108.67/uploads/" + chatList.get(position).getMessage())
                    .centerCrop()
                    .into(((SenderImageHolder) holder).image);
            Log.e("a이미지",chatList.get(position).getMessage());
            ((SenderImageHolder) holder).time.setText(chatList.get(position).getTime());
        }
    }

    private ChatItem getItem(int position){
        return chatList.get(position);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class SenderHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ConstraintLayout sendLayout;
        private TextView body,time;

        public SenderHolder(View itemView) {
            super(itemView);
            body = itemView.findViewById(R.id.text_message_body);
            time = itemView.findViewById(R.id.text_message_time);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public class ReceiverHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ConstraintLayout receivLayout;
        private TextView name,body,time;
        private ImageView profile;

        public ReceiverHolder(View itemView) {
            super(itemView);
          name = itemView.findViewById(R.id.text_message_name);
          body = itemView.findViewById(R.id.text_message_body);
          time = itemView.findViewById(R.id.text_message_time);
          profile = itemView.findViewById(R.id.image_message_profile);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public class NoticeHolder extends RecyclerView.ViewHolder{

        private ConstraintLayout noticeLayout;
        private TextView notice;
        public NoticeHolder(View itemView) {
            super(itemView);

            notice = itemView.findViewById(R.id.chat_notice);

        }
    }

    private class ReceiverImageHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout receivLayout;
        private TextView name,time;
        private ImageView profile,image;

        public ReceiverImageHolder(View v4) {
            super(v4);
            name = itemView.findViewById(R.id.text_message_name);
            image = itemView.findViewById(R.id.text_image_body);
            time = itemView.findViewById(R.id.text_message_time);
            profile = itemView.findViewById(R.id.image_message_profile);
        }
    }

    private class SenderImageHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout sendLayout;
        private TextView time;
        private ImageView image;


        public SenderImageHolder(View v5) {
            super(v5);
            image = itemView.findViewById(R.id.text_image_body);
            time = itemView.findViewById(R.id.text_message_time);
        }
    }
}
