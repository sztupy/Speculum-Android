package com.nielsmasdorp.speculum.views;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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

public class WeatherDetailsView extends View {
    private static final String LOG_TAG = "WeatherDetailsView";
    private Weather weather;
    private int width;
    private int height;
    private Paint temperatureLine;
    private Paint precipitationBar;
    private Paint dateLine;
    private Paint cloudLine;
    private SimpleDateFormat format;
    private ArgbEvaluator argbEvaluator;

    public WeatherDetailsView(Context context) {
        super(context);
        init();
    }

    public WeatherDetailsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeatherDetailsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        temperatureLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        temperatureLine.setColor(Color.RED);
        temperatureLine.setStrokeWidth(3);
        temperatureLine.setStyle(Paint.Style.STROKE);

        precipitationBar = new Paint(Paint.ANTI_ALIAS_FLAG);
        precipitationBar.setColor(Color.rgb(64,64,255));
        precipitationBar.setStyle(Paint.Style.FILL);

        cloudLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        cloudLine.setColor(Color.YELLOW);
        cloudLine.setStrokeWidth(2);
        cloudLine.setStyle(Paint.Style.STROKE);

        dateLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        dateLine.setColor(Color.WHITE);
        dateLine.setStyle(Paint.Style.FILL);
        dateLine.setTextAlign(Paint.Align.LEFT);
        dateLine.setTextSize(20);

        format = new java.text.SimpleDateFormat("dd MMM E", Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());

        argbEvaluator = new ArgbEvaluator();
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

        if (weather.getForecast().size() < 2)
            return;

        float length = (float)width / weather.getForecast().size();

        float minTemperature = weather.getForecast().get(0).getTemperature();
        float maxTemperature = weather.getForecast().get(0).getTemperature();

        for (ForecastDayWeather forecast : weather.getForecast()) {
            if (minTemperature > forecast.getTemperature()){
                minTemperature = forecast.getTemperature();
            }
            if (maxTemperature < forecast.getTemperature()) {
                maxTemperature = forecast.getTemperature();
            }
        }

        float displayTop = (float)60;
        float displayBottom = (float)height - 35;
        float heightWeight = (displayBottom - displayTop) / (maxTemperature - minTemperature);

        // precipitation bar
        float pos = length/2;
        for (int i = 0; i < weather.getForecast().size(); i++) {
            ForecastDayWeather first = weather.getForecast().get(i);

            float firstAlpha = (float)(0.5 + Math.sqrt(first.getPrecipIntensity())/10);
            if (firstAlpha>1) firstAlpha = 1;

            float firstSize = displayBottom - (displayBottom - displayTop) * first.getPrecipProbability();

            precipitationBar.setAlpha(Math.round(firstAlpha*255));

            canvas.drawRect(pos - length/2, firstSize, pos+length/2, displayBottom, precipitationBar);

            pos += length;
        }

        // cloud cover line
        pos = length/2;
        for (int i = 0; i < weather.getForecast().size()-1; i++) {
            ForecastDayWeather first = weather.getForecast().get(i);
            ForecastDayWeather second = weather.getForecast().get(i+1);

            float firstPos = displayBottom - (displayBottom - displayTop) * (1-first.getCloudCover());
            float secondPos = displayBottom - (displayBottom - displayTop) * (1-second.getCloudCover());

            float red = 0.5f + 0.5f * first.getDayOrNight();
            float green = 0.5f + 0.5f * first.getDayOrNight();
            float blue = 0.5f - 0.5f * first.getDayOrNight();

            cloudLine.setARGB(255,Math.round(255*red), Math.round(255*green), Math.round(255*blue));

            canvas.drawLine(pos, firstPos, pos+length, secondPos, cloudLine);

            pos += length;
        }

        // temperature line
        pos = length/2;
        for (int i = 0; i < weather.getForecast().size()-1; i++) {
            ForecastDayWeather first = weather.getForecast().get(i);
            ForecastDayWeather second = weather.getForecast().get(i+1);

            float firstPos = displayBottom - heightWeight * (first.getTemperature() - minTemperature);
            float secondPos = displayBottom - heightWeight * (second.getTemperature() - minTemperature);

            canvas.drawLine(pos, firstPos, pos+length, secondPos, temperatureLine);

            pos += length;
        }

        // days
        pos = length/2;
        dateLine.setTextAlign(Paint.Align.LEFT);
        for (int i = 0; i < weather.getForecast().size()-1; i++) {
            ForecastDayWeather first = weather.getForecast().get(i);
            ForecastDayWeather second = weather.getForecast().get(i+1);

            String firstDate = format.format(first.getDate());
            String secondDate = format.format(second.getDate());

            if (!firstDate.equals(secondDate)) {
                canvas.drawLine(pos + length/2, displayTop, pos + length/2, height, dateLine);

                canvas.drawText(secondDate, pos + length / 2 + 5, height - 5, dateLine);
            }

            pos += length;
        }

        // icons
        pos = length/2;
        float remaining = 20;
        dateLine.setTextAlign(Paint.Align.CENTER);
        for (ForecastDayWeather forecast : weather.getForecast()) {
            if (remaining<0) {
                Drawable d = ContextCompat.getDrawable(getContext(), forecast.getIconId());
                d.setBounds(Math.round(pos - 16), 0, Math.round(pos + 16), 32);
                d.draw(canvas);

                String temperature = String.format(Locale.getDefault(), "%.0f",forecast.getTemperature());

                canvas.drawText(temperature, pos, 52, dateLine);
                remaining = 40;
            }

            pos += length;
            remaining -= length;
        }
    }
}
