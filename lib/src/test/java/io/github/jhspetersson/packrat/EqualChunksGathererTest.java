package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EqualChunksGathererTest {
    @Test
    void equalChunksTest() {
        var numbers = Stream.of(1, 1, 2, 2, 2, 3, 4, 4, 5, 5, 5, 5, 6);
        var result = numbers.gather(Packrat.equalChunks()).toList();

        assertEquals(List.of(List.of(1, 1), List.of(2, 2, 2), List.of(3), List.of(4, 4), List.of(5, 5, 5, 5), List.of(6)), result);
    }

    @Test
    void equalChunksWithMapperTest() {
        var strings = Stream.of("apple", "apricot", "banana", "blueberry", "cherry", "date");
        var result = strings.gather(Packrat.equalChunksBy(s -> s.charAt(0))).toList();

        assertEquals(List.of(List.of("apple", "apricot"), List.of("banana", "blueberry"), List.of("cherry"), List.of("date")), result);
    }

    @Test
    void equalChunksWithObjectsTest() {
        record Person(String name, int age) {}

        var people = Stream.of(
            new Person("John", 25),
            new Person("Alice", 25),
            new Person("Bob", 30),
            new Person("Charlie", 30),
            new Person("David", 40),
            new Person("Eve", 40),
            new Person("Frank", 25)
        );

        var result = people.gather(Packrat.equalChunksBy(Person::age)).toList();

        assertEquals(List.of(
            List.of(new Person("John", 25), new Person("Alice", 25)),
            List.of(new Person("Bob", 30), new Person("Charlie", 30)),
            List.of(new Person("David", 40), new Person("Eve", 40)),
            List.of(new Person("Frank", 25))
        ), result);
    }

    @Test
    void emptyStreamTest() {
        var emptyStream = Stream.<Integer>empty();
        var result = emptyStream.gather(Packrat.equalChunks()).toList();

        assertEquals(List.of(), result);
    }

    @Test
    void singleElementTest() {
        var singleElement = Stream.of(42);
        var result = singleElement.gather(Packrat.equalChunks()).toList();

        assertEquals(List.of(List.of(42)), result);
    }

    @Test
    void equalChunksWithComparatorTest() {
        // Case-insensitive string comparison
        var strings = Stream.of("Apple", "apple", "Banana", "banana", "Cherry", "cherry");
        var result = strings.gather(Packrat.equalChunks(String.CASE_INSENSITIVE_ORDER)).toList();

        assertEquals(List.of(
            List.of("Apple", "apple"),
            List.of("Banana", "banana"),
            List.of("Cherry", "cherry")
        ), result);
    }

    @Test
    void equalChunksWithMapperAndComparatorTest() {
        record Person(String name, String id) {}

        // Group people by the first letter of their ID, case-insensitive
        var people = Stream.of(
            new Person("John", "A123"),
            new Person("Alice", "a456"),
            new Person("Bob", "B789"),
            new Person("Charlie", "b012"),
            new Person("David", "C345"),
            new Person("Eve", "c678")
        );

        var result = people.gather(Packrat.equalChunksBy(
            p -> p.id().substring(0, 1),  // Map to the first letter of ID
            String.CASE_INSENSITIVE_ORDER  // Compare case-insensitive
        )).toList();

        assertEquals(List.of(
            List.of(new Person("John", "A123"), new Person("Alice", "a456")),
            List.of(new Person("Bob", "B789"), new Person("Charlie", "b012")),
            List.of(new Person("David", "C345"), new Person("Eve", "c678"))
        ), result);
    }
}
