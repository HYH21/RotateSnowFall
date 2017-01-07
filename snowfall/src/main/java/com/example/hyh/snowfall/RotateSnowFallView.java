package com.example.hyh.snowfall;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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

public class RotateSnowFallView extends View {

    private static final String TAG = "SnowFallView";

    public static final int DEFAULT_SNOWFLAKES_NUM = 200;

    private int snowflakesNum;
    private Bitmap mSnowFlakeBitmap;
    SnowFlakeParams mParamsBean;

    private UpdateSnowflakesThread updateSnowflakeThread = new UpdateSnowflakesThread();
    private List<RotateSnowFlake> mSnowFlakeList;

    public RotateSnowFallView(Context context) {
        this(context, null);
    }

    public RotateSnowFallView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateSnowFallView(Context context, AttributeSet attrs, int defStyleAttr) {
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
            mSnowFlakeBitmap = Drawables.toBitmap(drawable);
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
            for (RotateSnowFlake snowflake : mSnowFlakeList) {
                snowflake.reset();
            }
        }
    }

    private List<RotateSnowFlake> createSnowflakes() {
        mParamsBean.setParentHeight(getHeight());
        mParamsBean.setParentWidth(getWidth());
        List<RotateSnowFlake> result = new ArrayList<>();
        for (int i = 0; i < snowflakesNum; i++) {
            result.add(new RotateSnowFlake(mParamsBean, mSnowFlakeBitmap));
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode()) {
            return;
        }
        for (RotateSnowFlake snowflake : mSnowFlakeList) {
            snowflake.draw(canvas);
            updateSnowflake(snowflake);
        }
    }

    public void setAngle(int angle) {
        for (RotateSnowFlake snowflake : mSnowFlakeList) {
            snowflake.updateOffset(angle);
        }
    }

    private void updateSnowflake(final RotateSnowFlake snow) {
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
