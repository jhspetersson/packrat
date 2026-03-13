package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AtMostOrderBugTest {

    @Test
    void atMostShouldPreserveOriginalOrder() {
        var result = Stream.of("a", "b", "a", "b")
                .gather(Packrat.atMost(2))
                .toList();
        assertEquals(List.of("a", "b", "a", "b"), result);
    }

    @Test
    void atMostShouldPreserveOrderWithUniqueElements() {
        var result = Stream.of("cherry", "apple", "banana", "date")
                .gather(Packrat.atMost(1))
                .toList();
        assertEquals(List.of("cherry", "apple", "banana", "date"), result);
    }
}
