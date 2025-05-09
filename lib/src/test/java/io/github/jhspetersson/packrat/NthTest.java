package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NthTest {
    @Test
    public void nthTest() {
        var numbers = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        var result = numbers.gather(Packrat.nth(3)).toList();
        assertEquals(List.of(3, 6, 9), result);
    }

    @Test
    public void nthWithLargeStreamTest() {
        var numbers = IntStream.rangeClosed(1, 100).boxed();
        var result = numbers.gather(Packrat.nth(10)).toList();
        assertEquals(List.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), result);
    }

    @Test
    public void nthWithEmptyStreamTest() {
        var numbers = Stream.of();
        var result = numbers.gather(Packrat.nth(5)).toList();
        assertTrue(result.isEmpty());
    }

    @Test
    public void nthWithSingleElementTest() {
        var numbers = Stream.of(42);
        var result = numbers.gather(Packrat.nth(1)).toList();
        assertEquals(List.of(42), result);
    }

    @Test
    public void nthWithFewerElementsThanNTest() {
        var numbers = Stream.of(1, 2, 3, 4);
        var result = numbers.gather(Packrat.nth(5)).toList();
        assertEquals(List.of(), result);
    }

    @Test
    public void nthWithExactlyNElementsTest() {
        var numbers = Stream.of(1, 2, 3, 4, 5);
        var result = numbers.gather(Packrat.nth(5)).toList();
        assertEquals(List.of(5), result);
    }

    @Test
    public void nthWithStringTest() {
        var strings = Stream.of("apple", "banana", "cherry", "date", "elderberry", "fig", "grape");
        var result = strings.gather(Packrat.nth(2)).toList();
        assertEquals(List.of("banana", "date", "fig"), result);
    }
}
