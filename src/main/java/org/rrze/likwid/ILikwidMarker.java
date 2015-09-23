package org.rrze.likwid;

public interface ILikwidMarker {
    /**
     * Initialize the Marker API and read the configured eventsets from the environment
     */
    void init();

    /**
     * Add thread to the Marker API and initialize access to the performance counters (start daemon
     * or open device files)
     */
    void threadInit();

    /**
     * Register a region name tag to the Marker API. This creates an entry in the internally used hash
     * table to minimize overhead at the start of the named region. This call is optional, the same
     * operations are done by start if not registered previously.
     *
     * @param tag tag name
     */
    void register(String tag);

    /**
     * Start a named region identified by tag. This reads the counters are stores the values in
     * the thread's hash table. If the hash table entry tag does not exist, it is created.
     *
     * @param tag tag name
     */
    void start(String tag);

    /**
     * Stop a named region identified by tag. This reads the counters are stores the values in
     * the thread's hash table. It is assumed that a this method can only follow
     * a {@link #start(String)}, hence no existence check of the hash table entry is performed.
     *
     * @param tag tag name
     */
    void stop(String tag);

    /**
     * If you want to process a code regions measurement results in the instrumented application itself,
     * you can call this function to get the intermediate results. The region is identified by tag.
     * The nevents parameter is used to specify the length of the events array. After the function
     * returns, nevents is the number of events filled in the events array. The aggregated measurement
     * time is returned in time and the amount of measurements is returned in count.
     */
    LikwidMarkerResults getResults(String tag, int eventsNumbers);

    /**
     * Switch to the next eventset. If only a single eventset is given, the function performs
     * no operation. If multiple eventsets are configured, this function switches through the eventsets
     * in a round-robin fashion.
     * <p/>
     * Notice: This function creates the biggest overhead of all Marker API functions as it has to setup
     * the register to the next eventset.
     */
    void nextGroup();

    /**
     * Finalize the Marker API and write the aggregated results of all regions to a file that is picked
     * up by likwid-perfctr for evaulation.
     */
    void close();
}
