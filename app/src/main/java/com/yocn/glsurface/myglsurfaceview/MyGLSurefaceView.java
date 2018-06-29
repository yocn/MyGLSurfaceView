package com.yocn.glsurface.myglsurfaceview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @Author yocn
 * @Date 2018/6/28 下午1:34
 * @ClassName MyGLSurefaceView
 */
public class MyGLSurefaceView extends GLSurfaceView {
    private Context mContext;
    SurfaceTexture mSurface;
    //    CameraInterface mCameraInterface;
    private DirectDrawer mDirectDrawer;

    public MyGLSurefaceView(Context context) {
        super(context);
    }

    public MyGLSurefaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        //设置OpenGL版本
        setEGLContextClientVersion(2);
        //设置renderer
        setRenderer(mRenderer);
        //设置刷新模式
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    MyRenderer mRenderer = new MyRenderer() {
        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            int mTextureID = GlUtil.createTextureID();
            mSurface = new SurfaceTexture(mTextureID);
            mSurface.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    LogUtil.d("yocn surfaceTexture->" + surfaceTexture);
                    MyGLSurefaceView.this.requestRender();
                }
            });
            mDirectDrawer = new DirectDrawer(mTextureID);
            CameraCapture.get().openBackCamera();
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            // 设置OpenGL场景的大小,(0,0)表示窗口内部视口的左下角，(w,h)指定了视口的大小
            GLES20.glViewport(0, 0, width, height);
            if (!CameraCapture.get().isPreviewing()) {
                CameraCapture.get().doStartPreview(mSurface);
            }
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            LogUtil.d("yocn onDrawFrame");
            // 设置白色为清屏
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            // 清除屏幕和深度缓存
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            // 更新纹理
            mSurface.updateTexImage();
            mDirectDrawer.draw();
        }

    };

}