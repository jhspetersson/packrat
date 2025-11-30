package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static io.github.jhspetersson.packrat.TestUtils.getEmployees;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LastingGathererTest {

    @Test
    public void lastWindowSimple() {
        var data = IntStream.range(0, 10).boxed().toList();

        var result = data.stream()
                .gather(new LastingGatherer<>(3))
                .toList();

        assertEquals(List.of(7, 8, 9), result);
    }

    @Test
    public void zeroNProducesEmpty() {
        var data = IntStream.range(0, 10).boxed().toList();

        var result = data.stream()
                .gather(new LastingGatherer<>(0))
                .toList();

        assertTrue(result.isEmpty());
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
                new Employee("Mark Bloom", 21),
                new Employee("Rebecca Schneider", 24)
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
}
