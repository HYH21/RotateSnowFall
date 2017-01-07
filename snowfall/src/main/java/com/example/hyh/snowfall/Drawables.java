package com.example.hyh.snowfall;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HYH on 2017/1/6.
 */

public class Drawables {

    @SuppressLint("NewApi")
    public static Bitmap toBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            return vectorDrawableToBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("Unsupported drawable type");
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static  Bitmap vectorDrawableToBitmap(VectorDrawable vtDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vtDrawable.getIntrinsicWidth(), vtDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vtDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vtDrawable.draw(canvas);
        return bitmap;
    }

    /**
     * Created by HYH on 2017/1/6.
     */

    public static class SnowFallView extends View {

        private static final String TAG = "SnowFallView";

        public static final int DEFAULT_SNOWFLAKES_NUM = 200;

        private int snowflakesNum;
        private Bitmap mSnowFlakeBitmap;
        SnowFlakeParams mParamsBean;

        private UpdateSnowflakesThread updateSnowflakeThread = new UpdateSnowflakesThread();
        private List<SnowFlake> mSnowFlakeList;

        public SnowFallView(Context context) {
            this(context, null);
        }

        public SnowFallView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public SnowFallView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SnowfallView);
            init(typedArray);
        }

        private void init(TypedArray typedArray) {
            mParamsBean = new SnowFlakeParams();
            try {
                snowflakesNum = typedArray.getInt(R.styleable.SnowfallView_snowflakesNum, DEFAULT_SNOWFLAKES_NUM);

                mParamsBean.setAlphaMin(typedArray.getInt(R.styleable.SnowfallView_snowflakeAlphaMin, mParamsBean.getAlphaMin()));
                mParamsBean.setAlphaMax(typedArray.getInt(R.styleable.SnowfallView_snowflakeAlphaMax, mParamsBean.getAlphaMax()));

                mParamsBean.setAngleMax(typedArray.getInt(R.styleable.SnowfallView_snowflakeAngleMax, mParamsBean.getAngleMax()));

                mParamsBean.setSizeMinInPx(typedArray.getDimensionPixelSize(R.styleable.SnowfallView_snowflakeSizeMin, dpToPx(mParamsBean.getSizeMinInPx())));
                mParamsBean.setSizeMaxInPx(typedArray.getDimensionPixelSize(R.styleable.SnowfallView_snowflakeSizeMax, dpToPx(mParamsBean.getSizeMaxInPx())));

                mParamsBean.setSpeedMin(typedArray.getInt(R.styleable.SnowfallView_snowflakeSpeedMin, mParamsBean.getSpeedMin()));
                mParamsBean.setSpeedMax(typedArray.getInt(R.styleable.SnowfallView_snowflakeSpeedMax, mParamsBean.getSpeedMax()));

                mParamsBean.setFadingEnabled(typedArray.getBoolean(R.styleable.SnowfallView_snowflakesFadingEnabled, mParamsBean.isFadingEnabled()));
                mParamsBean.setAlreadyFalling(typedArray.getBoolean(R.styleable.SnowfallView_snowflakesAlreadyFalling, mParamsBean.isAlreadyFalling()));

                Drawable drawable = typedArray.getDrawable(R.styleable.SnowfallView_snowflakeImage);
                mSnowFlakeBitmap = toBitmap(drawable);
            } finally {
                typedArray.recycle();
            }
            updateSnowflakeThread = new UpdateSnowflakesThread();
            updateSnowflakeThread.start();
        }

        private int dpToPx(int dp) {
            return (dp * DisplayMetrics.DENSITY_DEFAULT);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mSnowFlakeList = createSnowflakes();
        }

        @Override
        protected void onVisibilityChanged(View changedView, int visibility) {
            super.onVisibilityChanged(changedView, visibility);
            if (changedView.equals(this) && visibility == GONE) {
                mSnowFlakeList.clear();
            } else if(changedView.equals(this) && visibility == VISIBLE){
                mSnowFlakeList = createSnowflakes();
                for (SnowFlake snowflake : mSnowFlakeList) {
                    snowflake.reset();
                }
            }
        }

        private List<SnowFlake> createSnowflakes() {
            mParamsBean.setParentHeight(getHeight());
            mParamsBean.setParentWidth(getWidth());
            List<SnowFlake> result = new ArrayList<>();
            for (int i = 0; i < snowflakesNum; i++) {
                result.add(new SnowFlake(mParamsBean, mSnowFlakeBitmap));
            }
            return result;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (isInEditMode()) {
                return;
            }
            for (SnowFlake snowflake : mSnowFlakeList) {
                snowflake.draw(canvas);
                updateSnowflake(snowflake);
            }
        }

        public void setAngle(int angle) {
            for (SnowFlake snowflake : mSnowFlakeList) {
                snowflake.updateOffset(angle);
            }
        }

        private void updateSnowflake(final SnowFlake snow) {
            // 这了开线程主要是减少onDraw() 里进行update计算的负担
            updateSnowflakeThread.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    snow.update();
                    // 这个替代是什么
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        postInvalidateOnAnimation();
                    } else {
                        postInvalidate();
                    }
                }
            });
        }

        private class UpdateSnowflakesThread extends HandlerThread {
            private Handler handler;

            public UpdateSnowflakesThread() {
                super("SnowflakesComputations");
            }

            public Handler getHandler() {
                if (handler == null) {
                    synchronized (UpdateSnowflakesThread.this) {
                        if (handler == null) {
                            handler = new Handler(getLooper());
                        }
                    }
                }
                return handler;
            }
        }

    }

    /**
     * Created by HYH on 2017/1/6.
     * 一个雪花，决定大小、速度和移动轨迹等
     */
    public static class SnowFlake {

        private int size = 0;
        private int alpha = 255;
        private double angle = 0.0;
        private double speed = 0.0;
        private double speedX = 0.0;
        private double speedY = 0.0;
        private double positionX = 0.0;
        private double positionY = 0.0;

        private Bitmap mScaledBitmap = null;
        private Bitmap mRawBitmap;

        private Paint paint;
        private SnowFlakeParams mParams;

        public SnowFlake(SnowFlakeParams snowflakeParams, Bitmap rawBitmap) {
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
            reset(0);
        }

        public void reset(double newY) {
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
            angle = Math.toRadians(Randomizer.randomDouble(mParams.getAngleMax()) * Randomizer.randomSignum());
            speedX = speed * Math.sin(angle);
            speedY = speed * Math.cos(angle);
            // 透明度
            alpha = Randomizer.randomInt(mParams.getAlphaMin(), mParams.getAlphaMax(), false);
            paint.setAlpha(alpha);

            // 出现位置
            positionX = Randomizer.randomDouble(mParams.getParentWidth());
            if (newY == 0) {
                positionY = Randomizer.randomDouble(mParams.getParentHeight());
                // 若不是已经开始下落，确保雪花在屏幕之外
                if (!mParams.isAlreadyFalling()) {
                    positionY -= (mParams.getParentHeight() + size);
                }
            } else {
                positionY = newY;
            }
        }

        public void updateOffset(int offset) {
            speedX = speed * Math.sin(angle + offset);
            speedY = speed * Math.cos(angle + offset);
        }

        public void update() {
            positionX += speedX;
            positionY += speedY;
            if (positionY >= mParams.getParentHeight() || positionX < 0 - size || positionX > mParams.getParentWidth()) {
                reset(positionY - mParams.getParentHeight());
            }
            if (mParams.isFadingEnabled()) {
                paint.setAlpha((int) Math.ceil(alpha * (1.0 * (mParams.getParentHeight() - positionY) / mParams.getParentHeight())));
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
}
