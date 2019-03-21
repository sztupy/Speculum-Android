package com.nielsmasdorp.speculum.models;

import java.util.Date;
import java.util.List;

public class TravelDetails {
    private List<TravelRoute> routes;
    private Date lastUpdated;
    private String destination;

    public TravelDetails(List<TravelRoute> routes, Date lastUpdated, String destination) {
        this.routes = routes;
        this.lastUpdated = lastUpdated;
        this.destination = destination;
    }

    public List<TravelRoute> getRoutes() {
        return routes;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public String getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return "TravelDetails{" +
                "routes=" + routes +
                ", lastUpdated=" + lastUpdated +
                ", destination='" + destination + '\'' +
                '}';
    }
}
