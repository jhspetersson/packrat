package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SamplingMaxSpanBugTest {

    @Test
    void sampleShouldRejectMaxSpanLessThanOrEqualToN() {
        assertThrows(IllegalArgumentException.class, () -> Packrat.sample(10, 5));
    }

    @Test
    void sampleShouldRejectMaxSpanEqualToN() {
        assertThrows(IllegalArgumentException.class, () -> Packrat.sample(10, 10));
    }

    @Test
    void sampleShouldRejectMaxSpanZero() {
        assertThrows(IllegalArgumentException.class, () -> Packrat.sample(10, 0));
    }

    @Test
    void sampleShouldRejectMaxSpanNegative() {
        assertThrows(IllegalArgumentException.class, () -> Packrat.sample(10, -1));
    }
}
