package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("preview")
public class IncreasingDecreasingTest {

    @Test
    void increasingTest() {
        var numbers = Stream.of(1, 2, 2, 5, 4, 2, 6, 9, 3, 11, 0, 1, 20);
        var result = numbers.gather(Packrat.increasing()).toList();

        assertEquals(List.of(1, 2, 5, 6, 9, 11, 20), result);
    }

    @Test
    void increasingOrEqualTest() {
        var numbers = Stream.of(1, 2, 2, 5, 4, 2, 6, 9, 3, 11, 0, 1, 20);
        var result = numbers.gather(Packrat.increasingOrEqual()).toList();

        assertEquals(List.of(1, 2, 2, 5, 6, 9, 11, 20), result);
    }

    @Test
    void decreasingTest() {
        var numbers = Stream.of(20, 17, 18, 15, 11, 11, 14, 9, 11, 11, 7, 7, 0);
        var result = numbers.gather(Packrat.decreasing()).toList();

        assertEquals(List.of(20, 17, 15, 11, 9, 7, 0), result);
    }

    @Test
    void decreasingOrEqualTest() {
        var numbers = Stream.of(20, 17, 18, 15, 11, 11, 14, 9, 11, 11, 7, 7, 0);
        var result = numbers.gather(Packrat.decreasingOrEqual()).toList();

        assertEquals(List.of(20, 17, 15, 11, 11, 9, 7, 7, 0), result);
    }
}
