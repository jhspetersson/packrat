package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MinMaxNullElementBugTest {
    @Test
    void maxByReturnsNullElement() {
        var result = Stream.<String>of(null, "b", "a")
                .gather(Packrat.maxBy(s -> s == null ? "zzz" : s))
                .toList();
        assertEquals(1, result.size());
        assertNull(result.getFirst());
    }

    @Test
    void minByReturnsNullElement() {
        var result = Stream.<String>of("b", null, "a")
                .gather(Packrat.minBy(s -> s == null ? "" : s))
                .toList();
        assertEquals(1, result.size());
        assertNull(result.getFirst());
    }

    @Test
    void singleNullElement() {
        var result = Stream.<String>of((String) null)
                .gather(Packrat.maxBy(s -> s == null ? "x" : s))
                .toList();
        assertEquals(1, result.size());
        assertNull(result.getFirst());
    }
}
