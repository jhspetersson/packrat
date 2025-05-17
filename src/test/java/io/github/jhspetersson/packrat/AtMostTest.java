package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AtMostTest {
    @Test
    public void atMostTest() {
        var numbers = Stream.of(1, 2, 3, 3, 3, 4, 5, 5, 6, 7, 8, 8, 8, 8, 9, 10);
        var result = numbers.gather(Packrat.atMost(2)).toList();
        assertEquals(List.of(1, 2, 4, 5, 5, 6, 7, 9, 10), result);
    }

    @Test
    public void atMostUnorderedTest() {
        var numbers = Stream.of(1, 10, 3, 2, 3, 8, 4, 9, 5, 6, 7, 8, 3, 8, 8, 5);
        var result = numbers.gather(Packrat.atMost(2)).toList();
        var expected = List.of(1, 10, 2, 4, 5, 5, 6, 7, 9);
        assertTrue(result.size() == expected.size() && result.containsAll(expected) && expected.containsAll(result));
    }

    @Test
    public void atMostEmptyTest() {
        var numbers = Stream.of(1, 1, 1, 2, 2, 2, 3, 3, 3);
        var result = numbers.gather(Packrat.atMost(0)).toList();
        assertTrue(result.isEmpty());
    }

    @Test
    public void atMostEmptySourceTest() {
        var numbers = Stream.of();
        var result = numbers.gather(Packrat.atMost(10)).toList();
        assertTrue(result.isEmpty());
    }

    @Test
    public void atMostSingleValueTest() {
        var numbers = Stream.of(1, 1, 1);
        var result = numbers.gather(Packrat.atMost(3)).toList();
        assertEquals(List.of(1, 1, 1), result);
    }

    @Test
    public void atMostAllValuesTest() {
        var numbers = Stream.of(1, 2, 3, 4, 5);
        var result = numbers.gather(Packrat.atMost(1)).toList();
        assertEquals(List.of(1, 2, 3, 4, 5), result);
    }

    @Test
    public void atMostByTest() {
        var strings = Stream.of("apple", "banana", "cherry", "date", "elderberry", "fig", "grape");
        var result = strings.gather(Packrat.atMostBy(1, String::length)).collect(Collectors.toSet());
        // "date", "fig", and "elderberry" have unique lengths (4, 3, and 10 characters)
        var expected = Set.of("date", "elderberry", "fig");
        assertEquals(result, expected);
    }
}
