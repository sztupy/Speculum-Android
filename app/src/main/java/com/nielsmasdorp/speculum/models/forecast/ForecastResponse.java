package com.nielsmasdorp.speculum.models.forecast;

/**
 * @author Niels Masdorp (NielsMasdorp)
 */
@SuppressWarnings("unused")
public class ForecastResponse {

    private Currently currently;
    private Forecast daily;
    private Forecast hourly;

    public Currently getCurrently() {
        return currently;
    }

    public Forecast getForecast() {
        return daily;
    }

    public Forecast getDaily() {
        return daily;
    }

    public Forecast getHourly() { return hourly; }

    @Override
    public String toString() {
        return "ForecastResponse{" +
                "currently=" + currently +
                ", daily=" + daily +
                ", hourly=" + hourly +
                '}';
    }
}
