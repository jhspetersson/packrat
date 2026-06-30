package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MapWhileUntilTest {
    @Test
    public void mapWhileTest() {
        var mapped = IntStream.rangeClosed(1, 10).boxed().gather(Packrat.mapWhile(n -> n * 10, n -> n <= 3)).toList();
        assertEquals(List.of(10, 20, 30, 4, 5, 6, 7, 8, 9, 10), mapped);
    }

    @Test
    public void mapUntilTest() {
        var mapped = IntStream.rangeClosed(1, 10).boxed().gather(Packrat.mapUntil(n -> n * 10, n -> n == 6)).toList();
        assertEquals(List.of(10, 20, 30, 40, 50, 6, 7, 8, 9, 10), mapped);
    }

    @Test
    public void mapWhileNullPredicateThrows() {
        assertThrows(NullPointerException.class, () -> Packrat.mapWhile(n -> (Integer) n * 10, null));
    }

    @Test
    public void mapUntilNullPredicateThrows() {
        assertThrows(NullPointerException.class, () -> Packrat.mapUntil(n -> (Integer) n * 10, null));
    }

    @Test
    public void mapWhileNullMapperThrows() {
        assertThrows(NullPointerException.class, () -> Packrat.<Integer>mapWhile(null, n -> n <= 3));
    }

    @Test
    public void mapUntilNullMapperThrows() {
        assertThrows(NullPointerException.class, () -> Packrat.<Integer>mapUntil(null, n -> n == 6));
    }
}
