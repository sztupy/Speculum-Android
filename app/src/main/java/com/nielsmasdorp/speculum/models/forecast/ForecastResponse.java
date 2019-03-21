package com.nielsmasdorp.speculum.models.forecast;

/**
 * @author Niels Masdorp (NielsMasdorp)
 */
@SuppressWarnings("unused")
public class ForecastResponse {

    private Currently currently;
    private Forecast minutely;
    private Forecast daily;
    private Forecast hourly;

    public Currently getCurrently() {
        return currently;
    }

    public Forecast getMinutely() {
        return minutely;
    }

    public Forecast getDaily() {
        return daily;
    }

    public Forecast getHourly() { return hourly; }

    @Override
    public String toString() {
        return "ForecastResponse{" +
                "currently=" + currently +
                ", minutely=" + minutely +
                ", daily=" + daily +
                ", hourly=" + hourly +
                '}';
    }
}
