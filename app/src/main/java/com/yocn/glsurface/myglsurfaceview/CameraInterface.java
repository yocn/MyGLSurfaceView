package com.yocn.glsurface.myglsurfaceview;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

/**
 * @Author yocn
 * @Date 2018/6/28 下午2:12
 * @ClassName CameraInterface
 */
public class CameraInterface {

    private static final float DEFAULT_PREVIEW_RATE = 4f / 3f;
    private Camera mCamera;
    private Camera.Parameters mParams;
    private SurfaceTexture mSurface;

    public CameraInterface() {
    }

    public void openBackCamera() {
        int cameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < cameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.d("", "Back Camera open over....");
                mCamera = Camera.open(i);
//                isOpenBackCamera = true;
                return;
            }
        }
    }

    /**
     * 使用Surfaceview开启预览
     *
     * @param holder
     */
    public void doStartPreview(SurfaceHolder holder) {
//        LOG.logI("doStartPreview...");
//        if (isPreviewing) {
//            mCamera.stopPreview();
//            return;
//        }
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
//        LOG.logI("doStartPreview...");
//        if (isPreviewing) {
////            LOG.logI("stopPreview...");
//            mCamera.stopPreview();
//            return;
//        }
        if (mCamera != null) {
            mSurface = surface;
            try {
//                LOG.logI("setPreviewTexture...");
                mCamera.setPreviewTexture(surface);
            } catch (IOException e) {
                e.printStackTrace();
            }
            initCamera();
        }

    }

    private void initCamera() {
        if (mCamera != null) {

            mParams = mCamera.getParameters();
            //设置PreviewSize和PictureSize
            Camera.Size previewSize = CameraUtil.chooseOptimalSize(
                    mParams.getSupportedPreviewSizes(), DEFAULT_PREVIEW_RATE, 800);
            mParams.setPreviewSize(previewSize.width, previewSize.height);

            mCamera.setDisplayOrientation(90);

            // 设置摄像头为自动聚焦
            List<String> focusModes = mParams.getSupportedFocusModes();
            if (focusModes.contains("continuous-video")) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.setParameters(mParams);
            mCamera.startPreview();//开启预览
//            isPreviewing = true;

            mParams = mCamera.getParameters(); //重新get一次
            Log.d("", "最终设置:PreviewSize--With = " + mParams.getPreviewSize().width
                    + "Height = " + mParams.getPreviewSize().height);
            Log.d("", "最终设置:PictureSize--With = " + mParams.getPictureSize().width
                    + "Height = " + mParams.getPictureSize().height);
        }
    }

}
