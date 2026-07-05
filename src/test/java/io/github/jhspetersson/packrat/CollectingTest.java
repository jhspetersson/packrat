package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollectingTest {
    @Test
    void collectingTest() {
        var numbers = Stream.of(1, 2, 3, 4, 5);
        var result = numbers.gather(Packrat.asGatherer(Collectors.toList())).toList();
        assertEquals(List.of(List.of(1, 2, 3, 4, 5)), result);
    }

    @Test
    void accumulatorShouldBeFetchedOncePerEvaluation() {
        var fetches = new AtomicInteger();
        var collector = new Collector<Integer, List<Integer>, List<Integer>>() {
            @Override
            public Supplier<List<Integer>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<Integer>, Integer> accumulator() {
                fetches.incrementAndGet();
                return List::add;
            }

            @Override
            public BinaryOperator<List<Integer>> combiner() {
                return (a, b) -> {
                    a.addAll(b);
                    return a;
                };
            }

            @Override
            public Function<List<Integer>, List<Integer>> finisher() {
                return Function.identity();
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Set.of();
            }
        };

        var input = new ArrayList<Integer>();
        for (var i = 0; i < 100; i++) {
            input.add(i);
        }

        var result = input.stream().gather(Packrat.asGatherer(collector)).toList();

        assertEquals(List.of(input), result);
        // the JDK may call integrator() a few times per evaluation, but the fetch count must not scale with element count
        assertTrue(fetches.get() <= 4, "accumulator() fetched " + fetches.get() + " times");
    }
}
