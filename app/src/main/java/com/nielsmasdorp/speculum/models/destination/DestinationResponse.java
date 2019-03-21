package com.nielsmasdorp.speculum.models.destination;

import java.util.ArrayList;
import java.util.List;

public class DestinationResponse {
    private List<Route> routes = new ArrayList<>();

    public List<Route> getRoutes() {
        return routes;
    }

    @Override
    public String toString() {
        return "DestinationResponse{" +
                "routes=" + routes +
                '}';
    }
}
