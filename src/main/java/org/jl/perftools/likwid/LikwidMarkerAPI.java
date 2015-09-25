package org.jl.perftools.likwid;

public class LikwidMarkerAPI {
    public native void init();

    public native void threadInit();

    public native void register(String tag);

    public native void start(String tag);

    public native void stop(String tag);

    public native void getResults(String tag, int eventsNumbers, LikwidMarkerResults results);

    public native void nextGroup();

    public native void close();

}
