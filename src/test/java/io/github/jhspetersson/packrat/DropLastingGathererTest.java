package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

import static io.github.jhspetersson.packrat.TestUtils.getEmployees;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DropLastingGathererTest {

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    public void shouldRetainMetadataForOnlyLastUniqueKeys(boolean dropAllOccurrences) {
        var gatherer = new DropLastingGatherer<Integer>(3, dropAllOccurrences, Function.identity());
        var state = gatherer.initializer().get();
        var integrator = gatherer.integrator();
        Gatherer.Downstream<Integer> downstream = _ -> true;

        for (var i = 0; i < 10_000; i++) {
            assertTrue(integrator.integrate(state, i, downstream));
            assertEquals(Math.min(i + 1, 3), state.lastIndexByKey.size());
        }

        assertEquals(List.of(9_997, 9_998, 9_999), List.copyOf(state.lastIndexByKey.keySet()));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    public void shouldSupportLongMaxValueCapacity(boolean dropAllOccurrences) {
        var result = Stream.of(1, 2, 1, 2)
                .gather(new DropLastingGatherer<>(Long.MAX_VALUE, dropAllOccurrences, Function.identity()))
                .toList();

        assertEquals(dropAllOccurrences ? List.of() : List.of(1, 2), result);
    }

    @Test
    public void dropLastUniqueShouldHandleEvictedKeysReturning() {
        var result = Stream.of(1, 2, 1, 3, 2, 4)
                .gather(Packrat.dropLastUnique(2))
                .toList();

        assertEquals(List.of(1, 2, 1, 3), result);
    }

    @Test
    public void dropLastUniqueByShouldHandleEvictedNullKeysReturning() {
        var result = Stream.of("a1", "b1", "a2", "c1", "b2", "d1")
                .gather(Packrat.dropLastUniqueBy(2, s -> s.startsWith("b") ? null : s.charAt(0)))
                .toList();

        assertEquals(List.of("a1", "b1", "a2", "c1"), result);
    }

    @Test
    public void dropLastByShouldHandleEvictedNullKeysReturning() {
        var result = Stream.of("a1", "b1", "a2", "c1", "b2", "d1")
                .gather(Packrat.dropLastBy(2, s -> s.startsWith("b") ? null : s.charAt(0)))
                .toList();

        assertEquals(List.of("a1", "a2", "c1"), result);
    }

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
    public void dropLastUniqueZeroShouldStreamLazily() {
        var result = org.junit.jupiter.api.Assertions.assertTimeoutPreemptively(java.time.Duration.ofSeconds(2), () ->
                Stream.iterate(0, i -> i + 1).gather(Packrat.dropLastUnique(0)).limit(3).toList());

        assertEquals(List.of(0, 1, 2), result);
    }

    @Test
    public void dropLastByZeroShouldStreamLazily() {
        var result = org.junit.jupiter.api.Assertions.assertTimeoutPreemptively(java.time.Duration.ofSeconds(2), () ->
                Stream.iterate(0, i -> i + 1).gather(Packrat.dropLastBy(0, Function.identity())).limit(3).toList());

        assertEquals(List.of(0, 1, 2), result);
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
    public void dropLastByShouldInvokeMapperOncePerElement() {
        var count = new java.util.concurrent.atomic.AtomicInteger();

        var result = Stream.of("a", "b", "c", "a", "b")
                .gather(Packrat.dropLastBy(1, s -> {
                    count.incrementAndGet();
                    return s;
                }))
                .toList();

        assertEquals(List.of("a", "c", "a"), result);
        assertEquals(5, count.get());
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
