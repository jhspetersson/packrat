package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NCopiesTest {
    @Test
    public void nCopiesTest() {
        var sum = IntStream.of(5).boxed().gather(Packrat.nCopies(20)).reduce(Integer::sum).orElseThrow();
        assertEquals(100, sum);
    }

    @Test
    public void nCopiesZeroShouldNotConsumeTheStream() {
        var result = assertTimeoutPreemptively(java.time.Duration.ofSeconds(2), () ->
                Stream.iterate(0, i -> i + 1).gather(Packrat.nCopies(0)).toList());

        assertTrue(result.isEmpty());
    }

    @Test
    public void nCopiesOneShouldStreamLazily() {
        var result = assertTimeoutPreemptively(java.time.Duration.ofSeconds(2), () ->
                Stream.iterate(0, i -> i + 1).gather(Packrat.nCopies(1)).limit(3).toList());

        assertEquals(List.of(0, 1, 2), result);
    }
}
