package com.example.joanne.selfinsns_;

import android.annotation.SuppressLint;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.resource.bitmap.CircleCrop;
//import com.bumptech.glide.request.RequestOptions;
import com.example.joanne.selfinsns_.retrofit.model.CommentItem;
import com.example.joanne.selfinsns_.retrofit.remote.APIService;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_A = 0;
    public static final int VIEW_TYPE_B = 1;

    private List<CommentItem> clist;
    private Context context;

    //대댓글 리사이클러뷰
//    private List<CommentItem> cclist;//아이템리스트.
    private OnItemClickListener onItemClickListener;

    private int sb_id, writer_no, depth, group, id;
    private String comment;
    private SharedPreferences sp;


    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public interface ItemLongClickListener {
        void onItemLongClick(View v,int pos);
    }

    public CommentAdapter(Context context, List<CommentItem> clist) {
        this.clist = clist;
        this.context = context;
    }

    public CommentAdapter(String comment) {
        this.comment = comment;
    }

    public CommentAdapter(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_A) {
            View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
            Log.e("onCreateViewHolder","댓글");
            return new Holder(v1);
        } else {
            View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_of_comment_item, parent, false);
            Log.e("onCreateViewHolder","대댓글");
            return new Holder2(v2);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (clist.get(position).getDepth() == 0) {
            Log.e("getItemViewType","댓글");

            return VIEW_TYPE_A;
        } else {
            Log.e("getItemViewType","대댓글");
            return VIEW_TYPE_B;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        CommentItem item = clist.get(position);
        depth = clist.get(position).getDepth();
//        if (holder instanceof Holder) {
            if(depth==0){
            Log.e("onBindViewHolder", "댓글");

            ((Holder) holder).writer_id.setText(clist.get(position).getWriterId());
            ((Holder) holder).comment.setText(clist.get(position).getComment());
            ((Holder) holder).regdate.setText(clist.get(position).getRegdate());
            //작성자이미지 셋팅하기
            Glide.with(context)
                    .load("http://13.209.108.67/uploads/" + clist.get(position).getWriter_image())
                    .centerCrop()
//                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(((Holder) holder).writer_image);

            //작성자이미지 둥글게 만들기
            ((Holder) holder).writer_image.setBackground(new ShapeDrawable(new OvalShape()));
            if (Build.VERSION.SDK_INT >= 21) {
                ((Holder) holder).writer_image.setClipToOutline(true);
            }
            ((Holder) holder).writer_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ((Holder) holder).getBtnReply().setOnClickListener(new View.OnClickListener() {  //답글 버튼
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View v) {
//                onItemClickListener.onItemClick(v,position);
                    //키보드 띄우기
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    Log.e("키보드띄우기","ㅇㅇ");
                    //글id, 작성자id, depth 가져오기........
                    sb_id = clist.get(position).getSbId();
                    depth = clist.get(position).getDepth();
                    group= clist.get(position).getGroup();

                    sp = context.getSharedPreferences("comment", Activity.MODE_PRIVATE); //로그인한 유저 정보를 저장해놓은 셰어드
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("sb_id", sb_id);
                    editor.putInt("group", group);
                    editor.putInt("depth", depth + 1);
                    editor.commit();

                    Log.e("답글버튼 누름 sb_id", String.valueOf(sb_id));
                    Log.e("답글버튼 누름 depth", String.valueOf(depth));
                    Log.e("답글버튼 누름 group", String.valueOf(group));

                }
            });

            ((Holder) holder).setItemLongClickListener(new ItemLongClickListener() {
                @Override
                public void onItemLongClick(View v, int pos) {

                }
            });

        } else {
            Log.e("onBindViewHolder", "대댓글");

            ((Holder2) holder).writer_id2.setText(clist.get(position).getWriterId());
            ((Holder2) holder).comment2.setText(clist.get(position).getComment());
            ((Holder2) holder).regdate2.setText(clist.get(position).getRegdate());
            //작성자이미지 셋팅하기
            Glide.with(context)
                    .load("http://13.209.108.67/uploads/" + clist.get(position).getWriter_image())
//                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(((Holder2) holder).writer_image2);
                ((Holder2) holder).writer_image2.setBackground(new ShapeDrawable(new OvalShape()));
                if (Build.VERSION.SDK_INT >= 21) {
                    ((Holder2) holder).writer_image2.setClipToOutline(true);
                }
                ((Holder2) holder).writer_image2.setScaleType(ImageView.ScaleType.CENTER_CROP);

            ((Holder2) holder).getBtnReply().setOnClickListener(new View.OnClickListener() {  //답글 버튼
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View v) {
                    //키보드 띄우기
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                    //글id, 작성자id, depth 가져오기........
                    sb_id = clist.get(position).getSbId();
                    depth = clist.get(position).getDepth();
                    group= clist.get(position).getGroup();

                    sp = context.getSharedPreferences("comment", Activity.MODE_PRIVATE); //로그인한 유저 정보를 저장해놓은 셰어드
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("sb_id", sb_id);
                    editor.putInt("depth", depth + 1);
                    editor.putInt("group", group);
                    editor.commit();
                    Log.e("답글버튼 누름 sb_id", String.valueOf(sb_id));
                    Log.e("답글버튼 누름 depth", String.valueOf(depth));
                    Log.e("답글버튼 누름 group", String.valueOf(group));

                }
            });
                ((Holder2) holder).setItemLongClickListener(new ItemLongClickListener() {
                    @Override
                    public void onItemLongClick(View v, int pos) {

                    }
                });


            }
    }

    private CommentItem getItem(int position) {
        return clist.get(position);
    }

    public void setItem(List<CommentItem> items) {
        clist.clear();
        clist.addAll(items);
    }

    public void addItem(CommentItem item) {
        clist.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        clist.remove(position);
        this.notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return clist.size();
    }


    public class Holder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        private LinearLayout commentLayout;
        private ImageView writer_image;//작성자프로필이미지,더보기(수정,삭제) 버튼
        private TextView writer_id, comment, regdate;//작성자아이디,코멘트,작성일자,답글달기
        private TextView btnReply;
        ItemLongClickListener itemLongClickListener;

        public Holder(View v) {
            super(v);
            commentLayout = v.findViewById(R.id.comment_item_layout);
            writer_image = v.findViewById(R.id.comment_uimage);
            writer_id = v.findViewById(R.id.comment_uid);
            comment = v.findViewById(R.id.comment_ucomment);
            regdate = v.findViewById(R.id.comment_date);

            btnReply = (TextView) v.findViewById(R.id.comment_btnreply);
            v.setOnLongClickListener(this);
        }

        public TextView getBtnReply() {
            return this.btnReply;
        }

        private void addItem(int position, CommentItem item) {
            clist.add(position, item);
            notifyItemInserted(position);
        }

        public void setItemLongClickListener(ItemLongClickListener ic)
        {
            this.itemLongClickListener=ic;
        }

        @Override
        public boolean onLongClick(View v) {
            this.itemLongClickListener.onItemLongClick(v,getLayoutPosition());
            return false;
        }
    }


    public class Holder2 extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        private LinearLayout commentLayout2;
        private ImageView writer_image2;//작성자프로필이미지,더보기(수정,삭제) 버튼
        private TextView writer_id2, comment2, regdate2;//작성자아이디,코멘트,작성일자,답글달기
        private TextView btnReply2;
        ItemLongClickListener itemLongClickListener;

        public Holder2(View itemView) {
            super(itemView);
            commentLayout2 = itemView.findViewById(R.id.comment_item2);
            writer_image2 = itemView.findViewById(R.id.comment_uimage2);
            writer_id2 = itemView.findViewById(R.id.comment_uid2);
            comment2 = itemView.findViewById(R.id.comment_ucomment2);
            regdate2 = itemView.findViewById(R.id.comment_date2);

            btnReply2 = (TextView) itemView.findViewById(R.id.comment_reply2);
        }

        public TextView getBtnReply() {
            return this.btnReply2;
        }

        private void addItem(int position, CommentItem item) {
            clist.add(position, item);
            notifyItemInserted(position);
        }

        public void setItemLongClickListener(ItemLongClickListener ic)
        {
            this.itemLongClickListener=ic;
        }

        @Override
        public boolean onLongClick(View v) {
            this.itemLongClickListener.onItemLongClick(v,getLayoutPosition());
            return false;
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
