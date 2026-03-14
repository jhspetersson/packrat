package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EqualChunksNullBugTest {
    @Test
    void equalChunksWithNullElements() {
        var result = Stream.<Integer>of(null, null, 1, 1)
                .gather(Packrat.equalChunksBy(Function.identity()))
                .toList();
        assertEquals(List.of(Arrays.asList(null, null), List.of(1, 1)), result);
    }

    @Test
    void equalChunksWithNullInLastChunk() {
        var result = Stream.<Integer>of(1, 1, null, null)
                .gather(Packrat.equalChunksBy(Function.identity()))
                .toList();
        assertEquals(List.of(List.of(1, 1), Arrays.asList(null, null)), result);
    }

    @Test
    void equalChunksSingleNullChunk() {
        var result = Stream.<Integer>of((Integer) null)
                .gather(Packrat.equalChunksBy(Function.identity()))
                .toList();
        assertEquals(List.of(Arrays.asList((Integer) null)), result);
    }
}
