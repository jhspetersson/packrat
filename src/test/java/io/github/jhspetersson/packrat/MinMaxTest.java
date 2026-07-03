package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MinMaxTest {
    @Test
    void combinerShouldPickWinner() {
        var gatherer = new MinMaxGatherer<Integer, Integer>(Function.identity(), Comparator.naturalOrder(), cmp -> cmp > 0);
        var integrator = gatherer.integrator();
        Gatherer.Downstream<Integer> downstream = _ -> true;

        var left = gatherer.initializer().get();
        var right = gatherer.initializer().get();
        integrator.integrate(left, 5, downstream);
        integrator.integrate(right, 7, downstream);

        var combined = gatherer.combiner().apply(left, right);

        assertEquals(7, combined.element);
    }

    @Test
    void maxByParallelTest() {
        var result = IntStream.range(0, 100_000).boxed()
                .parallel()
                .gather(Packrat.maxBy(Function.identity()))
                .toList();

        assertEquals(List.of(99_999), result);
    }

    @Test
    void emptyTest() {
        var check = Stream.empty().gather(Packrat.minBy(element -> Long.parseLong(element.toString()))).toList();

        assertTrue(check.isEmpty());
    }

    @Test
    void minTest() {
        var check = Stream.of("10", "2", "1", "-12", "22", "35", "66", "77", "123", "4", "7", "29")
                        .gather(Packrat.minBy(Long::parseLong))
                        .toList();

        assertEquals(1, check.size());
        assertEquals("-12", check.getFirst());
    }

    @Test
    void maxTest() {
        var check = Stream.of("10", "2", "1", "-12", "22", "35", "66", "77", "123", "4", "7", "29")
                .gather(Packrat.maxBy(Long::parseLong))
                .toList();

        assertEquals(1, check.size());
        assertEquals("123", check.getFirst());
    }

    @Test
    void maxByReturnsNullElement() {
        var result = Stream.of(null, "b", "a")
                .gather(Packrat.maxBy(s -> s == null ? "zzz" : s))
                .toList();
        assertEquals(1, result.size());
        assertNull(result.getFirst());
    }

    @Test
    void minByReturnsNullElement() {
        var result = Stream.of("b", null, "a")
                .gather(Packrat.minBy(s -> s == null ? "" : s))
                .toList();
        assertEquals(1, result.size());
        assertNull(result.getFirst());
    }

    @Test
    void singleNullElement() {
        var result = Stream.of((String) null)
                .gather(Packrat.maxBy(s -> s == null ? "x" : s))
                .toList();
        assertEquals(1, result.size());
        assertNull(result.getFirst());
    }

    @Test
    void minByWithNullsFirstComparatorReturnsNull() {
        var result = Stream.of(null, "a")
                .gather(Packrat.minBy(s -> s, Comparator.<String>nullsFirst(Comparator.naturalOrder())))
                .toList();
        assertEquals(1, result.size());
        assertNull(result.getFirst());
    }

    @Test
    void maxByWithNullsLastComparatorReturnsNull() {
        var result = Stream.of("a", null)
                .gather(Packrat.maxBy(s -> s, Comparator.<String>nullsLast(Comparator.naturalOrder())))
                .toList();
        assertEquals(1, result.size());
        assertNull(result.getFirst());
    }

    @Test
    void minByWithNullMappedValueNotFirst() {
        var result = Stream.of("a", null, "b")
                .gather(Packrat.minBy(s -> s, Comparator.<String>nullsFirst(Comparator.naturalOrder())))
                .toList();
        assertEquals(1, result.size());
        assertNull(result.getFirst());
    }
}
