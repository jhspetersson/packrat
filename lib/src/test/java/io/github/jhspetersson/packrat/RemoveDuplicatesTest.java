package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RemoveDuplicatesTest {
    @Test
    public void removeDuplicatesTest() {
        var listWithCopies = List.of(0, 1, 2, 2, 3, 4, 5, 5, 6, 7, 8, 8, 8, 9, 8, 7, 7, 6, 5, 4, 4, 4, 3, 2, 1, 0);
        var unique = listWithCopies.stream().gather(Packrat.removeDuplicates()).toList();

        var expected = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);

        assertEquals(expected, unique);
    }

    @Test
    public void emptyListTest() {
        var emptyList = List.<Integer>of();
        var result = emptyList.stream().gather(Packrat.removeDuplicates()).toList();

        assertEquals(List.of(), result);
    }

    @Test
    public void singleElementTest() {
        var singleElement = List.of(42);
        var result = singleElement.stream().gather(Packrat.removeDuplicates()).toList();

        assertEquals(List.of(42), result);
    }

    @Test
    public void allDuplicatesTest() {
        var allDuplicates = List.of(1, 1, 1, 1, 1);
        var result = allDuplicates.stream().gather(Packrat.removeDuplicates()).toList();

        assertEquals(List.of(1), result);
    }

    @Test
    public void noDuplicatesTest() {
        var noDuplicates = List.of(1, 2, 3, 4, 5);
        var result = noDuplicates.stream().gather(Packrat.removeDuplicates()).toList();

        assertEquals(List.of(1, 2, 3, 4, 5), result);
    }

    @Test
    public void removeDuplicatesByTest() {
        var listWithCopies = List.of(
            new Person("John", 25),
            new Person("Alice", 30),
            new Person("Bob", 30),
            new Person("Charlie", 30),
            new Person("David", 40),
            new Person("Eve", 40),
            new Person("Frank", 40)
        );

        var uniqueByAge = listWithCopies.stream().gather(Packrat.removeDuplicatesBy(Person::age)).toList();

        var expectedByAge = List.of(
            new Person("John", 25),
            new Person("Alice", 30),
            new Person("David", 40)
        );

        assertEquals(expectedByAge, uniqueByAge);
    }

    @Test
    public void removeDuplicatesByWithNullsTest() {
        var listWithNulls = java.util.Arrays.asList("a", "b", null, null, "c", "d", "d");

        var result = listWithNulls.stream().gather(Packrat.removeDuplicatesBy(s -> s)).toList();

        var expected = java.util.Arrays.asList("a", "b", null, "c", "d");

        assertEquals(expected, result);
    }

    @Test
    public void removeDuplicatesByStringLengthTest() {
        var strings = List.of("a", "b", "cc", "d", "ee", "fff", "gg", "h", "ii");

        var uniqueByLength = strings.stream().gather(Packrat.removeDuplicatesBy(String::length)).toList();

        var expectedByLength = List.of("a", "cc", "d", "ee", "fff", "gg", "h", "ii");

        assertEquals(expectedByLength, uniqueByLength);
    }

    private record Person(String name, int age) {}
}