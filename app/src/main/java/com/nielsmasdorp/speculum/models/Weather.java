package com.nielsmasdorp.speculum.models;

import com.nielsmasdorp.speculum.models.forecast.DayForecast;

import java.util.Date;
import java.util.List;

/**
 * @author Niels Masdorp (NielsMasdorp)
 */
public class Weather {

    private int iconId;
    private String temperature;
    private String lastUpdated;
    private String windInfo;
    private String humidityInfo;
    private String visibilityInfo;
    private List<ForecastDayWeather> forecast;
    private List<ForecastDayWeather> hourForecast;

    public static class Builder {

        private int iconId;
        private String temperature;
        private String lastUpdated;
        private String windInfo;
        private String humidityInfo;
        private String visibilityInfo;
        private List<ForecastDayWeather> forecast;
        private List<ForecastDayWeather> hourForecast;

        public Builder iconId(int iconId) { this.iconId = iconId; return this;}
        public Builder temperature(String temperature) { this.temperature = temperature; return this; }
        public Builder lastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; return this; }
        public Builder windInfo(String windInfo) { this.windInfo = windInfo; return this; }
        public Builder humidityInfo(String humidityInfo) { this.humidityInfo = humidityInfo; return this; }
        public Builder visibilityInfo(String visibilityInfo) { this.visibilityInfo = visibilityInfo; return this; }
        public Builder forecast(List<ForecastDayWeather> forecast) { this.forecast = forecast; return this; }
        public Builder hourForecast(List<ForecastDayWeather> hourForecast) { this.hourForecast = hourForecast; return this; }

        public Weather build() {

            return new Weather(this);
        }
    }

    private Weather(Builder builder) {

        this.iconId = builder.iconId;
        this.lastUpdated = builder.lastUpdated;
        this.temperature = builder.temperature;
        this.windInfo = builder.windInfo;
        this.humidityInfo = builder.humidityInfo;
        this.visibilityInfo = builder.visibilityInfo;
        this.forecast = builder.forecast;
        this.hourForecast = builder.hourForecast;
    }

    public int getIconId() {
        return iconId;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public String getWindInfo() {
        return windInfo;
    }

    public String getHumidityInfo() {
        return humidityInfo;
    }

    public String getVisibilityInfo() {
        return visibilityInfo;
    }

    public List<ForecastDayWeather> getForecast() {
        return forecast;
    }

    public List<ForecastDayWeather> getHourForecast() {
        return hourForecast;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "iconId=" + iconId +
                ", temperature='" + temperature + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                ", windInfo='" + windInfo + '\'' +
                ", humidityInfo='" + humidityInfo + '\'' +
                ", visibilityInfo='" + visibilityInfo + '\'' +
                ", forecast=" + forecast +
                ", hourForecast=" + hourForecast +
                '}';
    }
}

