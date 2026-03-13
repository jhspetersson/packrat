package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GathererReuseBugTest {

    @Test
    void increasingGathererShouldBeReusable() {
        var gatherer = Packrat.<Integer>increasing();

        var result1 = Stream.of(1, 2, 3).gather(gatherer).toList();
        assertEquals(List.of(1, 2, 3), result1);

        var result2 = Stream.of(1, 2, 3).gather(gatherer).toList();
        assertEquals(List.of(1, 2, 3), result2);
    }

    @Test
    void decreasingGathererShouldBeReusable() {
        var gatherer = Packrat.<Integer>decreasing();

        var result1 = Stream.of(3, 2, 1).gather(gatherer).toList();
        assertEquals(List.of(3, 2, 1), result1);

        var result2 = Stream.of(3, 2, 1).gather(gatherer).toList();
        assertEquals(List.of(3, 2, 1), result2);
    }

    @Test
    void increasingChunksGathererShouldBeReusable() {
        var gatherer = Packrat.<Integer>increasingChunks();

        var result1 = Stream.of(1, 2, 1, 2).gather(gatherer).toList();
        assertEquals(List.of(List.of(1, 2), List.of(1, 2)), result1);

        var result2 = Stream.of(1, 2, 1, 2).gather(gatherer).toList();
        assertEquals(List.of(List.of(1, 2), List.of(1, 2)), result2);
    }

    @Test
    void decreasingChunksGathererShouldBeReusable() {
        var gatherer = Packrat.<Integer>decreasingChunks();

        var result1 = Stream.of(2, 1, 2, 1).gather(gatherer).toList();
        assertEquals(List.of(List.of(2, 1), List.of(2, 1)), result1);

        var result2 = Stream.of(2, 1, 2, 1).gather(gatherer).toList();
        assertEquals(List.of(List.of(2, 1), List.of(2, 1)), result2);
    }

    @Test
    void equalChunksGathererShouldBeReusable() {
        var gatherer = Packrat.<Integer>equalChunks();

        var result1 = Stream.of(1, 1, 2, 2).gather(gatherer).toList();
        assertEquals(List.of(List.of(1, 1), List.of(2, 2)), result1);

        var result2 = Stream.of(1, 1, 2, 2).gather(gatherer).toList();
        assertEquals(List.of(List.of(1, 1), List.of(2, 2)), result2);
    }
}
