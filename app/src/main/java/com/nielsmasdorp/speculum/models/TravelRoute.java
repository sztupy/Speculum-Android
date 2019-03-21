package com.nielsmasdorp.speculum.models;

import java.util.Date;

public class TravelRoute {
    private CharSequence route;
    private String firstBus;
    private Date departureTime;
    private Date arrivalTime;

    public TravelRoute(CharSequence route, String firstBus, Date departureTime, Date arrivalTime) {
        this.route = route;
        this.firstBus = firstBus;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public CharSequence getRoute() {
        return route;
    }

    public String getFirstBus() {
        return firstBus;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public String toString() {
        return "TravelRoute{" +
                "route='" + route + '\'' +
                ", firstBus='" + firstBus + '\'' +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                '}';
    }
}
