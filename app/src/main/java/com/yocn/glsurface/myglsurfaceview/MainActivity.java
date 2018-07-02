package com.yocn.glsurface.myglsurfaceview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {
    MyGLSurefaceView mGLSurfaceView;
    Button mSwitchBtn;
    Button mRotateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGLSurfaceView = findViewById(R.id.glsv);
        mSwitchBtn = (Button) findViewById(R.id.bt_change);
        mRotateBtn = (Button) findViewById(R.id.bt_rotate);
        mSwitchBtn.setOnClickListener(this);
        mRotateBtn.setOnClickListener(this);
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
