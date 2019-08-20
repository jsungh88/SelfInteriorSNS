package com.example.joanne.selfinsns_;

import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.joanne.selfinsns_.retrofit.model.KnowHowItem;

import java.util.List;

/**
 * Created by Joanne on 2018-05-23.
 */

/**
 * 노하우등록 3단계 미리보기 중, 이미지+이미지설명 영역 리사이클러뷰
 */
public class KnowHowImageAdapter extends RecyclerView.Adapter<KnowHowImageAdapter.ViewHolder>{

    KnowHowWrite_Step3 context;
    List<KnowHowItem> items;

    public KnowHowImageAdapter(KnowHowWrite_Step3 context, List<KnowHowItem> items){
        this.context = context;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View view = layoutInflater.inflate( R.layout.knowhow_write_itemmodel,parent,false );

        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams)holder.itemView.getLayoutParams();
//        layoutParams.height = layoutParams.width;
//        holder.itemView.requestLayout();

        KnowHowItem item = items.get( position );
        holder.image.setImageURI( Uri.parse( items.get( position ).getImage() ) );
        holder.image_desc.setText( items.get( position ).getImage_desc() );
    }

    @Override
    public int getItemCount() {

        return this.items.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout knowhowwriteLayout;
        ImageView image;
        TextView image_desc;

        public ViewHolder(View itemView) {
            super( itemView );
            knowhowwriteLayout = itemView.findViewById( R.id.knowhow_write_item_layout );
            image = itemView.findViewById( R.id.khwrite_image );
            image_desc = itemView.findViewById( R.id.image_desc );
        }
    }

}

