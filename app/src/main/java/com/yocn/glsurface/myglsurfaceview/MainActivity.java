package com.yocn.glsurface.myglsurfaceview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    MyGLSurefaceView mGLSurfaceView;
    Button mSwitchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGLSurfaceView = findViewById(R.id.glsv);
        mSwitchBtn = (Button) findViewById(R.id.switch_camera);
        mSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraCapture.get().switchCamera(1);
            }
        });
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
}
