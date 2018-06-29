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
    CameraInterface mCameraInterface;

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
            super.onSurfaceCreated(gl10, eglConfig);
            int textureId = createTextureID();
            mSurface = new SurfaceTexture(textureId);
            mSurface.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {

                }
            });
            MyRenderer myRenderer = new MyRenderer();
            mCameraInterface = new CameraInterface();
            mCameraInterface.openBackCamera();
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            super.onSurfaceChanged(gl10, width, height);
            // 设置OpenGL场景的大小,(0,0)表示窗口内部视口的左下角，(w,h)指定了视口的大小
            GLES20.glViewport(0, 0, width, height);
//            if (!mCameraInterface.isPreviewing()) {
                mCameraInterface.doStartPreview(mSurface);
//            }
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            super.onDrawFrame(gl10);
        }
    };

    private int createTextureID() {
        int[] texture = new int[1];

        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return texture[0];
    }
}