package com.nielsmasdorp.speculum.models.destination;

public class Line {
    private String name;
    private String shortName;

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    @Override
    public String toString() {
        return "Line{" +
                "name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                '}';
    }
}
