package com.example.hyh.snowfall;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by HYH on 2017/1/6.
 */

public class RotateSnowFlake {

    private int size = 0;
    private int alpha = 255;
    private double mRandomAngle = 0.0;
    private double speed = 0.0;
    private double speedX = 0.0;
    private double speedY = 0.0;
    private double positionX = 0.0;
    private double positionY = 0.0;

    // 用于计算渐变
    private double pathLenght = 0.0;

    private int mOffsetAngle = 0;

    private Bitmap mScaledBitmap = null;
    private Bitmap mRawBitmap;

    private Paint paint;
    private SnowFlakeParams mParams;

    private boolean mHasShowed = false;

    public RotateSnowFlake(SnowFlakeParams snowflakeParams, Bitmap rawBitmap) {
        mParams = snowflakeParams;
        mRawBitmap = rawBitmap;
        initPaint();
        reset();
    }

    private void initPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(255, 255, 255));
        paint.setStyle(Paint.Style.FILL);
    }

    public void reset() {
        reset(0, 0);
    }

    public void reset(double newX, double newY) {
        // 大小
        size = Randomizer.randomInt(mParams.getSizeMinInPx(), mParams.getSizeMaxInPx(), true);
        // 雪花图像
        if (mRawBitmap != null) {
            mScaledBitmap = Bitmap.createScaledBitmap(mRawBitmap, size, size, false);
        }
        // 速度
        speed = ((size - mParams.getSizeMinInPx()) * 1.0 / (mParams.getSizeMaxInPx() - mParams.getSizeMinInPx()) *
                (mParams.getSpeedMax() - mParams.getSpeedMin()) + mParams.getSpeedMin());
        // 角度
        mRandomAngle = Randomizer.randomDouble(mParams.getAngleMax()) * Randomizer.randomSignum();
        double multiAngle = Math.toRadians(mRandomAngle + mOffsetAngle);
        speedX = speed * Math.sin(multiAngle);
        speedY = speed * Math.cos(multiAngle);
        // 透明度
        alpha = Randomizer.randomInt(mParams.getAlphaMin(), mParams.getAlphaMax(), false);
        paint.setAlpha(alpha);

        // 出现位置
        positionX = Randomizer.randomDouble(mParams.getParentWidth());
        if (newY == 0 && newX == 0) {
            mHasShowed = false;
            positionY = Randomizer.randomDouble(mParams.getParentHeight());
            // 若不是已经开始下落，确保雪花在屏幕之外
            if (!mParams.isAlreadyFalling()) {
                positionY -= (mParams.getParentHeight() + size);
            }
        } else {
            positionX = newX;
            positionY = newY;
        }
        pathLenght = 0;
    }

    public void updateOffset(int offsetAngle) {
        mOffsetAngle = offsetAngle;
        double multiAngle = Math.toRadians(mRandomAngle + mOffsetAngle);
        speedX = speed * Math.sin(multiAngle);
        speedY = speed * Math.cos(multiAngle);
    }

    public void update() {
        pathLenght += speed;
        positionX += speedX;
        positionY += speedY;
        if(!mHasShowed && positionX > 0 && positionY > 0) {
            mHasShowed = true;
        }
        // 飞出屏幕
        if (mHasShowed) {
            if (positionX > mParams.getParentWidth()) {
                reset(0 - size, positionY);
            }
            if (positionX < 0 - size) {
                reset(mParams.getParentWidth(), positionY);
            }
            if (positionY > mParams.getParentHeight()) {
                reset(positionX, 0 - size);
            }
            if (positionY < 0 - size) {
                reset(positionX, mParams.getParentHeight());
            }
        } else {
            if(positionY < -mParams.getParentHeight()) {
                positionY = mParams.getParentHeight();
            }
        }
        if (mParams.isFadingEnabled()) {
            double ratio = mParams.getParentHeight() - pathLenght < 0 ? 0 : mParams.getParentHeight() - pathLenght;
            paint.setAlpha((int) Math.ceil(alpha * (ratio / mParams.getParentHeight())));
        }
    }

    public void draw(Canvas canvas) {
        if (mScaledBitmap != null) {
            canvas.drawBitmap(mScaledBitmap, (float) (positionX), (float) positionY, paint);
        } else {
            // 没有图片就画圈圈
            canvas.drawCircle((float) positionX, (float) positionY, size, paint);
        }
    }
}
