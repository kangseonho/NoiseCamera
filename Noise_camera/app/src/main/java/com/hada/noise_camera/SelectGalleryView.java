package com.hada.noise_camera;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.app.Activity;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

public class SelectGalleryView extends AppCompatActivity {

    private int currentPosition = 0;
    private ArrayList<String> urls = new ArrayList<>();
    private ArrayList<Integer> ids = new ArrayList<>();
    private ImageView currentImage;
    private TextView textView;
    private final int DELETE_PERMISSION_REQUEST = 0x1033;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_gallery_view);
        Bundle bundle = getIntent().getExtras();
        currentPosition = bundle.getInt("position");
        getAllPhotos();

        textView = (TextView)findViewById(R.id.count);
        textView.setText((currentPosition+1)+"/"+urls.size());
        currentImage = (ImageView)findViewById(R.id.currentImage);
        currentImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this).load(urls.get(currentPosition)).into(currentImage);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void CancleButton(View view) throws IntentSender.SendIntentException {
        System.out.println(ids.size());
        try {
            Uri contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    ids.get(currentPosition).toString()
            );
            ContentResolver contentResolver = getContentResolver();
            contentResolver.delete(contentUri,null,null);
        } catch (RecoverableSecurityException e) {
           IntentSender intentSender = e.getUserAction().getActionIntent().getIntentSender();
           startIntentSenderForResult(intentSender, DELETE_PERMISSION_REQUEST,null,0,0,0,null);
        }
        getAllPhotos();
        currentImage = (ImageView)findViewById(R.id.currentImage);
        currentImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this).load(urls.get(currentPosition)).into(currentImage);
    }

    private void getAllPhotos() {
        urls.clear();
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,null,null,MediaStore.Images.ImageColumns.DATE_ADDED + " DESC");

        if(cursor != null) {
            while (cursor.moveToNext()) {
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                ids.add(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)));
                urls.add(url);
            }
            cursor.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Activity.RESULT_OK && requestCode == DELETE_PERMISSION_REQUEST) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),GalleryView.class);
        startActivity(intent);
    }
}