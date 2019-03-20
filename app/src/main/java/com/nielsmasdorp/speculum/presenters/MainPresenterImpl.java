package com.nielsmasdorp.speculum.presenters;

import android.app.Application;
import android.os.Build;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.nielsmasdorp.speculum.R;
import com.nielsmasdorp.speculum.activity.MainActivity;
import com.nielsmasdorp.speculum.interactor.MainInteractor;
import com.nielsmasdorp.speculum.models.Configuration;
import com.nielsmasdorp.speculum.models.Weather;
import com.nielsmasdorp.speculum.util.Constants;
import com.nielsmasdorp.speculum.views.MainView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

/**
 * @author Niels Masdorp (NielsMasdorp)
 */
public class MainPresenterImpl implements MainPresenter {

    private MainView view;
    private MainInteractor interactor;
    private Application application;
    private Configuration configuration;

    public MainPresenterImpl(MainView view, MainInteractor interactor, Application application) {
        this.view = view;
        this.interactor = interactor;
        this.application = application;
    }

    /*
    Begin presenter methods
     */
    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void start(boolean hasAccessToCalendar) {
        if (null != configuration) {
            interactor.loadWeather(
                    configuration.getLocation(),
                    configuration.isCelsius(),
                    configuration.getPollingDelay(),
                    ((MainActivity)view).getString(R.string.forecast_api_key),
                    new WeatherSubscriber()
            );
            if (hasAccessToCalendar) {
                interactor.loadCalendarEvents(configuration.getPollingDelay(), new CalendarEventSubscriber());
            }

        }
    }

    @Override
    public void finish() {
        interactor.unsubscribeAll();
    }

    @Override
    public void foreground() {
        interactor.loadSecondScheduler(new SecondsSubscriber());
    }

    @Override
    public void background() {
        interactor.unsubscribeTemporary();
    }

    private final class WeatherSubscriber extends Subscriber<Weather> {

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            view.showError(e.getMessage());
        }

        @Override
        public void onNext(Weather weather) {
            view.displayCurrentWeather(weather);
        }
    }

    private final class CalendarEventSubscriber extends Subscriber<String> {

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            view.showError(e.getMessage());
        }

        @Override
        public void onNext(String events) {
            view.displayCalendarEvents(events);
        }
    }


    private final class SecondsSubscriber extends Subscriber<Long> {

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            view.showError(e.getMessage());
        }

        @Override
        public void onNext(Long events) {
            view.updateTimeRemaining();
        }
    }
}
