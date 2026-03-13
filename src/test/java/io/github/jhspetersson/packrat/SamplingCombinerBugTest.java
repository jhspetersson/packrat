package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SamplingCombinerBugTest {

    @Test
    void sampleParallelShouldReturnExactlyNElements() {
        var result = IntStream.range(0, 10_000).boxed()
                .parallel()
                .gather(Packrat.sample(5))
                .toList();
        assertEquals(5, result.size());
    }

    @Test
    void sampleParallelResultsShouldBeWithinRange() {
        var result = IntStream.range(0, 10_000).boxed()
                .parallel()
                .gather(Packrat.sample(10))
                .toList();
        assertEquals(10, result.size());
        for (var value : result) {
            assertTrue(value >= 0 && value < 10_000);
        }
    }
}
