package com.hada.noise_camera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.GridView;

import java.util.ArrayList;

public class GalleryView extends AppCompatActivity {

    private ArrayList<Integer> mItems;
    private CustomAdapter mAdapter;
    private int count = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this,3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mItems = new ArrayList<>();

        mItems.add(R.drawable.shutterimg);
        mItems.add(R.drawable.shutterimg);
        mItems.add(R.drawable.shutterimg);
        mItems.add(R.drawable.shutterimg);
        mItems.add(R.drawable.shutterimg);
        mItems.add(R.drawable.shutterimg);
        mItems.add(R.drawable.shutterimg);
        mItems.add(R.drawable.shutterimg);
        mItems.add(R.drawable.shutterimg);
        mItems.add(R.drawable.shutterimg);
        mItems.add(R.drawable.shutterimg);
        mItems.add(R.drawable.shutterimg);
        mItems.add(R.drawable.shutterimg);
        mItems.add(R.drawable.shutterimg);
        mItems.add(R.drawable.shutterimg);


        mAdapter = new CustomAdapter(mItems);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mGridLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

    }
}