package com.example.joanne.selfinsns_;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.joanne.selfinsns_.retrofit.model.ChatRoomItem;

import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {

    private List<ChatRoomItem> cList;
    private Context context;

    public ChatRoomAdapter(Context context, List<ChatRoomItem> cList) {
        this.cList = cList;
        this.context = context;
    }


    @Override
    public ChatRoomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatroom_item, parent, false);
        Log.e("onCreateViewHolder", "댓글");
        return new ChatRoomAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChatRoomAdapter.ViewHolder holder, int position) {
        if(cList.get(position).getKindof().equals("out")){
            //        Glide.with(context)//이미지
//                .load("http://13.209.108.67/uploads/" + cList.get(position).getChatroom_groupimage())
////                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
//                .into(holder.groupImage);

            holder.groupName.setText(cList.get(position).getGroup_name());
            holder.groupMessage.setVisibility(View.GONE);
            holder.groupTime.setVisibility(View.GONE);
            holder.groupCount.setText(cList.get(position).getCount().toString());
            Glide.with(context)//이미지
                    .load("http://13.209.108.67/uploads/" + cList.get(position).getLeaderImage())
                    .into(holder.groupLeaderImage);
            holder.groupLeaderName.setText(cList.get(position).getLeaderName());
        }else{
            //        Glide.with(context)//이미지
//                .load("http://13.209.108.67/uploads/" + cList.get(position).getChatroom_groupimage())
////                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
//                .into(holder.groupImage);
            holder.groupName.setText(cList.get(position).getGroup_name());
            holder.groupMessage.setText(cList.get(position).getMessage());
            holder.groupTime.setText(cList.get(position).getTime());
            holder.groupCount.setText(cList.get(position).getCount().toString());
            Glide.with(context)//이미지
                    .load("http://13.209.108.67/uploads/" + cList.get(position).getLeaderImage())
                    .into(holder.groupLeaderImage);
            holder.groupLeaderName.setText(cList.get(position).getLeaderName());
        }

    }


    public ChatRoomItem getItem(int position) {
        return cList.get(position);
    }

    @Override
    public int getItemCount() {
        return cList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout chatroomLayout;
        private ImageView groupImage, groupLeaderImage;
        private TextView groupName, groupMessage, groupTime, groupLeaderName, groupCount;

        public ViewHolder(View itemView) {
            super(itemView);

            chatroomLayout = itemView.findViewById(R.id.chatroom_linearLayout);
            groupImage = itemView.findViewById(R.id.chatroom_groupimage);
            groupName = itemView.findViewById(R.id.group_name);
            groupMessage = itemView.findViewById(R.id.group_message);
            groupTime = itemView.findViewById(R.id.group_time);
            groupCount = itemView.findViewById(R.id.group_count);
            groupLeaderName = itemView.findViewById(R.id.group_leaderName);
            groupLeaderImage = itemView.findViewById(R.id.group_leaderImage);


        }

        private void addItem(int position, ChatRoomItem item) {
            cList.add(position, item);
            notifyItemInserted(position);
        }

    }

    public static class RecyclerViewOnItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
        private ChatRoomAdapter.RecyclerViewOnItemClickListener.OnItemClickListener mListener;
        private GestureDetector mGestureDetector;

        public RecyclerViewOnItemClickListener(Context context, final RecyclerView recyclerView, ChatRoomAdapter.RecyclerViewOnItemClickListener.OnItemClickListener listener) {
            this.mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (childView != null && mListener != null) {
                        mListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
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
