package com.nielsmasdorp.speculum.views;

import com.nielsmasdorp.speculum.models.Weather;

/**
 * @author Niels Masdorp (NielsMasdorp)
 */
public interface MainView extends BaseView {

	void showListening();

	void hideListening();

	void displayCurrentWeather(Weather weather);

	void displayCalendarEvents(String events);

}
