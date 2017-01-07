package com.example.hyh.rotatesnowfall;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.hyh.snowfall.RotateSnowFallView;

import java.util.LinkedList;

/**
 * Created by gjz on 21/12/2016.
 */
public class GyroscopeObserver implements SensorEventListener {

    private static final String TAG = "GyroscopeObserver";

    private SensorManager mSensorManager;

    // For translate nanosecond to second.
    private static final float NS2S = 1.0f / 1000000000.0f;

    // The time in nanosecond when last sensor event happened.
    private long mLastTimestamp;

    // The radian the device already rotate along y-axis.
    private double mRotateRadianY;

    // The radian the device already rotate along x-axis.
    private double mRotateRadianX;
    // The maximum radian that the device should rotate along x-axis and y-axis to show image's bounds
    // The value must between (0, π/2].
    private double mMaxRotateRadian = Math.PI / 9;

    // The PanoramaImageViews to be notified when the device rotate.
    private LinkedList<RotateSnowFallView> mViews = new LinkedList<>();

    public void register(Context context) {
        if (mSensorManager == null) {
            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);

        mLastTimestamp = 0;
        mRotateRadianY = mRotateRadianX = 0;
    }

    public void unregister() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
            mSensorManager = null;
        }
    }

    void addRotateSnowFallView(RotateSnowFallView view) {
        if (view != null && !mViews.contains(view)) {
            mViews.addFirst(view);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mLastTimestamp == 0) {
            mLastTimestamp = event.timestamp;
            return;
        }

        if (Sensor.TYPE_ACCELEROMETER != event.sensor.getType()) {
            // 防止抖动
            return;
        }

        float[] values = event.values;
        float ax = values[0];
        float ay = values[1];
        float az = values[2];

        if (Math.abs(az) > 9) {
            // 防止平放抖动
            return;
        }

        double g = Math.sqrt(ax * ax + ay * ay);
        double cos = ay / g;
        if (cos > 1) {
            cos = 1;
        } else if (cos < -1) {
            cos = -1;
        }
        double rad = Math.acos(cos);
        if (ax < 0) {
            rad = 2 * Math.PI - rad;
        }

        for (RotateSnowFallView view : mViews) {
            if (view != null) {
                view.setAngle(-(int) Math.toDegrees(rad));
            }
        }
        mLastTimestamp = event.timestamp;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void setMaxRotateRadian(double maxRotateRadian) {
        if (maxRotateRadian <= 0 || maxRotateRadian > Math.PI / 2) {
            throw new IllegalArgumentException("The maxRotateRadian must be between (0, π/2].");
        }
        this.mMaxRotateRadian = maxRotateRadian;
    }
}
