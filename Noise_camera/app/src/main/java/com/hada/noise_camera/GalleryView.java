    package com.hada.noise_camera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.GridView;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class GalleryView extends AppCompatActivity {

    private ArrayList<String> urls = new ArrayList<>();
    private CustomAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view);

        getAllPhotos();

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this,3);
        RecyclerView.ItemDecoration dividerItemDecoration =
                new DividerItemDecorator(ContextCompat.getDrawable(this,R.drawable.divider));
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.addItemDecoration(dividerItemDecoration);


        mAdapter = new CustomAdapter(this,urls);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(dividerItemDecoration);

    }

    private void getAllPhotos() {
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, //the album it in
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,null,null,MediaStore.Images.ImageColumns.DATE_ADDED + " DESC");

        if(cursor != null) {
            while (cursor.moveToNext()) {
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                urls.add(url);
            }
            cursor.close();
        }
    }
}