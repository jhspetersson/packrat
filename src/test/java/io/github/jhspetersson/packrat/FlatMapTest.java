package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FlatMapTest {
    @Test
    public void flatMapIfTest() {
        var strings = Stream.of("A", "B", "CDE", "FG", "H", "IJ", "KL", "M", "NOP");
        var result = strings.gather(Packrat.flatMapIf(s -> Arrays.stream(s.split("")), s -> s.length() >= 3)).toList();
        assertEquals(List.of("A", "B", "C", "D", "E", "FG", "H", "IJ", "KL", "M", "N", "O", "P"), result);
    }

    @Test
    public void emptyStreamTest() {
        var emptyStream = Stream.<String>empty();
        var result = emptyStream.gather(Packrat.flatMapIf(s -> Arrays.stream(s.split("")), s -> s.length() > 1)).toList();
        assertTrue(result.isEmpty(), "Result of empty stream should be empty");
    }

    @Test
    public void singleElementTest() {
        var singleElement = Stream.of("ABC");
        var result = singleElement.gather(Packrat.flatMapIf(s -> Arrays.stream(s.split("")), s -> s.length() > 1)).toList();
        assertEquals(List.of("A", "B", "C"), result, "Single multi-character element should be flattened");

        var singleCharElement = Stream.of("X");
        var resultSingleChar = singleCharElement.gather(Packrat.flatMapIf(s -> Arrays.stream(s.split("")), s -> s.length() > 1)).toList();
        assertEquals(List.of("X"), resultSingleChar, "Single character element should remain unchanged");
    }

    @Test
    public void alwaysTruePredicateTest() {
        var strings = Stream.of("AB", "CD", "EF");
        var result = strings.gather(Packrat.flatMapIf(s -> Arrays.stream(s.split("")), _ -> true)).toList();
        assertEquals(List.of("A", "B", "C", "D", "E", "F"), result, "With always true predicate, all elements should be flattened");
    }

    @Test
    public void alwaysFalsePredicateTest() {
        var strings = Stream.of("ABC", "DEF", "GHI");
        var result = strings.gather(Packrat.flatMapIf(s -> Arrays.stream(s.split("")), _ -> false)).toList();
        assertEquals(List.of("ABC", "DEF", "GHI"), result, "With always false predicate, no elements should be flattened");
    }

    @Test
    public void differentMapperTest() {
        var numbers = Stream.of(1, 2, 3, 4, 5);
        var result = numbers.gather(Packrat.flatMapIf(
            n -> Stream.of(n * 10, n * 100), 
            n -> n % 2 == 0
        )).toList();
        assertEquals(List.of(1, 20, 200, 3, 40, 400, 5), result, "Even numbers should be mapped to multiples");
    }

    @Test
    public void emptyResultTest() {
        var strings = Stream.of("A", "B", "C");

        var result = strings.gather(Packrat.flatMapIf(
                _ -> Stream.empty(),
            s -> s.equals("B")
        )).toList();

        assertEquals(List.of("A", "C"), result, 
            "When mapper returns empty stream, the element should be effectively removed");
    }

    @Test
    public void multipleElementsMapperTest() {
        var strings = Stream.of("A", "BB", "CCC");

        var result = strings.gather(Packrat.flatMapIf(
            s -> Stream.of(s, s.repeat(2)),
            s -> s.length() > 1
        )).toList();

        assertEquals(List.of("A", "BB", "BBBB", "CCC", "CCCCCC"), result,
            "Elements matching predicate should be duplicated with the mapper");
    }

    @Test
    public void nullChecksTest() {
        assertThrows(NullPointerException.class, () -> Packrat.flatMapIf(null, _ -> true), "Null mapper should throw NullPointerException");
        assertThrows(NullPointerException.class, () -> Packrat.flatMapIf(_ -> Stream.empty(), null), "Null predicate should throw NullPointerException");
    }
}
