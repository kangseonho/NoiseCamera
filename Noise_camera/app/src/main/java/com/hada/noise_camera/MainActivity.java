package com.hada.noise_camera;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;

import android.app.Activity;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Size;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private TextureView mCameraTextureView;
    private CameraTextureView mPreview;
    private Button mNormalAngleButton;
    private Button mWideAngleButton;
    private Button mCameraCaptureButton;
    private Button mCameraDirectionButton;
    private ImageView grid, noiseimg;

    Activity mainActivity = this;

    private static final String TAG = "MAINACTIVITY";

    static final int REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Display display = getWindowManager().getDefaultDisplay();

        int width = display.getWidth();
        Log.d("width", "onCreate: "+width);

//        mNormalAngleButton = (Button) findViewById(R.id.camera_front_back);
//        mWideAngleButton = (Button) findViewById(R.id.wide);
        mCameraCaptureButton = (Button) findViewById(R.id.bt_shutter);
        mCameraDirectionButton = (Button) findViewById(R.id.camera_front_back);
        mCameraTextureView = (TextureView) findViewById(R.id.cameraTextureView);
        noiseimg = (ImageView) findViewById(R.id.noiseimg);
        grid = (ImageView) findViewById(R.id.grid);
        //화면의 가로 길이를구하여 세로길이를 4대3 비율로 정해줌
        mCameraTextureView.getLayoutParams().height= width*4/3;
        noiseimg.getLayoutParams().height= width*4/3;
        grid.getLayoutParams().height= width*4/3;
        mPreview = new CameraTextureView(this, mCameraTextureView, mNormalAngleButton, mWideAngleButton, mCameraCaptureButton, mCameraDirectionButton,noiseimg,mainActivity);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA:
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (permission.equals(Manifest.permission.CAMERA)) {
                        if (grantResult == PackageManager.PERMISSION_GRANTED) {
                            mCameraTextureView = (TextureView) findViewById(R.id.cameraTextureView);
                            mPreview = new CameraTextureView(mainActivity, mCameraTextureView, mNormalAngleButton, mWideAngleButton, mCameraCaptureButton, mCameraDirectionButton,noiseimg,mainActivity);
                            mPreview.openCamera();
                            Log.d(TAG, "mPreview set");
                        } else {
                            Toast.makeText(this, "Should have camera permission to run", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPreview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.onPause();
    }

    public void onClickGallery(View view) {
        Intent gallery = new Intent(getApplicationContext(), GalleryView.class);
        startActivity(gallery);
    }
//    CameraSurfaceView surfaceView;
//    ImageView imageView;
//    static final int REQUEST_CAMERA = 1;
//    private TextureView mCameraTextureView;
//    private CameraTextureView mPreview;
//
//    private Mat matInput;
//    Activity mainActivity = this;
//    private static final String TAG = "MAINACTIVITY";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        mCameraTextureView = (TextureView) findViewById(R.id.cameraTextureView);
//        mPreview = new CameraTextureView(this, mCameraTextureView);
//
////        surfaceView = findViewById(R.id.surfaceview);
//        imageView = findViewById(R.id.imageView);
//        TedPermission.with(getApplicationContext())
//                .setPermissionListener(permissionListener)
//                .setRationaleMessage("카메라 권한이 필요합니다.")
//                .setDeniedMessage("거부하셨습니다.")
//                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE)
//                .check();
//        Button button = findViewById(R.id.bt_shutter);
//        Button camera_front_back = findViewById(R.id.camera_front_back);
//
//
//
//
//        camera_front_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                capture();
//            }
//        });
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case REQUEST_CAMERA:
//                for (int i = 0; i < permissions.length; i++) {
//                    String permission = permissions[i];
//                    int grantResult = grantResults[i];
//                    if (permission.equals(Manifest.permission.CAMERA)) {
//                        if(grantResult == PackageManager.PERMISSION_GRANTED) {
//                            mCameraTextureView = (TextureView) findViewById(R.id.cameraTextureView);
//                            mPreview = new CameraTextureView(mainActivity, mCameraTextureView);
//                            Log.d(TAG,"mPreview set");
//                        } else {
//                            Toast.makeText(this,"Should have camera permission to run", Toast.LENGTH_LONG).show();
//                            finish();
//                        }
//                    }
//                }
//                break;
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mPreview.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mPreview.onPause();
//    }


//    public void capture(){
//        surfaceView.capture(new Camera.PictureCallback() {
//            @Override
//            public void onPictureTaken(byte[] data, Camera camera) {
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inSampleSize = 8;
//                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                OpenCVLoader.initDebug();
//
////                imageView.setImageBitmap(bmp);
//                matInput = new Mat();
//                Utils.bitmapToMat(bmp,matInput);
//
//
//                Mat noise = new Mat(matInput.size(), matInput.type());
//                Log.d("data.lengthSize", data.length+""+matInput.type());
//                Log.d("Bitmap.lengthSize", bmp.getWidth()+""+bmp.getHeight());
//                Log.d("matInputSize", matInput.size()+"");
//
//                MatOfDouble mean = new MatOfDouble ();
//                MatOfDouble dev = new MatOfDouble ();
//                Core.meanStdDev(matInput,mean,dev);
//
////                Core.randn(noise,mean.get(0,0)[0], dev.get(0,0)[0]);
//                Core.randn(noise,0.0, 30.0);
//
//                Core.add(matInput, noise, matInput);
//
//                Utils.matToBitmap(matInput,bmp);
//                Core.rotate(matInput,matInput,Core.ROTATE_90_CLOCKWISE);
//                // 이미지 중심으로 90도 회전 Matrix
//                Matrix matrix = new Matrix();
//                matrix.preRotate(90, 0, 0);
//                // 이미지 회전
//                Bitmap mbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
//                Utils.matToBitmap(matInput,mbmp);
//
//
//                Core.rotate(noise,noise,Core.ROTATE_90_CLOCKWISE);
//                Bitmap noisebmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
//                Utils.matToBitmap(noise,noisebmp);
//
////
//                imageView.setImageBitmap(noisebmp);
//
//                camera.startPreview();
//            }
//        });
//    }
    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(getApplicationContext(), "권한이 허용됨",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한이 거부됨",Toast.LENGTH_SHORT).show();
        }
    };
}