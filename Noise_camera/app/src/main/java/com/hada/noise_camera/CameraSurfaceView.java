package com.hada.noise_camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    SurfaceHolder holder;
    ImageView imageView;
    Camera camera = null;
    private int cameraID = 0;
    public CameraSurfaceView(Context context) {
        super(context);

        init(context);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context){
        holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open(cameraID);
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraID, info);
        if (info.canDisableShutterSound) {
            camera.enableShutterSound(false);
        }
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        parameters.set("orientation", "portrait");
        camera.setDisplayOrientation(90);
        parameters.setRotation(90);
        camera.setParameters(parameters);

        OpenCVLoader.initDebug();
        Size matSize = new Size( parameters.getPictureSize().width,parameters.getPictureSize().height);
        Mat noise = new Mat(matSize, CvType.CV_8UC1);
        Matrix matrix = new Matrix();
        matrix.preRotate(90, 0, 0);
        Log.d("noise", parameters.getPictureSize().height+""+parameters.getPictureSize().width);
        Log.d("noise", noise.height()+""+noise.width());
        Core.randn(noise,0.0, 30.0);
        Core.rotate(noise,noise,Core.ROTATE_90_CLOCKWISE);
        Bitmap bmp = Bitmap.createBitmap(parameters.getPictureSize().height,parameters.getPictureSize().width,Bitmap.Config.ARGB_8888);
        imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(convertMatToBitMap(noise));


        try {

            camera.setPreviewDisplay(holder);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera.startPreview();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public boolean capture(Camera.PictureCallback callback){
        if(camera != null){
            camera.takePicture(null, null, callback);
            return true;
        } else {
            return false;
        }
    }
    private static Bitmap convertMatToBitMap(Mat input){
        Bitmap bmp = null;
        Mat rgb = new Mat();
        Imgproc.cvtColor(input, rgb, Imgproc.COLOR_BGR2RGB);

        try {
            bmp = Bitmap.createBitmap(rgb.cols(), rgb.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(rgb, bmp);
        }
        catch (CvException e){
            Log.d("Exception",e.getMessage());
        }
        return bmp;
    }

    public void setCameraID(int cameraID) {
        this.cameraID = cameraID;
    }

    public SurfaceHolder getHolders() {
        return holder;
    }
}
