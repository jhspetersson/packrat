package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RingBufferTest {
    @ParameterizedTest
    @CsvSource({
            "0, 0, 1, 0",
            "2, 1, 5, 3",
            "4, 4, 5, 3",
            "1000000000, 1147483647, 1500000000, 647483647",
            "1000000000, 1147483648, 1500000000, 647483648",
            "1000000000, 1499999999, 1500000000, 999999999",
            "2147483638, 2147483638, 2147483639, 2147483637",
            "2147483638, 1, 2147483639, 0"
    })
    void physicalIndexShouldAvoidOverflow(int head, int offset, int capacity, int expected) {
        // Exercise large logical capacities without allocating their backing arrays.
        assertEquals(expected, RingBuffer.physicalIndex(head, offset, capacity));
    }

    @Test
    void addAndRemoveInFifoOrder() {
        var buffer = new RingBuffer<Integer>(3);
        buffer.add(1);
        buffer.add(2);
        buffer.add(3);

        assertTrue(buffer.isFull());
        assertEquals(1, buffer.removeFirst());
        assertEquals(2, buffer.removeFirst());
        assertEquals(3, buffer.removeFirst());
        assertEquals(0, buffer.size());
    }

    @Test
    void wrapsAroundWhenCyclingElements() {
        var buffer = new RingBuffer<Integer>(3);
        for (var i = 0; i < 100; i++) {
            if (buffer.isFull()) {
                assertEquals(i - 3, buffer.removeFirst());
            }
            buffer.add(i);
        }

        assertEquals(List.of(97, 98, 99), buffer.toList());

        var collected = new ArrayList<Integer>();
        buffer.forEach(collected::add);
        assertEquals(List.of(97, 98, 99), collected);
    }

    @Test
    void growsLazilyUpToMaxSize() {
        var buffer = new RingBuffer<Integer>(1000);
        var expected = new ArrayList<Integer>();
        for (var i = 0; i < 1000; i++) {
            buffer.add(i);
            expected.add(i);
        }

        assertTrue(buffer.isFull());
        assertEquals(expected, buffer.toList());
    }

    @Test
    void growPreservesOrderAfterWrap() {
        var buffer = new RingBuffer<Integer>(100);
        // fill initial capacity, wrap head, then grow
        for (var i = 0; i < 16; i++) {
            buffer.add(i);
        }
        buffer.removeFirst();
        buffer.removeFirst();
        for (var i = 16; i < 50; i++) {
            buffer.add(i);
        }

        var expected = new ArrayList<Integer>();
        for (var i = 2; i < 50; i++) {
            expected.add(i);
        }
        assertEquals(expected, buffer.toList());
    }

    @Test
    void permitsNullElements() {
        var buffer = new RingBuffer<String>(3);
        buffer.add("a");
        buffer.add(null);
        buffer.add("b");

        assertEquals(Arrays.asList("a", null, "b"), buffer.toList());
        assertEquals("a", buffer.removeFirst());
        assertEquals(null, buffer.removeFirst());
    }

    @Test
    void iteratorReflectsInsertionOrder() {
        var buffer = new RingBuffer<Integer>(3);
        buffer.add(1);
        buffer.add(2);

        var collected = new ArrayList<Integer>();
        for (var element : buffer) {
            collected.add(element);
        }
        assertEquals(List.of(1, 2), collected);
    }

    @Test
    void removeFirstOnEmptyThrows() {
        var buffer = new RingBuffer<Integer>(1);
        assertThrows(NoSuchElementException.class, buffer::removeFirst);
        assertFalse(buffer.isFull());
    }

    @Test
    void nonPositiveMaxSizeThrows() {
        assertThrows(IllegalArgumentException.class, () -> new RingBuffer<Integer>(0));
        assertThrows(IllegalArgumentException.class, () -> new RingBuffer<Integer>(-1));
    }
}
