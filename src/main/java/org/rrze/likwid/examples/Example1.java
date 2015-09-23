package org.rrze.likwid.examples;

import org.rrze.likwid.ILikwidMarker;
import org.rrze.likwid.LikwidMarkerAPIProvider;
import org.rrze.likwid.LikwidMarkerResults;

public class Example1 {
    long[] arr = new long[1 << 22];
    long sum = 0;
    int prime = 104729;
    int idx = 0;
    int mask = (1 << 22) - 1;
    int warmUpItrs = 5;
    int iterations = 1;
    int amount = 100000000;

    // test1 - expect low number of L1 cache misses
    public void test1(int amount) {
        idx = 0;
        for (int i = 0; i < amount; i++) {
            sum += arr[idx];
            idx = (idx + 1) & mask;
        }
    }

    // test1 - expect high number of L1 cache misses
    public void test2(int amount) {
        for (int i = 0; i < amount; i++) {
            sum += arr[idx];
            idx = (idx + prime) & mask;
        }
    }

    public void run() {
        // Initialize marker API and get its instance. If marker API is not available or disabled, it will return
        // a dummy implementation which will be inlined by Java VM and no performance impact will be introduced.
        ILikwidMarker marker = LikwidMarkerAPIProvider.getInstance();

        // Need to init marker API ahead of registering any marker
        marker.init();
        try {
            // warm-up for 1st test
            for (int i = 0; i < warmUpItrs; i++) test1(amount);

            // Likwid will be aware if the code region is repeated and it will calculate average ratios
            for (int i = 0; i < iterations; i++) {
                // Open a tagged code region for measurements
                marker.start("test1");
                test1(amount);
                // Closed a tagged code region for measurements
                marker.stop("test1");
            }

            // warm-up for 2nd test
            for (int i = 0; i < warmUpItrs; i++) test2(amount);
            for (int i = 0; i < iterations; i++) {
                // Open another tagged code region for measurements
                marker.start("test2");
                test2(amount);
                // Closed a tagged code region for measurements
                marker.stop("test2");
            }

            // The results can be obtained directly in Java
            LikwidMarkerResults results1 = marker.getResults("test1", 0);
            System.out.println(results1);

            LikwidMarkerResults results2 = marker.getResults("test2", 0);
            System.out.println(results2);

            System.out.printf("Which just means that test2 was on average %f faster than test1%n",
                    (results2.getTime() / results2.getCount()) / (results1.getTime() / results1.getCount()));

        } finally {
            // Remember to close the marker API
            marker.close();
        }

        System.out.println(sum);
    }


    public static void main(String[] args) {
        Example1 example = new Example1();
        example.run();
    }
}
