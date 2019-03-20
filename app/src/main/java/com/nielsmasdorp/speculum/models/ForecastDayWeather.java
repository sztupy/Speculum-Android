package com.nielsmasdorp.speculum.models;

import java.util.Date;

/**
 * @author Niels Masdorp (NielsMasdorp)
 */
public class ForecastDayWeather {

    private int iconId;
    private float temperature;
    private float precipProbability;
    private float precipIntensity;
    private float cloudCover;
    private Date date;

    public ForecastDayWeather(int iconId, float temperature, float precipProbability, float precipIntensity, float cloudCover, Date date) {
        this.iconId = iconId;
        this.temperature = temperature;
        this.precipProbability = precipProbability;
        this.precipIntensity = precipIntensity;
        this.cloudCover = cloudCover;
        this.date = date;
    }

    public int getIconId() {
        return iconId;
    }

    public float getTemperature() {
        return temperature;
    }

    public Date getDate() {
        return date;
    }

    public float getPrecipProbability() {
        return precipProbability;
    }

    public float getPrecipIntensity() {
        return precipIntensity;
    }

    public float getCloudCover() {
        return cloudCover;
    }

    @Override
    public String toString() {
        return "ForecastDayWeather{" +
                "iconId=" + iconId +
                ", temperature=" + temperature +
                ", precipProbability=" + precipProbability +
                ", precipIntensity=" + precipIntensity +
                ", cloudCover=" + cloudCover +
                ", date=" + date +
                '}';
    }
}
