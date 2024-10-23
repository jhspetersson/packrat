package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

import static io.github.jhspetersson.packrat.TestUtils.isOrdered;
import static io.github.jhspetersson.packrat.TestUtils.isOrderedSequence;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("preview")
public class SamplingTest {
    @Test
    void emptyTest() {
        var before = Collections.emptyList();
        var after = before.stream().gather(Packrat.sample(100)).toList();

        assertTrue(after.isEmpty());
    }

    @Test
    void notEnoughTest() {
        var size = 10;
        var before = new ArrayList<Integer>();
        IntStream.range(0, size).forEach(before::add);

        assertTrue(isOrderedSequence(before));
        assertEquals(size, before.size());

        var after = before.stream().gather(Packrat.sample(100)).toList();

        assertTrue(isOrderedSequence(after));
        assertEquals(size, after.size());
    }

    @Test
    void exactTest() {
        var size = 100;
        var before = new ArrayList<Integer>();
        IntStream.range(0, size).forEach(before::add);

        assertTrue(isOrderedSequence(before));
        assertEquals(size, before.size());

        var after = before.stream().gather(Packrat.sample(100)).toList();

        assertTrue(isOrderedSequence(after));
        assertEquals(size, after.size());
    }

    @Test
    void normalTest() {
        var size = 100000;
        var before = new ArrayList<Integer>();
        IntStream.range(0, size).forEach(before::add);

        assertTrue(isOrderedSequence(before));
        assertEquals(size, before.size());

        var after = before.stream().gather(Packrat.sample(100)).toList();

        assertTrue(isOrdered(after));
        assertEquals(100, after.size());
    }
}
