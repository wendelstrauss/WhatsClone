package com.wendelstrauss.whatsclone.config;

import android.app.Activity;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.wendelstrauss.whatsclone.activity.CameraActivity;

public class CameraControlador implements SurfaceHolder.Callback {

    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private boolean previewing = false;
    private Activity activity;

    public CameraControlador(Activity act, SurfaceView surfaceView){
        activity = act;
        this.surfaceView = surfaceView;
        surfaceHolder = this.surfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        camera = Camera.open();
        setCameraDisplayOrientation(activity, 0, camera);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        if (previewing) {
            pararVisualizacao();
        }

        if (camera != null) {
            try {
                camera.setPreviewDisplay(surfaceHolder);
                iniciarVisualizacao();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        pararVisualizacao();
        camera.release();
        camera = null;
    }

    public void tirarFoto(Camera.ShutterCallback shutter, Camera.PictureCallback raw, Camera.PictureCallback jpeg) {
        camera.takePicture(shutter, raw, jpeg);
    }

    public void iniciarVisualizacao() {
        previewing = true;
        camera.startPreview();
    }

    public void pararVisualizacao() {
        camera.stopPreview();
        previewing = false;
    }

    public Camera getCameraControler() {
        return camera;
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera)
    {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation)
        {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        }
        else
        { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

}
