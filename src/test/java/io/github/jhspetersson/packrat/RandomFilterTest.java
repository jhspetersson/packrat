package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.github.jhspetersson.packrat.TestUtils.isOrdered;
import static org.junit.jupiter.api.Assertions.*;

public class RandomFilterTest {
    @Test
    void emptyStreamTest() {
        var result = Stream.<Integer>of().gather(Packrat.randomFilter(0.5)).toList();
        assertTrue(result.isEmpty());
    }

    @Test
    void invalidProbabilityThrowsTest() {
        assertThrows(IllegalArgumentException.class, () -> Packrat.randomFilter(-0.1));
        assertThrows(IllegalArgumentException.class, () -> Packrat.randomFilter(1.1));
        assertThrows(IllegalArgumentException.class, () -> Packrat.randomFilter(Double.NaN));
    }

    @Test
    void resultIsOrderedTest() {
        int n = 10_000;
        var result = IntStream.range(0, n).boxed().gather(Packrat.randomFilter(0.5)).toList();
        assertTrue(isOrdered(result));
    }
}
