package com.nielsmasdorp.speculum.models.destination;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private List<Leg> legs = new ArrayList<>();

    public List<Leg> getLegs() {
        return legs;
    }

    @Override
    public String toString() {
        return "TravelRoute{" +
                "legs=" + legs +
                '}';
    }
}
