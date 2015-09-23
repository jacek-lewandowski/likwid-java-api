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
