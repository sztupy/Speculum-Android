package com.nielsmasdorp.speculum.models.destination;

import java.util.ArrayList;
import java.util.List;

public class Leg {
    private Time arrivalTime;
    private Time departureTime;

    private List<Step> steps = new ArrayList<>();

    public Time getArrivalTime() {
        return arrivalTime;
    }

    public Time getDepartureTime() {
        return departureTime;
    }

    public List<Step> getSteps() {
        return steps;
    }

    @Override
    public String toString() {
        return "Leg{" +
                "arrivalTime=" + arrivalTime +
                ", departureTime=" + departureTime +
                ", steps=" + steps +
                '}';
    }
}
