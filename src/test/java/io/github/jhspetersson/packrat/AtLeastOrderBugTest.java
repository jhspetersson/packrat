package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AtLeastOrderBugTest {

    @Test
    void atLeastShouldPreserveOriginalOrder() {
        var result = Stream.of("a", "b", "a", "b")
                .gather(Packrat.atLeast(2))
                .toList();
        assertEquals(List.of("a", "b", "a", "b"), result);
    }

    @Test
    void atLeastShouldPreserveOrderWithThreeGroups() {
        var result = Stream.of("x", "y", "z", "x", "y", "z")
                .gather(Packrat.atLeast(2))
                .toList();
        assertEquals(List.of("x", "y", "z", "x", "y", "z"), result);
    }
}
