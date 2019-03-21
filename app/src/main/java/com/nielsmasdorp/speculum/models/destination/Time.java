package com.nielsmasdorp.speculum.models.destination;

public class Time {
    private String text;
    private Integer value;

    public String getText() {
        return text;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Time{" +
                "text='" + text + '\'' +
                ", value=" + value +
                '}';
    }
}
