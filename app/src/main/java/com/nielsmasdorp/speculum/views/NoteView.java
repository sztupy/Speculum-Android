package com.nielsmasdorp.speculum.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    public void saveImage() {
        if (mBitmap!=null) {
            saveImage(mBitmap);
        }
    }

    private File getFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        File path = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(path, String.format("note-%s.png", dateFormat.format(date)));
    }

    public void saveImage(final Bitmap mBitmap) {
        if (mBitmap==null) {
            return;
        }

        File file = getFileName();

        try {
            OutputStream os = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 90, os);
            os.close();
            Log.i(LOG_TAG, "Note saved");
        } catch (Exception e) {
            Log.w(LOG_TAG, "Error writing " + file, e);
        }
    }

    public Bitmap loadImage() {
        File file = getFileName();

        try {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            Log.i(LOG_TAG, "Note loaded");
            return bitmap;
        } catch (Exception e) {
            Log.w(LOG_TAG, "Error reading " + file, e);
        }
        return null;
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
        mPaint.setStrokeWidth(6);
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
        if (w > 0 && h > 0) {
            width = w;
            height = h;

            saveImage(mBitmap);
            clearImage();

            Bitmap bitmap = loadImage();
            if (bitmap != null) {
                mCanvas.drawBitmap(bitmap, 0, 0, null);
            }
        }
    }

    private void clearImage() {
        if (width>0 && height>0) {
            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0x00000000);

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
    }

    private void touch_start(float x, float y, float pressure) {
        mPath.reset();
        mPath.moveTo(x, y);
        mPaint.setStrokeWidth(10 * pressure);
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
        mPaint.setStrokeWidth(10 * pressure);
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

        if (mainView != null) {
            mainView.ping();
        }

        if (x<50 && y<50) {
            clearImage();
            invalidate();
            return true;
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
