package com.example.hyh.snowfall;

/**
 * Created by HYH on 2017/1/6.
 */

public class SnowFlakeParams {
    // 散落范围
    private int parentWidth = 768;
    private int parentHeight = 1280;
    // 透明度
    private int alphaMin = 150;
    private int alphaMax = 250;
    // 角度
    private int angleMax = 10;
    // 雪花大小
    private int sizeMinInPx = 2;
    private int sizeMaxInPx = 8;
    // 速度
    private int speedMin = 2;
    private int speedMax = 8;
    // 渐变
    private boolean fadingEnabled = false;
    // 是否正在下了(在顶部出现还是中间突然出现)
    private boolean alreadyFalling = false;

    public SnowFlakeParams() {
    }

    public int getParentWidth() {
        return parentWidth;
    }

    public void setParentWidth(int parentWidth) {
        this.parentWidth = parentWidth;
    }

    public int getParentHeight() {
        return parentHeight;
    }

    public void setParentHeight(int parentHeight) {
        this.parentHeight = parentHeight;
    }

    public int getAlphaMin() {
        return alphaMin;
    }

    public void setAlphaMin(int alphaMin) {
        this.alphaMin = alphaMin;
    }

    public int getAlphaMax() {
        return alphaMax;
    }

    public void setAlphaMax(int alphaMax) {
        this.alphaMax = alphaMax;
    }

    public int getAngleMax() {
        return angleMax;
    }

    public void setAngleMax(int angleMax) {
        this.angleMax = angleMax;
    }

    public int getSizeMinInPx() {
        return sizeMinInPx;
    }

    public void setSizeMinInPx(int sizeMinInPx) {
        this.sizeMinInPx = sizeMinInPx;
    }

    public int getSizeMaxInPx() {
        return sizeMaxInPx;
    }

    public void setSizeMaxInPx(int sizeMaxInPx) {
        this.sizeMaxInPx = sizeMaxInPx;
    }

    public int getSpeedMin() {
        return speedMin;
    }

    public void setSpeedMin(int speedMin) {
        this.speedMin = speedMin;
    }

    public int getSpeedMax() {
        return speedMax;
    }

    public void setSpeedMax(int speedMax) {
        this.speedMax = speedMax;
    }

    public boolean isFadingEnabled() {
        return fadingEnabled;
    }

    public void setFadingEnabled(boolean fadingEnabled) {
        this.fadingEnabled = fadingEnabled;
    }

    public boolean isAlreadyFalling() {
        return alreadyFalling;
    }

    public void setAlreadyFalling(boolean alreadyFalling) {
        this.alreadyFalling = alreadyFalling;
    }
}
