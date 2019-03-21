package com.nielsmasdorp.speculum.di.module;

import android.app.Application;

import com.nielsmasdorp.speculum.services.ForecastIOService;
import com.nielsmasdorp.speculum.services.GoogleCalendarService;
import com.nielsmasdorp.speculum.services.GoogleMapsDestinationService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Niels Masdorp (NielsMasdorp)
 */
@Module
public class ServiceModule {

    @Provides
    @Singleton
    public ForecastIOService provideForecastIOService() {

        return new ForecastIOService();
    }

    @Provides
    @Singleton
    public GoogleCalendarService provideGoogleCalendarService(Application application) {

        return new GoogleCalendarService(application);
    }

    @Provides
    @Singleton
    public GoogleMapsDestinationService provideGoogleMapsDestinationService() {
        return new GoogleMapsDestinationService();
    }
}
