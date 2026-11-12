package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RepeatGathererTest {
    @Test
    public void repeatZeroShouldNotBufferTheStream() {
        var result = org.junit.jupiter.api.Assertions.assertTimeoutPreemptively(java.time.Duration.ofSeconds(2), () ->
                Stream.iterate(0, i -> i + 1).gather(Packrat.repeat(0)).toList());

        assertTrue(result.isEmpty());
    }

    @Test
    public void repeatOnceShouldStreamLazily() {
        var result = org.junit.jupiter.api.Assertions.assertTimeoutPreemptively(java.time.Duration.ofSeconds(2), () ->
                Stream.iterate(0, i -> i + 1).gather(Packrat.repeat(1)).limit(3).toList());

        assertEquals(List.of(0, 1, 2), result);
    }

    @Test
    public void repeatTest() {
        var numbers = Stream.of(1, 2, 3);
        var result = numbers.gather(Packrat.repeat(3)).toList();
        assertEquals(List.of(1, 2, 3, 1, 2, 3, 1, 2, 3), result);
    }

    @Test
    public void repeatZeroTest() {
        var numbers = Stream.of(1, 2, 3);
        var result = numbers.gather(Packrat.repeat(0)).toList();
        assertTrue(result.isEmpty());
    }

    @Test
    public void repeatOnceTest() {
        var numbers = Stream.of(1, 2, 3);
        var result = numbers.gather(Packrat.repeat(1)).toList();
        assertEquals(List.of(1, 2, 3), result);
    }

    @Test
    public void repeatEmptyStreamTest() {
        var numbers = Stream.<Integer>empty();
        var result = numbers.gather(Packrat.repeat(5)).toList();
        assertTrue(result.isEmpty());
    }

    @Test
    public void repeatEmptyStreamWithMaxRepetitionsShouldTerminate() {
        var result = org.junit.jupiter.api.Assertions.assertTimeoutPreemptively(java.time.Duration.ofSeconds(2), () ->
                Stream.<Integer>empty().gather(Packrat.repeat(Long.MAX_VALUE)).toList());

        assertTrue(result.isEmpty());
    }

    @Test
    public void repeatShouldStreamFirstCopyLazily() {
        var consumed = new java.util.concurrent.atomic.AtomicInteger();
        var result = Stream.iterate(0, i -> i + 1).limit(100_000)
                .peek(_ -> consumed.incrementAndGet())
                .gather(Packrat.repeat(2))
                .limit(1)
                .toList();

        assertEquals(List.of(0), result);
        assertEquals(1, consumed.get());
    }

    @Test
    public void repeatShouldShortCircuitUnboundedStream() {
        var result = org.junit.jupiter.api.Assertions.assertTimeoutPreemptively(java.time.Duration.ofSeconds(2), () ->
                Stream.iterate(0, i -> i + 1).gather(Packrat.repeat(2)).limit(3).toList());

        assertEquals(List.of(0, 1, 2), result);
    }

    @Test
    public void repeatShouldStopWhileReplaying() {
        var result = Stream.of(1, 2, 3).gather(Packrat.repeat(3)).limit(5).toList();
        assertEquals(List.of(1, 2, 3, 1, 2), result);
    }

    @Test
    public void repeatParallelTest() {
        var result = Stream.of(1, 2, 3).parallel().gather(Packrat.repeat(2)).toList();
        assertEquals(List.of(1, 2, 3, 1, 2, 3), result);
    }
}
