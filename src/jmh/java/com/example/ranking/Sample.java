package com.example.ranking;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class Sample {

    private ArrayList<Integer> arrayList = new ArrayList();
    private LinkedList<Integer> linkedList = new LinkedList();

    @Setup(Level.Iteration)
    public void setUp(){
        for (int i = 0; i < 50000; i++) {
            arrayList.add(i);
            linkedList.add(i);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 5, time = 1)
    @Measurement(iterations = 5, time = 1)
    @Fork(5)
    public void testArrayListPrintByFor(Blackhole bh){
        int result = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            result = arrayList.get(i);
        }
        bh.consume(result);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 5, time = 1)
    @Measurement(iterations = 5, time = 1)
    @Fork(5)
    public void testArrayListPrintByStream(Blackhole bh){
        int result = arrayList.stream().mapToInt(x -> x.intValue()).max().getAsInt();
        bh.consume(result);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 5, time = 1)
    @Measurement(iterations = 5, time = 1)
    @Fork(5)
    public void testLinkedListPrintByFor(Blackhole bh){
        int result = 0;
        for (int i = 0; i < linkedList.size(); i++) {
            result = linkedList.get(i);
        }
        bh.consume(result);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 5, time = 1)
    @Measurement(iterations = 5, time = 1)
    @Fork(5)
    public void testLinkedListPrintByStream(Blackhole bh){
        int result = linkedList.stream().mapToInt(x -> x.intValue()).max().getAsInt();
        bh.consume(result);
    }

}