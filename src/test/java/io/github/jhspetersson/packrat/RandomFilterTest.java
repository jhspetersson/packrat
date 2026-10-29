package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.github.jhspetersson.packrat.TestUtils.isOrdered;
import static org.junit.jupiter.api.Assertions.*;

public class RandomFilterTest {
    @ParameterizedTest
    @ValueSource(doubles = {0.0, -0.0})
    void zeroProbabilityShouldShortCircuitUnboundedStream(double probability) {
        var consumed = new AtomicInteger();

        var result = Stream.generate(() -> 42)
                // Fail on overconsumption instead of hanging if short-circuiting regresses.
                .peek(_ -> assertEquals(1, consumed.incrementAndGet(), "Only one input should be consumed"))
                .gather(Packrat.randomFilter(probability))
                .toList();

        assertTrue(result.isEmpty());
        assertEquals(1, consumed.get());
    }

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

    @Test
    void zeroProbabilityPassesNoneTest() {
        int n = 10_000;
        var result = IntStream.range(0, n).boxed().gather(Packrat.randomFilter(0.0)).toList();
        assertTrue(result.isEmpty());
    }

    @Test
    void oneProbabilityPassesAllTest() {
        int n = 10_000;
        var result = IntStream.range(0, n).boxed().gather(Packrat.randomFilter(1.0)).toList();
        assertEquals(n, result.size());
        assertEquals(IntStream.range(0, n).boxed().toList(), result);
    }
}
