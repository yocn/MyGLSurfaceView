package com.yocn.glsurface.myglsurfaceview;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity implements View.OnClickListener {
    MyGLSurefaceView mGLSurfaceView;
    Button mSwitchBtn;
    Button mRotateBtn;
    ImageView mFocusIV;
    float touchX;
    float touchY;

    private int width = 0;
    private int height = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGLSurfaceView = findViewById(R.id.glsv);
        mSwitchBtn = (Button) findViewById(R.id.bt_change);
        mRotateBtn = (Button) findViewById(R.id.bt_rotate);
        mFocusIV = findViewById(R.id.iv_focus);
        mSwitchBtn.setOnClickListener(this);
        mRotateBtn.setOnClickListener(this);
        mGLSurfaceView.post(new Runnable() {
            @Override
            public void run() {
                width = mGLSurfaceView.getMeasuredWidth();
                height = mGLSurfaceView.getMeasuredHeight();
            }
        });
        CameraCapture.get().setCallback(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {
                mFocusIV.setVisibility(View.GONE);
            }
        });
        mGLSurfaceView.setTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchX = motionEvent.getX();
                        touchY = motionEvent.getY();
                        setViewSize(mFocusIV, (int) touchX, (int) touchY, 100, 100);
                        break;
                    case MotionEvent.ACTION_UP:
                        CameraCapture.get().focusOnTouch((int) touchX, (int) touchY, width, height);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 自定义设置位置及其大小
     */
    private void setViewSize(ImageView view, int top, int left, int width, int height) {
        LogUtil.d("yocn view->" + view.toString() + " top->" + top + " left->" + left + " wodth->" + width + " height->" + height);
        view.setVisibility(View.VISIBLE);
        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(view.getLayoutParams());
        int dpTop = top;
        int dpLeft = left;
        int dpRight = left + width;
        int dpbottom = top + height;

        margin.setMargins(dpLeft, dpTop, dpRight, dpbottom);

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        layoutParams.width = width;
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.bringToFront();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_change:
                CameraCapture.get().switchCamera(1);
                break;
            case R.id.bt_rotate:
                mGLSurfaceView.setRotate();
                break;
            default:
                break;
        }
    }
}
