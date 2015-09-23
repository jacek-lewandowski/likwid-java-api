package org.rrze.likwid;

import java.util.Arrays;

public class LikwidMarkerResults {
    private double[] events;
    private double time;
    private int count;

    public LikwidMarkerResults() {
    }

    public double[] getEvents() {
        return events;
    }

    public void setEvents(double[] events) {
        this.events = events;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    @Override
    public String toString() {
        return "LikwidMarkerResults{" +
                "events=" + Arrays.toString(events) +
                ", time=" + time +
                ", count=" + count +
                '}';
    }
}
