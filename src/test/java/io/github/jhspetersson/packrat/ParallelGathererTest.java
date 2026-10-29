package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParallelGathererTest {
    @ParameterizedTest(name = "{0}")
    @MethodSource("statelessGatherers")
    <T> void statelessGatherersSupportParallelExecution(String name, List<T> input,
                                                       Gatherer<T, ?, T> gatherer, List<T> expected) {
        assertNotSame(Gatherer.defaultCombiner(), gatherer.combiner(),
                "A stateless gatherer must not force sequential execution");

        assertEquals(expected, input.parallelStream().gather(gatherer).toList());
        assertEquals(expected.stream().limit(7).toList(),
                input.parallelStream().gather(gatherer).limit(7).toList());
    }

    static Stream<Arguments> statelessGatherers() {
        var input = IntStream.range(0, 1_000).boxed().toList();
        var even = input.stream().filter(i -> i % 2 == 0).toList();
        var odd = input.stream().filter(i -> i % 2 != 0).toList();
        var entries = input.stream().map(i -> Map.entry(i, -i)).toList();

        return Stream.of(
                Arguments.of("filterBy", input, Packrat.<Integer, Integer>filterBy(i -> i % 2, 0), even),
                Arguments.of("removeBy", input, Packrat.<Integer, Integer>removeBy(i -> i % 2, 0), odd),
                Arguments.of("filterEntries", entries,
                        Packrat.<Integer, Integer>filterEntries((k, v) -> k % 2 == 0),
                        entries.stream().filter(e -> e.getKey() % 2 == 0).toList()),
                Arguments.of("removeEntries", entries,
                        Packrat.<Integer, Integer>removeEntries((k, v) -> k % 2 == 0),
                        entries.stream().filter(e -> e.getKey() % 2 != 0).toList()),
                Arguments.of("flatMapIf", input,
                        Packrat.<Integer>flatMapIf(i -> Stream.of(i, -i), i -> i % 2 == 0),
                        input.stream().flatMap(i -> i % 2 == 0 ? Stream.of(i, -i) : Stream.of(i)).toList()),
                Arguments.of("identity", input, Packrat.<Integer>identity(), input),
                Arguments.of("nCopies", input, Packrat.<Integer>nCopies(3),
                        input.stream().flatMap(i -> Stream.of(i, i, i)).toList()),
                Arguments.of("randomFilter(0)", input, Packrat.<Integer>randomFilter(0), List.of()),
                Arguments.of("randomFilter(1)", input, Packrat.<Integer>randomFilter(1), input)
        );
    }

    @Test
    void filteringUsesMultipleWorkerThreads() throws Exception {
        var threads = ConcurrentHashMap.<Long>newKeySet();
        var started = new CountDownLatch(2);
        var gatherer = Packrat.<Integer, Integer>filterBy(i -> {
            if (threads.add(Thread.currentThread().threadId())) {
                started.countDown();
                try {
                    assertTrue(started.await(5, TimeUnit.SECONDS), "Expected two concurrent filter workers");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new AssertionError(e);
                }
            }
            return i % 2;
        }, 0);

        try (var pool = new ForkJoinPool(4)) {
            var result = pool.submit(() -> IntStream.range(0, 10_000).parallel().boxed()
                    .gather(gatherer).toList()).get(10, TimeUnit.SECONDS);

            assertEquals(IntStream.range(0, 10_000).filter(i -> i % 2 == 0).boxed().toList(), result);
            assertTrue(threads.size() > 1, "Filtering should run on multiple worker threads");
        }
    }
}
