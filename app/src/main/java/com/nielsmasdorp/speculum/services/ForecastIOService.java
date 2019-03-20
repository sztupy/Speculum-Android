package com.nielsmasdorp.speculum.services;

import android.app.Application;
import android.text.format.DateFormat;
import android.util.Log;

import com.nielsmasdorp.speculum.models.ForecastDayWeather;
import com.nielsmasdorp.speculum.models.Weather;
import com.nielsmasdorp.speculum.models.forecast.DayForecast;
import com.nielsmasdorp.speculum.models.forecast.ForecastResponse;
import com.nielsmasdorp.speculum.util.Constants;
import com.nielsmasdorp.speculum.util.WeatherIconGenerator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author Niels Masdorp (NielsMasdorp)
 */
public class ForecastIOService {

    private static final String LOG_TAG = "ForecastIOService";

    private ForecastIOApi forecastIOApi;

    public ForecastIOService() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.FORECAST_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        forecastIOApi = retrofit.create(ForecastIOApi.class);
    }

    public Observable<Weather> getCurrentWeather(ForecastResponse response,
                                                 WeatherIconGenerator iconGenerator,
                                                 Application application,
                                                 boolean metric) {

        Log.i(LOG_TAG, "Weather report: " + response.toString());

        boolean is24HourFormat = DateFormat.is24HourFormat(application);

        String distanceUnit = metric ? Constants.DISTANCE_METRIC : Constants.DISTANCE_IMPERIAL;
        String pressureUnit = metric ? Constants.PRESSURE_METRIC : Constants.PRESSURE_IMPERIAL;
        String speedUnit = metric ? Constants.SPEED_METRIC : Constants.SPEED_IMPERIAL;
        String temperatureUnit = metric ? Constants.TEMPERATURE_METRIC : Constants.TEMPERATURE_IMPERIAL;

        // Convert degrees to cardinal directions for wind
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "N"};
        String direction = directions[(int) Math.round((response.getCurrently().getWindBearing() % 360) / 45)];

        List<ForecastDayWeather> forecast = new ArrayList<>();

        int maxHours = 24 * 5;
        for (DayForecast f : response.getHourly().getData()) {
            float temperature = (f.getTemperature() == null) ? (f.getTemperatureMax() + f.getTemperatureMin() / 2) : f.getTemperature();
            float precipProbability = f.getPrecipProbability() == null ? 0 : f.getPrecipProbability();
            float precipIntensity = f.getPrecipIntensity() == null ? 0 : f.getPrecipIntensity();
            float cloudCover = f.getCloudCover() == null ? 0 : f.getCloudCover();
            int iconId = iconGenerator.getIcon(f.getIcon());
            maxHours -= 1;
            if (maxHours>0) {
                forecast.add(new ForecastDayWeather(iconId, temperature, precipProbability, precipIntensity, cloudCover, new Date((long) f.getTime() * 1000)));
            }
        }

        return Observable.just(new Weather.Builder()
                .iconId(iconGenerator.getIcon(response.getCurrently().getIcon()))
                .summary(response.getCurrently().getSummary())
                .temperature(String.format(Locale.getDefault(), "%.1fº%s", response.getCurrently().getTemperature(), temperatureUnit))
                .lastUpdated(new SimpleDateFormat(!is24HourFormat ? "h:mm" : "H:mm", Locale.getDefault()).format(new Date((long) response.getCurrently().getTime() * 1000)))
                .windInfo(response.getCurrently().getWindSpeed().intValue() + speedUnit + " " + direction + " | " + response.getCurrently().getApparentTemperature().intValue() + "º" + temperatureUnit)
                .humidityInfo((int) (response.getCurrently().getHumidity() * 100) + "%")
                .pressureInfo(response.getCurrently().getPressure().intValue() + pressureUnit)
                .visibilityInfo(response.getCurrently().getVisibility().intValue() + distanceUnit)
                .forecast(forecast)
                .build());
    }

    public ForecastIOApi getApi() {

        return forecastIOApi;
    }

    public interface ForecastIOApi {

        @GET("{apiKey}/{latLong}")
        Observable<ForecastResponse> getCurrentWeatherConditions(@Path("apiKey") String apiKey, @Path("latLong") String latLong, @Query("units") String units, @Query("extend") String extend);
    }
}
