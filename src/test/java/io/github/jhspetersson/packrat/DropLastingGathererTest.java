package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static io.github.jhspetersson.packrat.TestUtils.getEmployees;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DropLastingGathererTest {

    @Test
    public void dropLastUniqueTest() {
        var result = Stream.of(1, 2, 3, 4, 5, 4, 1, 1, 1, 2, 2, 6)
                .gather(Packrat.dropLastUnique(3))
                .toList();

        assertEquals(List.of(1, 2, 3, 4, 5, 4, 1, 1, 2), result);
    }

    @Test
    public void dropLastUniqueSingleTest() {
        var result = Stream.of(1, 2, 3, 4)
                .gather(Packrat.dropLastUnique(1))
                .toList();

        assertEquals(List.of(1, 2, 3), result);
    }

    @Test
    public void dropLastUniqueMoreThanUniqueCountTest() {
        var result = Stream.of(1, 2, 1, 2)
                .gather(Packrat.dropLastUnique(10))
                .toList();

        assertEquals(List.of(1, 2), result);
    }

    @Test
    public void dropLastUniqueZeroTest() {
        var result = Stream.of(1, 2, 3)
                .gather(Packrat.dropLastUnique(0))
                .toList();

        assertEquals(List.of(1, 2, 3), result);
    }

    @Test
    public void dropLastUniqueEmptyTest() {
        var result = Stream.<Integer>of()
                .gather(Packrat.dropLastUnique(3))
                .toList();

        assertTrue(result.isEmpty());
    }

    @Test
    public void dropLastUniqueByTest() {
        var result = getEmployees()
                .gather(Packrat.dropLastUniqueBy(3, Employee::age))
                .toList();

        assertEquals(List.of(
                new Employee("Ann Smith", 35),
                new Employee("Mark Bloom", 21)
        ), result);
    }

    @Test
    public void dropLastUniqueByOneTest() {
        var result = getEmployees()
                .gather(Packrat.dropLastUniqueBy(1, Employee::age))
                .toList();

        assertEquals(List.of(
                new Employee("Ann Smith", 35),
                new Employee("John Rodgers", 40),
                new Employee("Mark Bloom", 21),
                new Employee("Rebecca Schneider", 24)
        ), result);
    }

    @Test
    public void dropLastByTest() {
        var result = Stream.of(1, 2, 3, 4, 5, 4, 1, 1, 1, 2, 2, 6)
                .gather(Packrat.dropLastBy(3, Function.identity()))
                .toList();

        assertEquals(List.of(3, 4, 5, 4), result);
    }

    @Test
    public void dropLastByEmployeeAgeTest() {
        var result = getEmployees()
                .gather(Packrat.dropLastBy(2, Employee::age))
                .toList();

        assertEquals(List.of(
                new Employee("Ann Smith", 35),
                new Employee("John Rodgers", 40)
        ), result);
    }

    @Test
    public void dropLastByZeroTest() {
        var result = Stream.of(1, 2, 3)
                .gather(Packrat.dropLastBy(0, Function.identity()))
                .toList();

        assertEquals(List.of(1, 2, 3), result);
    }

    @Test
    public void dropLastByMoreThanUniqueCountTest() {
        var result = Stream.of(1, 2, 1, 2)
                .gather(Packrat.dropLastBy(10, Function.identity()))
                .toList();

        assertTrue(result.isEmpty());
    }

    @Test
    public void negativeNThrows() {
        assertThrows(IllegalArgumentException.class, () -> Packrat.dropLastUnique(-1));
        assertThrows(IllegalArgumentException.class, () -> Packrat.dropLastUniqueBy(-1, Function.identity()));
        assertThrows(IllegalArgumentException.class, () -> Packrat.dropLastBy(-1, Function.identity()));
    }

    @Test
    public void nullMapperThrows() {
        assertThrows(NullPointerException.class, () -> Packrat.dropLastUniqueBy(3, null));
        assertThrows(NullPointerException.class, () -> Packrat.dropLastBy(3, null));
    }
}
