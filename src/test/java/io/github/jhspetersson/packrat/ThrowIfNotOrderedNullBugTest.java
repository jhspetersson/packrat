package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ThrowIfNotOrderedNullBugTest {
    @Test
    void nullMappedFirstElementSkipsDecreasingValidation() {
        assertThrows(Exception.class, () ->
                Stream.of(1, 2)
                        .gather(Packrat.throwIfNotDecreasingBy(
                                i -> i == 1 ? null : i,
                                () -> new IllegalStateException("not decreasing")))
                        .toList()
        );
    }

    @Test
    void nullMappedFirstElementSkipsIncreasingValidation() {
        assertThrows(Exception.class, () ->
                Stream.of(5, 3)
                        .gather(Packrat.throwIfNotIncreasingBy(
                                i -> i == 5 ? null : i,
                                () -> new IllegalStateException("not increasing")))
                        .toList()
        );
    }
}
