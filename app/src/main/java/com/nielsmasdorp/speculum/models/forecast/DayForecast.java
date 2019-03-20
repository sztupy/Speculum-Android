package com.nielsmasdorp.speculum.models.forecast;

/**
 * @author Niels Masdorp (NielsMasdorp)
 */
@SuppressWarnings("unused")
public class DayForecast {

    private Integer time;
    private String icon;
    private Float temperatureMin;
    private Float temperatureMax;
    private Float temperature;
    private Float apparentTemperature;
    private Float precipIntensity;
    private Float precipProbability;
    private Float cloudCover;

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Float getTemperatureMin() {
        return temperatureMin;
    }

    public Float getTemperatureMax() {
        return temperatureMax;
    }

    public Float getTemperature() {
        return temperature;
    }

    public Float getApparentTemperature() {
        return apparentTemperature;
    }

    public Float getPrecipIntensity() {
        return precipIntensity;
    }

    public Float getPrecipProbability() {
        return precipProbability;
    }

    public Float getCloudCover() {
        return cloudCover;
    }

    @Override
    public String toString() {
        return "DayForecast{" +
                "time=" + time +
                ", icon='" + icon + '\'' +
                ", temperatureMin=" + temperatureMin +
                ", temperatureMax=" + temperatureMax +
                ", temperature=" + temperature +
                ", apparentTemperature=" + apparentTemperature +
                ", precipIntensity=" + precipIntensity +
                ", precipProbability=" + precipProbability +
                ", cloudCover=" + cloudCover +
                '}';
    }
}
