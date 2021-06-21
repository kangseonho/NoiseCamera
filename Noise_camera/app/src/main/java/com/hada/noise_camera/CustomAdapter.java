package com.hada.noise_camera;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
    private ArrayList<Integer> mBitmaps;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView iv;

        public CustomViewHolder(View view) {
            super(view);
            this.iv = (ImageView) view.findViewById(R.id.id_listitem);
        }
    }

    public CustomAdapter(ArrayList<Integer> list) {
        this.mBitmaps = list;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewHolder, int position) {

        viewHolder.iv.setImageResource(mBitmaps.get(position));
    }

    @Override
    public int getItemCount() {
        return (null != mBitmaps ? mBitmaps.size() : 0);
    }
}
