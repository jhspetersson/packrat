package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IncreasingDecreasingChunksTest {
    @Test
    void increasingChunksTest() {
        var numbers = Stream.of(1, 2, 2, 5, 4, 2, 6, 9, 3, 11, 0, 1, 20);
        var result = numbers.gather(Packrat.increasingChunks()).toList();

        assertEquals(List.of(List.of(1, 2), List.of(2, 5), List.of(4), List.of(2, 6, 9), List.of(3, 11), List.of(0, 1, 20)), result);
    }

    @Test
    void increasingOrEqualChunksTest() {
        var numbers = Stream.of(1, 2, 2, 5, 4, 2, 6, 9, 3, 11, 0, 1, 20);
        var result = numbers.gather(Packrat.increasingOrEqualChunks()).toList();

        assertEquals(List.of(List.of(1, 2, 2, 5), List.of(4), List.of(2, 6, 9), List.of(3, 11), List.of(0, 1, 20)), result);
    }

    @Test
    void decreasingChunksTest() {
        var numbers = Stream.of(20, 17, 18, 15, 11, 11, 14, 9, 11, 11, 7, 7, 0);
        var result = numbers.gather(Packrat.decreasingChunks()).toList();

        assertEquals(List.of(List.of(20, 17), List.of(18, 15, 11), List.of(11), List.of(14, 9), List.of(11), List.of(11, 7), List.of(7, 0)), result);
    }

    @Test
    void decreasingOrEqualChunksTest() {
        var numbers = Stream.of(20, 17, 18, 15, 11, 11, 14, 9, 11, 11, 7, 7, 0);
        var result = numbers.gather(Packrat.decreasingOrEqualChunks()).toList();

        assertEquals(List.of(List.of(20, 17), List.of(18, 15, 11, 11), List.of(14, 9), List.of(11, 11, 7, 7, 0)), result);
    }

    @Test
    void decreasingChunksWithNullFirstElement() {
        var result = Stream.of((Integer) null, 5, 3, 7)
                .gather(Packrat.decreasingChunks(Comparator.nullsFirst(Comparator.naturalOrder())))
                .toList();
        assertEquals(List.of(Arrays.asList((Integer) null), List.of(5, 3), List.of(7)), result);
    }

    @Test
    void increasingChunksWithNullFirstElement() {
        var nullComparator = Comparator.<Integer>nullsFirst(Comparator.naturalOrder());
        var result = Stream.of((Integer) null, 3, 1, 5)
                .gather(Packrat.increasingChunks(nullComparator))
                .toList();
        assertEquals(List.of(Arrays.asList((Integer) null, 3), List.of(1, 5)), result);
    }

    @Test
    void increasingChunksGathererShouldBeReusable() {
        var gatherer = Packrat.<Integer>increasingChunks();

        var result1 = Stream.of(1, 2, 1, 2).gather(gatherer).toList();
        assertEquals(List.of(List.of(1, 2), List.of(1, 2)), result1);

        var result2 = Stream.of(1, 2, 1, 2).gather(gatherer).toList();
        assertEquals(List.of(List.of(1, 2), List.of(1, 2)), result2);
    }

    @Test
    void decreasingChunksGathererShouldBeReusable() {
        var gatherer = Packrat.<Integer>decreasingChunks();

        var result1 = Stream.of(2, 1, 2, 1).gather(gatherer).toList();
        assertEquals(List.of(List.of(2, 1), List.of(2, 1)), result1);

        var result2 = Stream.of(2, 1, 2, 1).gather(gatherer).toList();
        assertEquals(List.of(List.of(2, 1), List.of(2, 1)), result2);
    }
}
