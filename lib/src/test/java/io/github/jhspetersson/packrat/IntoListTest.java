package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static io.github.jhspetersson.packrat.TestUtils.isOrderedSequence;
import static io.github.jhspetersson.packrat.TestUtils.isReverseOrderedSequence;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("preview")
public class IntoListTest {
    @Test
    void shuffleTest() {
        var before = new ArrayList<Integer>();
        IntStream.range(0, 100).forEach(before::add);

        assertTrue(isOrderedSequence(before));

        var after = before.stream().gather(Packrat.shuffle()).toList();

        assertFalse(isOrderedSequence(after));
    }

    @Test
    void parallelShuffleTest() {
        var size = 100000;
        var before = new ArrayList<Integer>();
        IntStream.range(0, size).forEach(before::add);

        assertTrue(isOrderedSequence(before));
        assertEquals(size, before.size());

        var after = before.parallelStream().gather(Packrat.shuffle()).toList();

        assertFalse(isOrderedSequence(after));
        assertEquals(size, after.size());
        assertEquals(size, Set.copyOf(after).size());
    }

    @Test
    void reverseTest() {
        var before = new ArrayList<Integer>();
        IntStream.range(0, 100).forEach(before::add);

        assertTrue(isOrderedSequence(before));

        var after = before.stream().gather(Packrat.reverse()).toList();

        assertTrue(isReverseOrderedSequence(after));
    }

    @Test
    void rotateTest() {
        var before = new ArrayList<Integer>();
        IntStream.range(0, 10).forEach(before::add);

        assertTrue(isOrderedSequence(before));

        var after = before.stream().gather(Packrat.rotate(3)).toList();

        assertEquals(List.of(7, 8, 9, 0, 1, 2, 3, 4, 5, 6), after);

        var after2 = before.stream().gather(Packrat.rotate(-4)).toList();

        assertEquals(List.of(4, 5, 6, 7, 8, 9, 0, 1, 2, 3), after2);
    }
}
