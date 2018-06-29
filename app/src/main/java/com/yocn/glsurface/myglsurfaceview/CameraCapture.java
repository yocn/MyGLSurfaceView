package com.yocn.glsurface.myglsurfaceview;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

/**
 * @author liyachao 296777513
 * @version 1.0
 * @date 2017/3/1
 */
public class CameraCapture {

    private static final float DEFAULT_PREVIEW_RATE = 4f / 3f;

    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;

    private SurfaceTexture mSurface;

    private static CameraCapture sInstance = new CameraCapture();

    private CameraCapture() {

    }


    public static CameraCapture get() {
        return sInstance;
    }


    /**
     * open back camera
     */
    public void openBackCamera() {
        LogUtil.d("Back Camera open....");
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                LogUtil.d("Back Camera open over....");
                mCamera = Camera.open(i);
                return;
            }
        }
        LogUtil.d("Back Camera open 异常!!!");
        doStopCamera();
    }

    /**
     * open front camera
     */
    public void openFrontCamera() {
        LogUtil.d("Front Camera open....");
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                LogUtil.d("Front Camera open over....");
                mCamera = Camera.open(i);
                return;
            }
        }
        LogUtil.d("Front Camera open 异常!!!");
        doStopCamera();
    }

    private int mCameraPosition = 1;

    public void switchCamera(int cameraPosition) {
        doStopCamera();
        if (cameraPosition == mCameraPosition) {
            //现在是后置，变更为前置
            openFrontCamera();//打开当前选中的摄像头
            mCameraPosition = 0;
        } else {
            openBackCamera();//打开当前选中的摄像头
            mCameraPosition = 1;
        }
        doStartPreview(mSurface);
    }


    /**
     * 使用Surfaceview开启预览
     *
     * @param holder
     */
    public void doStartPreview(SurfaceHolder holder) {
        LogUtil.d("doStartPreview...");
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            initCamera();
        }
    }


    /**
     * 使用TextureView预览Camera
     *
     * @param surface
     */
    public void doStartPreview(SurfaceTexture surface) {
        LogUtil.d("doStartPreview...");
        if (isPreviewing) {
            LogUtil.d("stopPreview...");
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {
            mSurface = surface;
            try {
                LogUtil.d("setPreviewTexture...");
                mCamera.setPreviewTexture(surface);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            initCamera();
        }

    }

    /**
     * 停止预览，释放Camera
     */
    public void doStopCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mCamera.release();
            mCamera = null;
        }
    }


    public boolean isPreviewing() {
        return isPreviewing;
    }


    private void initCamera() {
        if (mCamera != null) {

            mParams = mCamera.getParameters();
            //设置PreviewSize和PictureSize
            Camera.Size previewSize = CameraUtil.chooseOptimalSize(
                    mParams.getSupportedPreviewSizes(), DEFAULT_PREVIEW_RATE, 800);
            mParams.setPreviewSize(previewSize.width, previewSize.height);

            mCamera.setDisplayOrientation(0);

            // 设置摄像头为自动聚焦
            List<String> focusModes = mParams.getSupportedFocusModes();
            if (focusModes.contains("continuous-video")) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.setParameters(mParams);
            mCamera.startPreview();//开启预览
            isPreviewing = true;

            mParams = mCamera.getParameters(); //重新get一次
            LogUtil.d("最终设置:PreviewSize--With = " + mParams.getPreviewSize().width
                    + "Height = " + mParams.getPreviewSize().height);
            LogUtil.d("最终设置:PictureSize--With = " + mParams.getPictureSize().width
                    + "Height = " + mParams.getPictureSize().height);
        }
    }


}
