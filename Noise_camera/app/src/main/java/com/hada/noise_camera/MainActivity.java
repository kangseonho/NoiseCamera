package com.hada.noise_camera;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;

import android.app.Activity;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
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
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private TextureView mCameraTextureView;
    private CameraTextureView mPreview;
    private Button mNormalAngleButton;
    private Button mWideAngleButton;
    private Button mCameraCaptureButton;
    private Button mCameraDirectionButton;
    private ImageView grid, noiseimg, title, bt_gallery;
    private Mat matInput;
    Activity mainActivity = this;
    private int width,height;
    private static final String TAG = "MAINACTIVITY";

    static final int REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Display display = getWindowManager().getDefaultDisplay();

        width = display.getWidth();
        height = display.getHeight();

        Log.d("width", "onCreate: "+width);

//        mNormalAngleButton = (Button) findViewById(R.id.camera_front_back);
//        mWideAngleButton = (Button) findViewById(R.id.wide);
        mCameraCaptureButton = (Button) findViewById(R.id.bt_shutter);
        mCameraDirectionButton = (Button) findViewById(R.id.camera_front_back);
        mCameraTextureView = (TextureView) findViewById(R.id.cameraTextureView);
        noiseimg = (ImageView) findViewById(R.id.noiseimg);
        grid = (ImageView) findViewById(R.id.grid);
        title = (ImageView) findViewById(R.id.title);
        bt_gallery = (ImageView) findViewById(R.id.bt_gallery);

        //화면의 가로 길이를구하여 세로길이를 4대3 비율로 정해줌
        mCameraTextureView.getLayoutParams().height= width*4/3;
        noiseimg.getLayoutParams().height= width*4/3;
        grid.getLayoutParams().height= width*4/3;


        //버튼 크기 정해주기
        Log.d("amolang2",width+"->"+width*15/100);
        mCameraCaptureButton.getLayoutParams().width = width*20/100;
        mCameraCaptureButton.getLayoutParams().height = width*20/100;
        mCameraDirectionButton.getLayoutParams().width = width*10/100;
        mCameraDirectionButton.getLayoutParams().height = width*10/100;
        title.getLayoutParams().width = width*30/100;
        bt_gallery.getLayoutParams().width = width*12/100;
        bt_gallery.getLayoutParams().height = width*12/100;

        Log.d("width", "onCreate: "+ width+","+ width*4/3);

        //초반 앱을 켤때에 노이즈 필터 적용
        OpenCVLoader.initDebug();
        Bitmap bmp5120 = BitmapFactory.decodeResource(this.getResources(), R.drawable.img_2560);
        Log.d("width", "onCreate: "+ bmp5120.getWidth()+","+ bmp5120.getHeight());

        Bitmap resized = Bitmap.createScaledBitmap(bmp5120,width, width*4/3, true);
        matInput = new Mat();
        Utils.bitmapToMat(resized, matInput);
        Mat noise = new Mat(matInput.size(), matInput.type());


        MatOfDouble mean = new MatOfDouble ();
        MatOfDouble dev = new MatOfDouble ();
        Core.meanStdDev(matInput,mean,dev);
        Core.randn(noise,0.0, 35.0);
        Matrix matrix = new Matrix();
        Bitmap noisebmp = Bitmap.createBitmap(resized, 0, 0, resized.getWidth(), resized.getHeight(), matrix, false);
        Utils.matToBitmap(noise,noisebmp);
        noiseimg.setImageBitmap(noisebmp);
        bmp5120.recycle();
        setRecentImageView();
        mPreview = new CameraTextureView(this, mCameraTextureView, mNormalAngleButton, mWideAngleButton, mCameraCaptureButton, mCameraDirectionButton,noiseimg,mainActivity,width,bt_gallery);
    }
    public void setRecentImageView(){
        //최신 사진 가져오는 부분
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, //the album it in
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };
        final Cursor cursor = getApplicationContext().getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                        null, MediaStore.Images.ImageColumns.DATE_ADDED + " DESC");

        if (cursor.moveToFirst()) {
            String imageLocation = cursor.getString(1);
            File imageFile = new File(imageLocation);
            if (imageFile.exists()) {   // TODO: is there a better way to do this?

                Bitmap image = BitmapFactory.decodeFile(imageLocation);//loading the large bitmap is fine.
                int w = image.getWidth();//get width
                int h = image.getHeight();//get height
                int aspRat = w / h;//get aspect ratio
                int W = width*15/100;//do whatever you want with width. Fixed, screen size, anything
                int H;
                if (aspRat>0) {
                    H = W * aspRat;//set the height based on width and aspect ratio
                }else {
                    H = W;
                }
                Log.d(TAG, "onCreate: width"+width+""+aspRat);
                Bitmap b = Bitmap.createScaledBitmap(image, W, H, false);//scale the bitmap
                bt_gallery.setImageBitmap(b);//set the image view
                image.recycle();//save memory on the bitmap called 'image'
            }
        }
    }
    private Bitmap compressBitmap(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,40, stream);
        byte[] byteArray = stream.toByteArray();
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        return compressedBitmap;
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
                            mPreview = new CameraTextureView(this, mCameraTextureView, mNormalAngleButton, mWideAngleButton, mCameraCaptureButton, mCameraDirectionButton,noiseimg,mainActivity,width,bt_gallery);
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