package com.hada.noisecamera;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "opencv";
    private Mat matInput;
    private Mat matResult;

    private Uri photoUri;
    ImageView iv_result;

    File file;
//    private CameraBridgeViewBase mOpenCvCameraView;

    public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);

    public native void ConvertNoise(long matAddrInput, long matAddrResult, int a);

    private final Semaphore writeLock = new Semaphore(1);

    public void getWriteLock() throws InterruptedException {
        writeLock.acquire();
    }

    public void releaseWriteLock() {
        writeLock.release();
    }

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File sdcard = Environment.getExternalStorageDirectory();
        file = new File(sdcard, "capture.jpg");

        iv_result = findViewById(R.id.image);

        // 권한 체크
        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture();
            }
        });

    }

    private Uri cameraImageUri = null;

    public void capture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoUri = FileProvider.getUriForFile(this, "com.hada.noisecamera.fileprovider", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", requestCode + resultCode + "");
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            iv_result.setImageBitmap(bitmap);
            try {
                getWriteLock();
                matInput = new Mat();
                Utils.bitmapToMat(bitmap, matInput);
                if (matResult == null)
                    matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());
                releaseWriteLock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            saveImage();
//            iv_result.setImageMatrix(matResult);
            saveImage1();
            saveImage2();

        }
    }
    public void saveImage() {
        try {
            getWriteLock();

            File path = new File(Environment.getExternalStorageDirectory() + "/Images/");
            path.mkdirs();
            File file = new File(path, "image.jpg");


            String filename = file.toString();


            Log.d("file", filename + path);
            ConvertRGBtoGray(matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
            Imgproc.cvtColor(matResult, matResult, Imgproc.COLOR_BGR2RGBA);
            boolean ret  = Imgcodecs.imwrite( filename, matResult);
            ConvertNoise(matResult.getNativeObjAddr(),matResult.getNativeObjAddr(),5);
            if ( ret ) Log.d(TAG, "SUCESS0"+filename);
            else Log.d(TAG, "FAIL");


            Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(file));
            sendBroadcast(mediaScanIntent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        releaseWriteLock();
    }
    public void saveImage1() {
        try {
            getWriteLock();

            File path = new File(Environment.getExternalStorageDirectory() + "/Images/");
            path.mkdirs();

            File file_2 = new File(path, "image1.jpg");


            String filename1 = file_2.toString();


            ConvertRGBtoGray(matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
            Imgproc.cvtColor(matResult, matResult, Imgproc.COLOR_BGR2RGBA);
            ConvertNoise(matResult.getNativeObjAddr(),matResult.getNativeObjAddr(),5);
            boolean ret1  = Imgcodecs.imwrite( filename1, matResult);
            if ( ret1 ) Log.d(TAG, "SUCESS1"+filename1);
            else Log.d(TAG, "FAIL");



            Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(file_2));
            sendBroadcast(mediaScanIntent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        releaseWriteLock();
    }
    public void saveImage2() {
        try {
            getWriteLock();

            File path = new File(Environment.getExternalStorageDirectory() + "/Images/");
            path.mkdirs();

            File file_3 = new File(path, "image2.jpg");


            String filename2 = file_3.toString();


            ConvertRGBtoGray(matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
            Imgproc.cvtColor(matResult, matResult, Imgproc.COLOR_BGR2RGBA);
            ConvertNoise(matResult.getNativeObjAddr(),matResult.getNativeObjAddr(),10);
            boolean ret2  = Imgcodecs.imwrite( filename2, matResult);
            if ( ret2 ) Log.d(TAG, "SUCESS2"+filename2);
            else Log.d(TAG, "FAIL");


            Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(file_3));
            sendBroadcast(mediaScanIntent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        releaseWriteLock();
    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(getApplicationContext(), "권한이 허용됨", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한이 거부됨", Toast.LENGTH_SHORT).show();
        }
    };
}