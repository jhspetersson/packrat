package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ThrowIfNotOrderedTest {

    @Test
    void orderedIntegersPassThrough() {
        var input = List.of(1, 1, 2, 3, 3, 5);
        var result = input.stream().gather(Packrat.throwIfNotOrdered()).toList();
        assertEquals(input, result);
    }

    @Test
    void unorderedIntegersThrowDefault() {
        assertThrows(IllegalStateException.class, () ->
                Stream.of(1, 3, 2, 4).gather(Packrat.throwIfNotOrdered()).toList()
        );
    }

    @Test
    void customMapperOrderedByKey() {
        // order employees by age and ensure pass-through using age mapper
        var result = Stream.of(
                        new Employee("Young A", 20),
                        new Employee("Young B", 20),
                        new Employee("Mid", 30),
                        new Employee("Senior", 40)
                )
                .gather(Packrat.throwIfNotOrderedBy(Employee::age))
                .toList();

        assertEquals(4, result.size());
        assertEquals("Young A", result.getFirst().name());
        assertEquals("Senior", result.getLast().name());
    }

    @Test
    void customExceptionSupplierIsUsed() {
        class MyEx extends IllegalArgumentException {}
        assertThrows(MyEx.class, () ->
                Stream.of(1, 5, 4).gather(Packrat.throwIfNotOrdered(MyEx::new)).toList()
        );
    }

    @Test
    void customExceptionSupplierIsUsedWithMapper() {
        class MyEx extends IllegalArgumentException {}
        assertThrows(MyEx.class, () ->
                Stream.of(1, 5, 4).gather(Packrat.throwIfNotOrderedBy(i -> i * 2, MyEx::new)).toList()
        );
    }
}
