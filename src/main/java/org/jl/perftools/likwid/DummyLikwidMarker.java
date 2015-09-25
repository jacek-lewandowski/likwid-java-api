package org.jl.perftools.likwid;

class DummyLikwidMarker implements ILikwidMarker {
    public final static DummyLikwidMarker INSTANCE = new DummyLikwidMarker();

    private DummyLikwidMarker() {
        // empty on purpose
    }

    public void init() {
        // empty on purpose
    }

    public void threadInit() {
        // empty on purpose
    }

    public void register(String tag) {
        // empty on purpose
    }

    public void start(String tag) {
        // empty on purpose
    }

    public void stop(String tag) {
        // empty on purpose
    }

    public LikwidMarkerResults getResults(String tag, int eventsNumbers) {
        LikwidMarkerResults results = new LikwidMarkerResults();
        results.setCount(0);
        results.setTime(0);
        results.setEvents(new double[0]);
        return results;
    }

    public void nextGroup() {
        // empty on purpose
    }

    public void close() {
        // empty on purpose
    }
}
