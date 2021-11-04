package com.example.gaugerotation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class GaugeRotation extends View {

    private static final String TAG = GaugeRotation.class.getSimpleName();

    private static final int DEGREE_CENTER = 0;
    private static final int DEGREE_MIN = -360;
    private static final int DEGREE_MAX = 360;

    private int mGaugeBackground;
    private int mPointBackground;
    private float mRotate;
    private int mGravity;

    float offserX;

    //罗盘背景是否跟着旋转
    private boolean isBackgroundRotation = false;

    private Paint mGaugePaint = new Paint();
    private Paint mPointPaint = new Paint();


    public GaugeRotation(Context context) {
        super(context);
    }

    public GaugeRotation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public GaugeRotation(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.gaugeRotation);
        mGaugeBackground = mTypedArray.getResourceId(R.styleable.gaugeRotation_gauge_gackground, 0);
        mPointBackground = mTypedArray.getResourceId(R.styleable.gaugeRotation_point_gackground, 0);
        isBackgroundRotation = mTypedArray.getBoolean(R.styleable.gaugeRotation_is_rotation, false);
        mGravity = mTypedArray.getInt(R.styleable.gaugeRotation_gravity, 0);
        mTypedArray.recycle();
        initPaint();
    }

    private void initPaint() {
        mGaugePaint.setStyle(Paint.Style.FILL);
        mGaugePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mGaugePaint.setAntiAlias(true);

        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setAntiAlias(true);
    }

    /*
     * 1.根据罗盘背景设置宽高 当width 为 wrap_content的时候
     * 2.根据指针背景设置offsetX
     * */

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int w = widthSize;
        int h = heightSize;

        if (widthMode == MeasureSpec.AT_MOST) {
            w = getResources().getDrawable(mGaugeBackground, null).getIntrinsicWidth();
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            h = getResources().getDrawable(mGaugeBackground, null).getIntrinsicHeight();
        }

        int dimension = Math.min(w, h);

        setMeasuredDimension(dimension, dimension);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        placementView();
        onRotateBackgroundDraw(canvas);
        onPointBackgroundDraw(canvas);
        super.onDraw(canvas);
    }


    private void onRotateBackgroundDraw(Canvas canvas) {
        if (isBackgroundRotation) {
            onRotateDraw(canvas, mGaugeBackground, mGaugePaint, offserX, 0);
        } else {
            canvas.drawBitmap(drawable2Bitmap(mGaugeBackground), offserX, 0, mGaugePaint);
        }
    }


    private void onPointBackgroundDraw(Canvas canvas) {
        float x = (getWidth() - getResources().getDrawable(mPointBackground, null).getIntrinsicWidth()) / 2;
        float y = (getHeight() - getResources().getDrawable(mPointBackground, null).getIntrinsicHeight()) / 2;
        onRotateDraw(canvas, mPointBackground, mPointPaint, x, y);
    }

    private void onRotateDraw(Canvas canvas, int resourId, Paint paint, float offsetX, float offsetY) {
        Bitmap mBitmap = drawable2Bitmap(resourId);
        Bitmap mTempBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas mTempCanvas = new Canvas(mTempBitmap);
        float rotateX = mBitmap.getWidth() / 2;
        float rotateY = mBitmap.getWidth() / 2;
        mTempCanvas.rotate(mRotate, rotateX, rotateY);
        mTempCanvas.save();
        mTempCanvas.drawBitmap(mBitmap, 0, 0, paint);
        canvas.drawBitmap(mTempBitmap, offsetX, offsetY, paint);
        mTempBitmap.recycle();
        mBitmap.recycle();
    }


    //计算罗盘的摆放位置
    private void placementView() {
        Drawable d = getResources().getDrawable(mGaugeBackground, null);
        int width = d.getIntrinsicWidth();
        int parentWidth = getWidth();
        switch (mGravity) {
            case 0:
                offserX = 0;
                break;
            case 1:
                offserX = (parentWidth - width) / 2;
                break;
            case 2:
                offserX = (parentWidth - width);
                break;
        }
    }

    private Bitmap drawable2Bitmap(int resourceId) {
        if (mGaugeBackground <= 0) {
            throw new NullPointerException("background resource is not set");
        }
        return BitmapFactory.decodeResource(getResources(), resourceId).copy(Bitmap.Config.ARGB_8888, true);
    }

    public void setRotate(float rotate) {
        this.mRotate = (float) (Math.toDegrees(rotate) + 360) % 360;
        if (mRotate < DEGREE_MIN) {
            this.mRotate = DEGREE_MIN;
        }
        if (mRotate > DEGREE_MAX) {
            this.mRotate = DEGREE_MAX;
        }
        Log.d(TAG, "setRotate: mRotate = " + mRotate);
        invalidate();
    }

}
