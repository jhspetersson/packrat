package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RotateOverflowBugTest {

    @Test
    void rotateShouldHandleMinIntDistance() {
        var result = Stream.of(1, 2, 3).gather(Packrat.rotate(Integer.MIN_VALUE)).toList();
        assertEquals(3, result.size());
    }

    @Test
    void rotateMinIntShouldProduceCorrectResult() {
        var result = Stream.of("a", "b", "c", "d").gather(Packrat.rotate(Integer.MIN_VALUE)).toList();
        assertEquals(List.of("a", "b", "c", "d"), result);
    }
}
