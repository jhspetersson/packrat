package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DropLastNBugTest {

    @Test
    void dropLastWithStreamLargerThanInternalBuffer() {
        var result = IntStream.range(0, 20).boxed()
                .gather(Packrat.dropLast(3))
                .toList();

        assertEquals(17, result.size());
        assertEquals(0, result.getFirst());
        assertEquals(16, result.getLast());
    }

    @Test
    void dropLastWithStreamMuchLargerThanBuffer() {
        var result = IntStream.range(0, 100).boxed()
                .gather(Packrat.dropLast(5))
                .toList();

        assertEquals(95, result.size());
        assertEquals(0, result.getFirst());
        assertEquals(94, result.getLast());
    }

    @Test
    void dropLastLargeNWithLargerStream() {
        var result = IntStream.range(0, 50).boxed()
                .gather(Packrat.dropLast(20))
                .toList();

        assertEquals(30, result.size());
        assertEquals(0, result.getFirst());
        assertEquals(29, result.getLast());
    }
}
