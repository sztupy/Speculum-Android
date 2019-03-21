package com.nielsmasdorp.speculum.models.destination;

public class Step {
    private String travelMode;
    private TransitDetails transitDetails;

    public String getTravelMode() {
        return travelMode;
    }

    public TransitDetails getTransitDetails() {
        return transitDetails;
    }

    @Override
    public String toString() {
        return "Step{" +
                "travelMode='" + travelMode + '\'' +
                ", transitDetails=" + transitDetails +
                '}';
    }
}
