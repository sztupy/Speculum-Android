package com.nielsmasdorp.speculum.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class NoteView extends View {
    private static final String LOG_TAG = "NoteView";

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private int width;
    private int height;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private Path mPath;
    private Paint mBitmapPaint;
    private MainView mainView;

    public NoteView(Context context) {
        super(context);
        init();
    }

    public NoteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setMainView(MainView view) {
        mainView = view;
    }

    private void init() {
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSize(minw, widthMeasureSpec);

        int minh = getPaddingTop() + getPaddingBottom() + getSuggestedMinimumHeight();
        int h = resolveSize(minh, heightMeasureSpec);

        setMeasuredDimension(w, h);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        clearImage();
    }

    private void clearImage() {
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0xFF000000);

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
    }

    private void touch_start(float x, float y, float pressure) {
        mPath.reset();
        mPath.moveTo(x, y);
        mPaint.setStrokeWidth(20 * pressure);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y, float pressure) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
        mPath.lineTo(mX, mY);
        mCanvas.drawPath(mPath, mPaint);

        mPath.reset();
        mPath.moveTo(x, y);
        mPaint.setStrokeWidth(20 * pressure);
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        mCanvas.drawPath(mPath, mPaint);
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (x<10 && y<10) {
            clearImage();
        }

        if (mainView != null) {
            mainView.ping();
        }

        Log.i(LOG_TAG, event.getPressure()+"");

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y, event.getPressure());
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y, event.getPressure());
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }
}
