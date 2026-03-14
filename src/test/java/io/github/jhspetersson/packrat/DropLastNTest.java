package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static io.github.jhspetersson.packrat.TestUtils.isOrderedSequence;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DropLastNTest {
    @Test
    public void dropLastTest() {
        var before = new ArrayList<Integer>();
        IntStream.range(0, 10).forEach(before::add);

        assertTrue(isOrderedSequence(before));

        var after = before.stream().gather(Packrat.dropLast(3)).toList();

        assertEquals(List.of(0, 1, 2, 3, 4, 5, 6), after);
    }

    @Test
    public void dropLastEmptyTest() {
        var before = new ArrayList<Integer>();

        var after = before.stream().gather(Packrat.dropLast(3)).toList();

        assertEquals(List.of(), after);
    }

    @Test
    public void dropLastAllTest() {
        var before = new ArrayList<Integer>();
        IntStream.range(0, 5).forEach(before::add);

        var after = before.stream().gather(Packrat.dropLast(5)).toList();

        assertEquals(List.of(), after);
    }

    @Test
    public void dropLastMoreThanSizeTest() {
        var before = new ArrayList<Integer>();
        IntStream.range(0, 5).forEach(before::add);

        var after = before.stream().gather(Packrat.dropLast(10)).toList();

        assertEquals(List.of(), after);
    }

    @Test
    public void dropLastDefaultTest() {
        var before = new ArrayList<Integer>();
        IntStream.range(0, 5).forEach(before::add);

        var after = before.stream().gather(Packrat.dropLast()).toList();

        assertEquals(List.of(0, 1, 2, 3), after);
    }

    @Test
    public void dropLastZeroTest() {
        var before = new ArrayList<Integer>();
        IntStream.range(0, 5).forEach(before::add);

        var after = before.stream().gather(Packrat.dropLast(0)).toList();

        assertEquals(List.of(0, 1, 2, 3, 4), after);
    }

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
