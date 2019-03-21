package com.nielsmasdorp.speculum.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.nielsmasdorp.speculum.models.ForecastDayWeather;
import com.nielsmasdorp.speculum.models.Weather;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class PrecipitationView extends View {
    private static final String LOG_TAG = "PrecipitationView";
    private Weather weather;
    private int width;
    private int height;
    private Paint precipitationBar;
    private Paint precipitationLine;

    public PrecipitationView(Context context) {
        super(context);
        init();
    }

    public PrecipitationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PrecipitationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        precipitationBar = new Paint(Paint.ANTI_ALIAS_FLAG);
        precipitationBar.setColor(Color.rgb(64,64,255));
        precipitationBar.setStyle(Paint.Style.FILL);

        precipitationLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        precipitationLine.setColor(Color.CYAN);
        precipitationLine.setStyle(Paint.Style.STROKE);
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
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (weather == null)
            return;

        if (weather.getHourForecast().size() < 2)
            return;

        float length = (float) width / weather.getHourForecast().size();

        float displayTop = 0;
        float displayBottom = (float) height;

        // precipitation bar
        float pos = length / 2;
        for (int i = 0; i < weather.getHourForecast().size(); i++) {
            ForecastDayWeather first = weather.getHourForecast().get(i);

            float firstSize = (float) Math.sqrt(first.getPrecipIntensity()) / 5;
            if (firstSize > 1)
                firstSize = 1;

            float firstAlpha = (float)(0.5 + Math.sqrt(first.getPrecipIntensity())/10);
            if (firstAlpha>1) firstAlpha = 1;

            precipitationBar.setAlpha(Math.round(firstAlpha*255));

            firstSize = displayBottom - (displayBottom - displayTop) * firstSize;

            canvas.drawRect(pos - length / 2, firstSize, pos + length / 2, displayBottom, precipitationBar);

            pos += length;
        }

        // precipitation line
        pos = length/2;
        for (int i = 0; i < weather.getHourForecast().size()-1; i++) {
            ForecastDayWeather first = weather.getHourForecast().get(i);
            ForecastDayWeather second = weather.getHourForecast().get(i+1);

            float firstPos = displayBottom - (displayBottom - displayTop) * first.getPrecipProbability();
            float secondPos = displayBottom - (displayBottom - displayTop) * second.getPrecipProbability();

            canvas.drawLine(pos, firstPos, pos+length, secondPos, precipitationLine);

            pos += length;
        }
    }
}
