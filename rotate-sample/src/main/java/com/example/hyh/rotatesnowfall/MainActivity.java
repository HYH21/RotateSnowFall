package com.example.hyh.rotatesnowfall;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.hyh.snowfall.RotateSnowFallView;


public class MainActivity extends Activity {

    private RotateSnowFallView mSnowFallView;

    private GyroscopeObserver gyroscopeObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gyroscopeObserver = new GyroscopeObserver();

        mSnowFallView = (RotateSnowFallView) findViewById(R.id.snowfall);
        gyroscopeObserver.addRotateSnowFallView(mSnowFallView);


        mSnowFallView.setOnClickListener(new View.OnClickListener() {
            private int i=0;
            @Override
            public void onClick(View v) {
                mSnowFallView.setAngle(i);
                i-=20;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        gyroscopeObserver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gyroscopeObserver.unregister();
    }
}
