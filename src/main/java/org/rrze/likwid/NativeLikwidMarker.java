package org.rrze.likwid;

class NativeLikwidMarker implements ILikwidMarker {
    private final LikwidMarkerAPI api;

    public NativeLikwidMarker(LikwidMarkerAPI api) {
        this.api = api;
    }

    public void init() {
        api.init();
    }

    public void threadInit() {
        api.threadInit();
    }

    public void register(String tag) {
        api.register(tag);
    }

    public void start(String tag) {
        api.start(tag);
    }

    public void stop(String tag) {
        api.stop(tag);
    }

    public LikwidMarkerResults getResults(String tag, int eventsNumber) {
        LikwidMarkerResults results = new LikwidMarkerResults();
        api.getResults(tag, eventsNumber, results);
        return results;
    }

    public void nextGroup() {
        api.nextGroup();
    }

    public void close() {
        api.close();
    }
}
