package com.hada.noise_camera;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
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

    CameraSurfaceView surfaceView;
    ImageView imageView;

    private Mat matInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = findViewById(R.id.surfaceview);
        imageView = findViewById(R.id.imageView);

        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
        Button button = findViewById(R.id.bt_shutter);
        Button camera_front_back = findViewById(R.id.camera_front_back);

        camera_front_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                surfaceView.setCameraID(1);
//                surfaceView.surfaceDestroyed(surfaceView.getHolders());
//                surfaceView.surfaceCreated(surfaceView.getHolders());
////                Intent intent = getIntent();
////                startActivity(intent);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture();
            }
        });
    }

    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
//            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_, this, mLoaderCallback);
        } else {
            surfaceView.capture(new Camera.PictureCallback(){
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    Log.d("start()", "11111111");
                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    matInput = new Mat();
                    Utils.bitmapToMat(bmp,matInput);
                    Mat noise = new Mat(matInput.size(), matInput.type());
                    Core.randn(noise,0.0, 30.0);
                    Matrix matrix = new Matrix();
                    matrix.preRotate(90, 0, 0);
                    Core.rotate(noise,noise,Core.ROTATE_90_CLOCKWISE);
                    Bitmap noisebmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
                    Utils.matToBitmap(noise,noisebmp);
                    imageView = findViewById(R.id.imageView);
                    imageView.setImageBitmap(noisebmp);
                    Log.d("start()", matInput.size()+"");
                }
            });
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {

        }
    };
//    public void start(){
//        surfaceView.capture(new Camera.PictureCallback(){
//            @Override
//            public void onPictureTaken(byte[] data, Camera camera) {
//                Log.d("start()", "11111111");
//                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                matInput = new Mat();
//                Utils.bitmapToMat(bmp,matInput);
//                Mat noise = new Mat(matInput.size(), matInput.type());
//                Core.randn(noise,0.0, 30.0);
//                Matrix matrix = new Matrix();
//                matrix.preRotate(90, 0, 0);
//                Core.rotate(noise,noise,Core.ROTATE_90_CLOCKWISE);
//                Bitmap noisebmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
//                Utils.matToBitmap(noise,noisebmp);
//                imageView = findViewById(R.id.imageView);
//                imageView.setImageBitmap(noisebmp);
//                Log.d("start()", matInput.size()+"");
//
//            }
//        });
//    }

    public void capture(){
        surfaceView.capture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                OpenCVLoader.initDebug();

//                imageView.setImageBitmap(bmp);
                matInput = new Mat();
                Utils.bitmapToMat(bmp,matInput);


                Mat noise = new Mat(matInput.size(), matInput.type());
                Log.d("data.lengthSize", data.length+""+matInput.type());
                Log.d("Bitmap.lengthSize", bmp.getWidth()+""+bmp.getHeight());
                Log.d("matInputSize", matInput.size()+"");

                MatOfDouble mean = new MatOfDouble ();
                MatOfDouble dev = new MatOfDouble ();
                Core.meanStdDev(matInput,mean,dev);

//                Core.randn(noise,mean.get(0,0)[0], dev.get(0,0)[0]);
                Core.randn(noise,0.0, 30.0);

                Core.add(matInput, noise, matInput);

                Utils.matToBitmap(matInput,bmp);
                Core.rotate(matInput,matInput,Core.ROTATE_90_CLOCKWISE);
                // 이미지 중심으로 90도 회전 Matrix
                Matrix matrix = new Matrix();
                matrix.preRotate(90, 0, 0);
                // 이미지 회전
                Bitmap mbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
                Utils.matToBitmap(matInput,mbmp);


//                Core.rotate(noise,noise,Core.ROTATE_90_CLOCKWISE);
//                Bitmap noisebmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
//                Utils.matToBitmap(noise,noisebmp);
//
//
//                imageView.setImageBitmap(noisebmp);

                camera.startPreview();
            }
        });
    }
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