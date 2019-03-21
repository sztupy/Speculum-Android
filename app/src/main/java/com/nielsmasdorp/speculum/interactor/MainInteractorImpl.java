package com.nielsmasdorp.speculum.interactor;

import android.app.Application;
import android.util.Log;

import com.nielsmasdorp.speculum.models.TravelDetails;
import com.nielsmasdorp.speculum.models.TravelRoute;
import com.nielsmasdorp.speculum.models.Weather;
import com.nielsmasdorp.speculum.services.GoogleMapsDestinationService;
import com.nielsmasdorp.speculum.util.Observables;
import com.nielsmasdorp.speculum.services.ForecastIOService;
import com.nielsmasdorp.speculum.services.GoogleCalendarService;
import com.nielsmasdorp.speculum.util.Constants;
import com.nielsmasdorp.speculum.util.WeatherIconGenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.cmu.pocketsphinx.Assets;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Niels Masdorp (NielsMasdorp)
 */
public class MainInteractorImpl implements MainInteractor {

    private static final String LOG_TAG = "MainInteractor";
    private static int AMOUNT_OF_RETRIES = 3;
    private static int DELAY_IN_SECONDS = 10;

    private Application application;
    private ForecastIOService forecastIOService;
    private GoogleCalendarService googleCalendarService;
    private GoogleMapsDestinationService googleMapsDestinationService;
    private WeatherIconGenerator weatherIconGenerator;
    private CompositeSubscription longRunningCompositeSubscription;
    private CompositeSubscription temporaryCompositeSubscription;

    public MainInteractorImpl(Application application, ForecastIOService forecastIOService,
                              GoogleCalendarService googleCalendarService,
                              WeatherIconGenerator weatherIconGenerator,
                              GoogleMapsDestinationService googleMapsDestinationService) {

        this.application = application;
        this.forecastIOService = forecastIOService;
        this.googleCalendarService = googleCalendarService;
        this.weatherIconGenerator = weatherIconGenerator;
        this.googleMapsDestinationService = googleMapsDestinationService;
        this.longRunningCompositeSubscription = new CompositeSubscription();
        this.temporaryCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void loadDepartureMap(int updateDelay, String departure, String destination, String destinationName, String apiKey, Subscriber<TravelDetails> subscriber) {
        longRunningCompositeSubscription.add(Observable.interval(0, updateDelay, TimeUnit.MINUTES, Schedulers.io())
                .flatMap(ignore -> googleMapsDestinationService.getApi().getDirections(departure, destination, "transit", apiKey, "bus", "true", "fewer_transfers" ))
                .flatMap(response -> googleMapsDestinationService.getTravelRoutes(response,destinationName))
                .retryWhen(Observables.exponentialBackoff(AMOUNT_OF_RETRIES, DELAY_IN_SECONDS, TimeUnit.SECONDS))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(s -> Log.i(LOG_TAG, "Observe fired: " + s + " on thread " + Thread.currentThread().getName()))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribe(subscriber));
    }

    @Override
    public void loadCalendarEvents(int updateDelay, Subscriber<String> subscriber) {

        longRunningCompositeSubscription.add(Observable.interval(0, updateDelay, TimeUnit.MINUTES)
                .flatMap(ignore -> googleCalendarService.getCalendarEvents())
                .retryWhen(Observables.exponentialBackoff(AMOUNT_OF_RETRIES, DELAY_IN_SECONDS, TimeUnit.SECONDS))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribe(subscriber));
    }

    @Override
    public void loadWeather(String location, boolean celsius, int updateDelay, String apiKey, Subscriber<Weather> subscriber) {

        final String query = celsius ? Constants.WEATHER_QUERY_SECOND_CELSIUS : Constants.WEATHER_QUERY_SECOND_FAHRENHEIT;

        longRunningCompositeSubscription.add(Observable.interval(0, updateDelay, TimeUnit.MINUTES, Schedulers.io())
                .doOnNext(s -> Log.i(LOG_TAG, "Interval fired: " + s + " on thread " + Thread.currentThread().getName()))
                .flatMap(ignore -> forecastIOService.getApi().getCurrentWeatherConditions(apiKey, location, query, "hourly"))
                .flatMap(response -> forecastIOService.getCurrentWeather(response, weatherIconGenerator, application, celsius))
                .retryWhen(Observables.exponentialBackoff(AMOUNT_OF_RETRIES, DELAY_IN_SECONDS, TimeUnit.SECONDS))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(s -> Log.i(LOG_TAG, "Observe fired: " + s + " on thread " + Thread.currentThread().getName()))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribe(subscriber));
    }

    @Override
    public void loadSecondScheduler(Subscriber<Long> subscriber) {
        temporaryCompositeSubscription.add(Observable.interval(0, 1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .subscribe(subscriber));
    }

    @Override
    public void unsubscribeTemporary() {
        temporaryCompositeSubscription.clear();
    }

    @Override
    public void unsubscribeAll() {
        temporaryCompositeSubscription.clear();
        longRunningCompositeSubscription.clear();
    }
}
