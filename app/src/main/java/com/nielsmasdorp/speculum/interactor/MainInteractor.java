package com.nielsmasdorp.speculum.interactor;

import com.nielsmasdorp.speculum.models.Weather;

import java.io.File;

import rx.Subscriber;

/**
 * @author Niels Masdorp (NielsMasdorp)
 */
public interface MainInteractor {

    void loadCalendarEvents(int updateDelay, Subscriber<String> subscriber);

    void loadWeather(String location, boolean celsius, int updateDelay, String apiKey, Subscriber<Weather> subscriber);

    void loadSecondScheduler(Subscriber<Long> subscriber);

    void unsubscribeAll();

    void unsubscribeTemporary();
}
