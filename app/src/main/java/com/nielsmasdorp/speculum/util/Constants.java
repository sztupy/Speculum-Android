package com.nielsmasdorp.speculum.util;

/**
 * @author Niels Masdorp (NielsMasdorp)
 */
public class Constants {

    /**
     * Base urls
     */
    public static final String FORECAST_BASE_URL = "https://api.forecast.io/forecast/";

    /**
     * Weather query
     */
    public static final String WEATHER_QUERY_SECOND_CELSIUS = "ca";
    public static final String WEATHER_QUERY_SECOND_FAHRENHEIT = "us";

    /**
     * Calendar query
     */
    public static final String CALENDAR_QUERY_FIRST = "( dtstart >";
    public static final String CALENDAR_QUERY_SECOND = ") and (dtend  <";
    public static final String CALENDAR_QUERY_THIRD = ")";
    public static final String CALENDAR_QUERY_FOURTH = "dtstart ASC";

    /**
     * Formats
     */
    public static final String SIMPLEDATEFORMAT_DDMMYY = "dd/MM/yy";
    public static final String SIMPLEDATEFORMAT_HHMMSSDDMMYY = "hh:mm:ss dd/MM/yy";
    public static final String END_OF_DAY_TIME = "23:59:59 ";

    /**
     * Units
     */
    public static final String PRESSURE_IMPERIAL = "in";
    public static final String PRESSURE_METRIC = "mb";

    public static final String SPEED_IMPERIAL = "mph";
    public static final String SPEED_METRIC = "km/h";

    public static final String DISTANCE_IMPERIAL = "mi";
    public static final String DISTANCE_METRIC = "km";

    public static final String TEMPERATURE_IMPERIAL = "F";
    public static final String TEMPERATURE_METRIC = "C";


    /**
     * Shared preferences identifiers
     */
    public static final String SP_LOCATION_IDENTIFIER = "location";
    public static final String SP_POLLING_IDENTIFIER = "pollingDelay";
    public static final String SP_CELSIUS_IDENTIFIER = "celsius";
}
