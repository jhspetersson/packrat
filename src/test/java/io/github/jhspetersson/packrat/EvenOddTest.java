package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EvenOddTest {

    @Test
    void evenTest() {
        var result = List.of("a", "b", "c", "d", "e").stream().gather(Packrat.even()).toList();
        assertEquals(List.of("a", "c", "e"), result);
    }

    @Test
    void oddTest() {
        var result = List.of("a", "b", "c", "d", "e").stream().gather(Packrat.odd()).toList();
        assertEquals(List.of("b", "d"), result);
    }

    @Test
    void evenNumbersTest() {
        var result = IntStream.range(0, 10).boxed().gather(Packrat.even()).toList();
        assertEquals(List.of(0, 2, 4, 6, 8), result);
    }

    @Test
    void oddNumbersTest() {
        var result = IntStream.range(0, 10).boxed().gather(Packrat.odd()).toList();
        assertEquals(List.of(1, 3, 5, 7, 9), result);
    }

    @Test
    void evenEmptyTest() {
        var result = Stream.of().gather(Packrat.even()).toList();
        assertTrue(result.isEmpty());
    }

    @Test
    void oddEmptyTest() {
        var result = Stream.of().gather(Packrat.odd()).toList();
        assertTrue(result.isEmpty());
    }

    @Test
    void evenSingleElementTest() {
        var result = Stream.of(42).gather(Packrat.even()).toList();
        assertEquals(List.of(42), result);
    }

    @Test
    void oddSingleElementTest() {
        var result = Stream.of(42).gather(Packrat.odd()).toList();
        assertTrue(result.isEmpty());
    }

    @Test
    void evenTwoElementsTest() {
        var result = List.of(1, 2).stream().gather(Packrat.even()).toList();
        assertEquals(List.of(1), result);
    }

    @Test
    void oddTwoElementsTest() {
        var result = List.of(1, 2).stream().gather(Packrat.odd()).toList();
        assertEquals(List.of(2), result);
    }
}
