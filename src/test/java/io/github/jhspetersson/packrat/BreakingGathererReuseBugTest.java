package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BreakingGathererReuseBugTest {

    @Test
    void wordsSameInstanceInPipelineShouldNotCorrupt() {
        var g = Packrat.words();
        var result = Stream.of("hello world")
                .gather(g)
                .gather(g)
                .toList();
        assertEquals(List.of("hello", "world"), result);
    }

    @Test
    void wordsSameInstanceUsedSequentiallyShouldProduceSameResults() {
        var g = Packrat.words();
        var result1 = Stream.of("hello world").gather(g).toList();
        var result2 = Stream.of("foo bar baz").gather(g).toList();
        assertEquals(List.of("hello", "world"), result1);
        assertEquals(List.of("foo", "bar", "baz"), result2);
    }

    @Test
    void sentencesSameInstanceInPipelineShouldNotCorrupt() {
        var g = Packrat.sentences();
        var result = Stream.of("Hello world. Goodbye world.")
                .gather(g)
                .gather(g)
                .toList();
        assertEquals(List.of("Hello world. ", "Goodbye world."), result);
    }
}
