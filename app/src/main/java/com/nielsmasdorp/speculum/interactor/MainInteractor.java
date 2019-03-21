package com.nielsmasdorp.speculum.interactor;

import com.nielsmasdorp.speculum.models.TravelDetails;
import com.nielsmasdorp.speculum.models.TravelRoute;
import com.nielsmasdorp.speculum.models.Weather;

import java.io.File;
import java.util.List;

import rx.Subscriber;

/**
 * @author Niels Masdorp (NielsMasdorp)
 */
public interface MainInteractor {

    void loadCalendarEvents(int updateDelay, Subscriber<String> subscriber);

    void loadWeather(String location, boolean celsius, int updateDelay, String apiKey, Subscriber<Weather> subscriber);

    void loadSecondScheduler(Subscriber<Long> subscriber);

    void loadDepartureMap(int updateDelay, String departure, String destination, String destinationName, String apiKey, Subscriber<TravelDetails> subscriber);

    void unsubscribeAll();

    void unsubscribeTemporary();
}
