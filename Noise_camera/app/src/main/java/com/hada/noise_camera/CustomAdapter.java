package com.hada.noise_camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder>{
    ArrayList<String> items = new ArrayList<String>();
    Context mContext;
    private OnItemClickListener mListener = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView iv;

        public CustomViewHolder(View view) {
            super(view);
            this.iv = (ImageView) view.findViewById(R.id.id_listitem);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION) {
                        if(mListener != null){
                            mListener.onItemClick(v,position);
                        }
                    }
                }
            });
        }
    }

    public CustomAdapter(Context context,ArrayList<String> list) {
        this.items = list;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewHolder, int position) {
        viewHolder.iv.setPadding(2,2,2,2);
        viewHolder.iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(mContext).load(items.get(position)).into(viewHolder.iv);
    }

    @Override
    public int getItemCount(){
        return items.size();
    }
}
