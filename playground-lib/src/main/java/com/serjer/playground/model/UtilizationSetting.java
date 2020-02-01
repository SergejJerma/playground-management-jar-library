package com.serjer.playground.model;
import java.util.concurrent.TimeUnit;

public class UtilizationSetting {

    private TimeUnit timeUnit = TimeUnit.MINUTES;
    private int startTimeIn24HourClock = 9;
    private int stopTimeIn24HourClock = 5;

    public UtilizationSetting() {

    }

    public UtilizationSetting(TimeUnit timeUnit, int startTimeIn24HourClock, int stopTimeIn24HourClock) {
        this.timeUnit = timeUnit;
        this.startTimeIn24HourClock = startTimeIn24HourClock;
        this.stopTimeIn24HourClock = stopTimeIn24HourClock;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public int getStartTimeIn24HourClock() {
        return startTimeIn24HourClock;
    }

    public int getStopTimeIn24HourClock() {
        return stopTimeIn24HourClock;
    }
}
