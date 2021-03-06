package com.nielsmasdorp.speculum.views;

import com.nielsmasdorp.speculum.models.TravelDetails;
import com.nielsmasdorp.speculum.models.Weather;

/**
 * @author Niels Masdorp (NielsMasdorp)
 */
public interface MainView extends BaseView {
	void displayCurrentWeather(Weather weather);

	void displayCalendarEvents(String events);

	void updateTimeRemaining();

	void ping();

	void updateRoutes(int id, TravelDetails routeList);
}
