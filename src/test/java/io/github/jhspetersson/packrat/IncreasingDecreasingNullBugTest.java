package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IncreasingDecreasingNullBugTest {
    @Test
    void decreasingWithNullFirstElement() {
        var result = Stream.of((Integer) null, 5, 3, 7)
                .gather(Packrat.decreasing(Comparator.nullsFirst(Comparator.naturalOrder())))
                .toList();
        assertEquals(Collections.singletonList(null), result);
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
}
