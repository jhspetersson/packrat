package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilterWithIndexTest {
    @Test
    public void filterByIndexTest() {
        var numbers = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        var result = numbers.gather(Packrat.filterWithIndex((index, _) -> index % 2 == 0)).toList();
        assertEquals(List.of(1, 3, 5, 7, 9), result);
    }

    @Test
    public void filterByElementTest() {
        var numbers = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        var result = numbers.gather(Packrat.filterWithIndex((_, element) -> element % 2 == 0)).toList();
        assertEquals(List.of(2, 4, 6, 8, 10), result);
    }

    @Test
    public void filterByIndexAndElementTest() {
        var numbers = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        var result = numbers.gather(Packrat.filterWithIndex((index, element) -> index % 2 == 1 && element % 2 == 0)).toList();
        assertEquals(List.of(2, 4, 6, 8, 10), result);
    }

    @Test
    public void filterWithStartIndexTest() {
        var numbers = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        var result = numbers.gather(Packrat.filterWithIndex((index, element) -> index % 2 == 0, 1)).toList();
        assertEquals(List.of(2, 4, 6, 8, 10), result);
    }

    @Test
    public void removeWithIndexTest() {
        var numbers = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        var result = numbers.gather(Packrat.removeWithIndex((index, element) -> index % 2 == 0)).toList();
        assertEquals(List.of(2, 4, 6, 8, 10), result);
    }

    @Test
    public void emptyStreamTest() {
        var numbers = Stream.<Integer>of();
        var result = numbers.gather(Packrat.filterWithIndex((index, element) -> index % 2 == 0)).toList();
        assertTrue(result.isEmpty());
    }

    @Test
    public void largeStreamTest() {
        var numbers = IntStream.rangeClosed(1, 100).boxed();
        var result = numbers.gather(Packrat.filterWithIndex((index, element) -> index % 10 == 0)).toList();
        assertEquals(List.of(1, 11, 21, 31, 41, 51, 61, 71, 81, 91), result);
    }

    @Test
    public void stringStreamTest() {
        var strings = Stream.of("apple", "banana", "cherry", "date", "elderberry");
        var result = strings.gather(Packrat.filterWithIndex((index, element) -> element.length() > index + 1)).toList();
        assertEquals(List.of("apple", "banana", "cherry", "elderberry"), result);
    }
}
