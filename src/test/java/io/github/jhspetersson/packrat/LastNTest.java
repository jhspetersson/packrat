package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static io.github.jhspetersson.packrat.TestUtils.getEmployees;
import static io.github.jhspetersson.packrat.TestUtils.isOrderedSequence;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
