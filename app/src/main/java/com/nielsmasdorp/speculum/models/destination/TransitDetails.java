package com.nielsmasdorp.speculum.models.destination;

public class TransitDetails {
    private Time arrivalTime;
    private Time departureTime;
    private Stop departureStop;
    private Stop arrivalStop;
    private String headsign;
    private Line line;

    public Time getArrivalTime() {
        return arrivalTime;
    }

    public Time getDepartureTime() {
        return departureTime;
    }

    public Stop getDepartureStop() {
        return departureStop;
    }

    public Stop getArrivalStop() {
        return arrivalStop;
    }

    public String getHeadsign() {
        return headsign;
    }

    public Line getLine() {
        return line;
    }

    @Override
    public String toString() {
        return "TransitDetails{" +
                "arrivalTime=" + arrivalTime +
                ", departureTime=" + departureTime +
                ", departureStop=" + departureStop +
                ", arrivalStop=" + arrivalStop +
                ", headsign='" + headsign + '\'' +
                ", line=" + line +
                '}';
    }
}
