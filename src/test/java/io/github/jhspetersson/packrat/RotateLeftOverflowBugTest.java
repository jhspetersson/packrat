package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RotateLeftOverflowBugTest {
    @Test
    void rotateLeftByMoreThanStreamSize() {
        var result = Stream.of(1, 2, 3).gather(Packrat.rotate(-4)).toList();
        assertEquals(List.of(2, 3, 1), result);
    }

    @Test
    void rotateLeftByStreamSizePlusTwo() {
        var result = Stream.of(1, 2, 3, 4, 5).gather(Packrat.rotate(-7)).toList();
        assertEquals(List.of(3, 4, 5, 1, 2), result);
    }
}
