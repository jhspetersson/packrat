package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.github.jhspetersson.packrat.TestUtils.getEmployees;
import static io.github.jhspetersson.packrat.TestUtils.isOrderedSequence;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LastNTest {
    @Test
    void lastUniqueByReusesEntryForRepeatedKey() {
        var gatherer = new LastingGatherer<String>(2, true, String::length);
        var state = gatherer.initializer().get();
        state.addElement("a", 2);
        var entry = state.lastByKey.entrySet().iterator().next();

        state.addElement("bb", 2);
        state.addElement("c", 2);

        assertSame(entry, new ArrayList<>(state.lastByKey.entrySet()).getLast());
        assertEquals("c", entry.getValue());
    }

    @Test
    void lastUniqueUpdatesRecencyBeforeEviction() {
        var result = Stream.of("a", "b", "a", "c")
                .gather(Packrat.lastUnique(2)).toList();

        assertEquals(List.of("a", "c"), result);
    }

    @Test
    void lastUniqueUpdatesRecencyForNullElement() {
        var result = Stream.of(null, "a", null, "b")
                .gather(Packrat.lastUnique(2)).toList();

        assertEquals(Arrays.asList(null, "b"), result);
    }

    @Test
    void lastUniqueByUpdatesValuesAndRecencyForRepeatedKeys() {
        record Item(String key, int version) {}

        var result = Stream.of(new Item("a", 1), new Item(null, 1), new Item("b", 1),
                        new Item(null, 2), new Item("a", 2), new Item("c", 1))
                .gather(Packrat.lastUniqueBy(3, Item::key)).toList();

        assertEquals(List.of(new Item(null, 2), new Item("a", 2), new Item("c", 1)), result);
    }

    @Test
    public void lastUniqueShouldNotBeQuadratic() {
        var result = assertTimeoutPreemptively(java.time.Duration.ofSeconds(10), () ->
                IntStream.range(0, 1_000_000)
                        .map(i -> i % 5_000)
                        .boxed()
                        .gather(Packrat.lastUnique(10_000))
                        .toList());

        assertEquals(5_000, result.size());
        // order of last occurrence: 995_000 .. 999_999 map to 0 .. 4_999
        assertEquals(0, result.getFirst());
        assertEquals(4_999, result.getLast());
    }

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
    void lastPreservesOrderAfterBufferGrowthAndWraparound() {
        var result = IntStream.range(0, 200).boxed()
                .gather(Packrat.last(33))
                .toList();

        assertEquals(IntStream.range(167, 200).boxed().toList(), result);
    }

    @Test
    void lastWithMaxLimitPreservesShortInput() {
        var input = Arrays.asList("a", null, "b");
        var result = input.stream()
                .gather(Packrat.last(Long.MAX_VALUE))
                .toList();

        assertEquals(input, result);
    }

    @Test
    public void lastZeroTest() {
        var before = new ArrayList<Integer>();
        IntStream.range(0, 100).forEach(before::add);

        assertTrue(isOrderedSequence(before));

        var after = before.stream().gather(Packrat.last(0)).toList();

        assertTrue(after.isEmpty());
    }

    @Test
    public void lastNUniqueTest() {
        var integers = List.of(1, 2, 3, 4, 5, 4, 1, 1, 1, 2, 2, 6).stream().gather(Packrat.lastUnique(3)).toList();

        assertEquals(List.of(1, 2, 6), integers);
    }

    @Test
    public void lastWindowSimple() {
        var data = IntStream.range(0, 10).boxed().toList();

        var result = data.stream()
                .gather(new LastingGatherer<>(3))
                .toList();

        assertEquals(List.of(7, 8, 9), result);
    }

    @Test
    public void negativeNThrows() {
        assertThrows(IllegalArgumentException.class, () -> new LastingGatherer<Integer>(-1));
    }

    @Test
    public void uniqueIdentityMode() {
        var data = List.of(1, 2, 3, 4, 5, 4, 1, 1, 1, 2, 2, 6);

        var result = data.stream()
                .gather(new LastingGatherer<>(3, true))
                .toList();

        assertEquals(List.of(1, 2, 6), result);
    }

    @Test
    public void uniqueCustomMapperMode() {
        var result = getEmployees()
                .gather(new LastingGatherer<>(3, true, Employee::age))
                .toList();

        assertEquals(List.of(
                new Employee("John Rodgers", 40),
                new Employee("Rebecca Schneider", 24),
                new Employee("Luke Norman", 21)
        ), result);
    }

    @Test
    public void lastUniqueByZeroTest() {
        var result = getEmployees()
                .gather(Packrat.lastUniqueBy(0, Employee::age))
                .toList();

        assertTrue(result.isEmpty());
    }

    @Test
    public void nullMapperThrows() {
        assertThrows(NullPointerException.class, () -> new LastingGatherer<Employee>(3, true, null));
    }

    @Test
    void lastWithNullElements() {
        var after = Stream.of("a", null, "b", null, "c")
                .gather(Packrat.last(2))
                .toList();

        assertEquals(Arrays.asList(null, "c"), after);
    }

    @Test
    void lastUniqueShouldMoveDuplicateToEnd() {
        var result = List.of("A", "B", "C", "A").stream()
                .gather(Packrat.lastUnique(3))
                .toList();

        assertEquals(List.of("B", "C", "A"), result);
    }

    @Test
    void lastUniqueShouldReflectMostRecentOrder() {
        var result = List.of(1, 2, 3, 2).stream()
                .gather(Packrat.lastUnique(3))
                .toList();

        assertEquals(List.of(1, 3, 2), result);
    }

    @Test
    void lastUniqueMultipleDuplicatesShouldMoveToEnd() {
        var result = List.of(1, 2, 3, 1, 3).stream()
                .gather(Packrat.lastUnique(3))
                .toList();

        assertEquals(List.of(2, 1, 3), result);
    }
}
