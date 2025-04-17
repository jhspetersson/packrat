package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static io.github.jhspetersson.packrat.TestUtils.isOrderedSequence;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LastNTest {
    @Test
    public void lastTest() {
        var before = new ArrayList<Integer>();
        IntStream.range(0, 100).forEach(before::add);

        assertTrue(isOrderedSequence(before));

        var after = before.stream().gather(Packrat.last()).toList();

        assertEquals(List.of(99), after);
    }

    @Test
    public void lastNTest() {
        var before = new ArrayList<Integer>();
        IntStream.range(0, 100).forEach(before::add);

        assertTrue(isOrderedSequence(before));

        var after = before.stream().gather(Packrat.last(10)).toList();

        assertEquals(List.of(90, 91, 92, 93, 94, 95, 96, 97, 98, 99), after);
    }

    @Test
    public void lastNUniqueTest() {
        var integers = List.of(1, 2, 3, 4, 5, 4, 1, 1, 1, 2, 2, 6).stream().gather(Packrat.lastUnique(3)).toList();

        assertEquals(List.of(1, 2, 6), integers);
    }
}
