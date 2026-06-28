package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MinMaxTest {
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
