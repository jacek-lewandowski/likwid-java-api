# likwid-java-api
A Java API for the performance monitoring and benchmarking suite called likwid (https://github.com/rrze-likwid/likwid)

This API allows you to put tagged markers into your code so that only those parts of your code will be measured by 
``likwid-perfctr``. 

## Requirements 
- Linux
- Properly installed likwid tool
- msr module loaded

Note: If you are running Linux in a virtual machine, make sure it is VMWare. To my knowledge, VirtualBox doesn't support
performance counters so this stuff won't work.

## Installation
In order to use this API, you need to:

- Make sure you have installed likwid properly and msr module is loaded (``modprobe msr``)
- Clone the project onto your local machine:
```bash
git clone https://github.com/jlewandowski/likwid-java-api.git likwid-java-api
```
- Build it and install into your local Maven repository:
```bash
mvn clean install
```
- Run the example application:
```bash
likwid-perfctr -C 0 -g TLB_DATA -m java -DLIKWID_PERFMON -cp target/likwid-java-api-1.0-SNAPSHOT.jar org.rrze.likwid.examples.Example1
```

## Enable/Disable
Likwid markers works there is ``LIKWID_PERFMON`` system property defined. You also need to run ``likwid-perfctr`` with 
``-m`` parameter to enable measurements for marked regions.
 
If ``LIKWID_PERFMON`` is not defined, an empty API implementation will be returned to the user. Therefore, Java should
inline all the empty method and it should have no impact on the performance.

## Example
The full example code can be found in Java sources. This is the only important part:

```java
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

} finally {
    // Remember to close the marker API
    marker.close();
}
```

The interesting methods which we want to test performance of are ``test1`` and ``test2``. We would like to compare 
them, so we enclose their invocations by marking start and stop of tagged region. The code inside a single region
can be invoked multiple times - in this case, likwid will sum the number of events and average the calculated ratios. 
 
An output of this example could be: 
```
# likwid-perfctr -C 0 -g TLB_DATA -m java -DLIKWID_PERFMON -cp target/likwid-java-api-1.0-SNAPSHOT.jar org.rrze.likwid.examples.Example1
--------------------------------------------------------------------------------
CPU name:	Intel(R) Core(TM) i7-4850HQ CPU @ 2.30GHz
CPU type:	Intel Core Haswell processor
CPU clock:	2.29 GHz
--------------------------------------------------------------------------------
Likwid markers are enabled.
LikwidMarkerResults{events=[], time=0.3667285972945661, count=5}
LikwidMarkerResults{events=[], time=6.979190770504919, count=5}
Which just means that test2 was on average 19.030942 faster than test1
0
--------------------------------------------------------------------------------
================================================================================
Group 1 TLB_DATA: Region test1
================================================================================
+-------------------+----------+
|    Region Info    |  Core 0  |
+-------------------+----------+
| RDTSC Runtime [s] | 0.366729 |
|     call count    |     5    |
+-------------------+----------+
```

This above fragment says that the code inside ``test1`` region was executed 5 times, which took 0.367 s in total.
```

+---------------------------------+---------+--------------+
|              Event              | Counter |    Core 0    |
+---------------------------------+---------+--------------+
|        INSTR_RETIRED_ANY        |  FIXC0  | 3.875063e+09 |
|      CPU_CLK_UNHALTED_CORE      |  FIXC1  | 1.260274e+09 |
|       CPU_CLK_UNHALTED_REF      |  FIXC2  | 8.292720e+08 |
|  DTLB_LOAD_MISSES_CAUSES_A_WALK |   PMC0  | 4.925000e+03 |
| DTLB_STORE_MISSES_CAUSES_A_WALK |   PMC1  | 1.290000e+03 |
|  DTLB_LOAD_MISSES_WALK_DURATION |   PMC2  | 4.292070e+05 |
| DTLB_STORE_MISSES_WALK_DURATION |   PMC3  | 1.686980e+05 |
+---------------------------------+---------+--------------+

+-----------------------------------+--------------+
|               Metric              |    Core 0    |
+-----------------------------------+--------------+
|        Runtime (RDTSC) [s]        |   0.3667286  |
|        Runtime unhalted [s]       | 5.491927e-01 |
|            Clock [MHz]            | 3.487451e+03 |
|                CPI                | 3.252267e-01 |
|        L1 DTLB load misses        |     4925     |
|       L1 DTLB load miss rate      | 1.270947e-06 |
|  L1 DTLB load miss duration [Cyc] | 8.714863e+01 |
|        L1 DTLB store misses       |     1290     |
|      L1 DTLB store miss rate      | 3.328978e-07 |
| L1 DTLB store miss duration [Cyc] | 1.307736e+02 |
+-----------------------------------+--------------+
```
The above statistics says what happened in region ``test1`` - for example, there were 4925 L1 DTLB load misses during
all of the repetitions which gives ``1.270947e-06`` average ratio.

The region marked with ``test2`` tag gives the following results:
```

================================================================================
Group 1 TLB_DATA: Region test2
================================================================================
+-------------------+----------+
|    Region Info    |  Core 0  |
+-------------------+----------+
| RDTSC Runtime [s] | 6.979191 |
|     call count    |     5    |
+-------------------+----------+

+---------------------------------+---------+--------------+
|              Event              | Counter |    Core 0    |
+---------------------------------+---------+--------------+
|        INSTR_RETIRED_ANY        |  FIXC0  | 3.875258e+09 |
|      CPU_CLK_UNHALTED_CORE      |  FIXC1  | 2.377920e+10 |
|       CPU_CLK_UNHALTED_REF      |  FIXC2  | 1.576489e+10 |
|  DTLB_LOAD_MISSES_CAUSES_A_WALK |   PMC0  | 1.002010e+09 |
| DTLB_STORE_MISSES_CAUSES_A_WALK |   PMC1  | 1.792100e+04 |
|  DTLB_LOAD_MISSES_WALK_DURATION |   PMC2  | 1.870362e+10 |
| DTLB_STORE_MISSES_WALK_DURATION |   PMC3  | 1.450194e+06 |
+---------------------------------+---------+--------------+

+-----------------------------------+--------------+
|               Metric              |    Core 0    |
+-----------------------------------+--------------+
|        Runtime (RDTSC) [s]        |   6.979191   |
|        Runtime unhalted [s]       | 1.036232e+01 |
|            Clock [MHz]            | 3.461358e+03 |
|                CPI                | 6.136159e+00 |
|        L1 DTLB load misses        |  1002010000  |
|       L1 DTLB load miss rate      | 2.585660e-01 |
|  L1 DTLB load miss duration [Cyc] | 1.866610e+01 |
|        L1 DTLB store misses       |     17921    |
|      L1 DTLB store miss rate      | 4.624466e-06 |
| L1 DTLB store miss duration [Cyc] | 8.092149e+01 |
+-----------------------------------+--------------+
```
