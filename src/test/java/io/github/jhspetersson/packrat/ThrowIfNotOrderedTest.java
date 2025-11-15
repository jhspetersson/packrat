package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ThrowIfNotOrderedTest {

    @Test
    void orderedIntegersPassThrough() {
        var input = List.of(1, 1, 2, 3, 3, 5);
        var result = input.stream().gather(Packrat.throwIfNotIncreasingOrEqual()).toList();
        assertEquals(input, result);
    }

    @Test
    void unorderedIntegersThrowDefault() {
        assertThrows(IllegalStateException.class, () ->
                Stream.of(1, 3, 2, 4).gather(Packrat.throwIfNotIncreasingOrEqual()).toList()
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
                .gather(Packrat.throwIfNotIncreasingOrEqualBy(Employee::age))
                .toList();

        assertEquals(4, result.size());
        assertEquals("Young A", result.getFirst().name());
        assertEquals("Senior", result.getLast().name());
    }

    @Test
    void customExceptionSupplierIsUsed() {
        class MyEx extends IllegalArgumentException {}
        assertThrows(MyEx.class, () ->
                Stream.of(1, 5, 4).gather(Packrat.throwIfNotIncreasingOrEqual(MyEx::new)).toList()
        );
    }

    @Test
    void customExceptionSupplierIsUsedWithMapper() {
        class MyEx extends IllegalArgumentException {}
        assertThrows(MyEx.class, () ->
                Stream.of(1, 5, 4).gather(Packrat.throwIfNotIncreasingOrEqualBy(i -> i * 2, MyEx::new)).toList()
        );
    }

    @Test
    void decreasingOrEqual_customExceptionSupplierIsUsed() {
        class MyEx extends IllegalArgumentException {}
        assertThrows(MyEx.class, () ->
                Stream.of(5, 4, 6).gather(Packrat.throwIfNotDecreasingOrEqual(MyEx::new)).toList()
        );
    }

    @Test
    void decreasingOrEqual_withMapper_pass_and_customException() {
        // pass when mapped keys are non-increasing
        var list = Stream.of(
                        new Employee("A", 40),
                        new Employee("B", 40),
                        new Employee("C", 30)
                )
                .gather(Packrat.throwIfNotDecreasingOrEqualBy(Employee::age))
                .toList();
        assertEquals(3, list.size());

        // custom exception when increases by mapper
        class MyEx extends IllegalArgumentException {}
        assertThrows(MyEx.class, () ->
                Stream.of(
                                new Employee("X", 50),
                                new Employee("Y", 49),
                                new Employee("Z", 60)
                        )
                        .gather(Packrat.throwIfNotDecreasingOrEqualBy(Employee::age, MyEx::new))
                        .toList()
        );
    }

    @Test
    void increasing_strict_withSupplier_and_mapper() {
        // custom supplier used on equal
        class MyEx extends IllegalArgumentException {}
        assertThrows(MyEx.class, () ->
                Stream.of(1, 2, 2, 3).gather(Packrat.throwIfNotIncreasing(MyEx::new)).toList()
        );

        // pass strictly increasing by mapper
        var ok = Stream.of(
                        new Employee("e1", 10),
                        new Employee("e2", 11),
                        new Employee("e3", 12)
                )
                .gather(Packrat.throwIfNotIncreasingBy(Employee::age))
                .toList();
        assertEquals(3, ok.size());

        // fail on equal by mapper with custom supplier
        assertThrows(MyEx.class, () ->
                Stream.of(
                                new Employee("a", 10),
                                new Employee("b", 10)
                        )
                        .gather(Packrat.throwIfNotIncreasingBy(Employee::age, MyEx::new))
                        .toList()
        );
    }

    @Test
    void decreasing_strict_withSupplier_and_mapper() {
        // custom supplier used on increase
        class MyEx extends IllegalArgumentException {}
        assertThrows(MyEx.class, () ->
                Stream.of(5, 4, 6).gather(Packrat.throwIfNotDecreasing(MyEx::new)).toList()
        );

        // pass strictly decreasing by mapper
        var ok = Stream.of(
                        new Employee("e1", 12),
                        new Employee("e2", 11),
                        new Employee("e3", 10)
                )
                .gather(Packrat.throwIfNotDecreasingBy(Employee::age))
                .toList();
        assertEquals(3, ok.size());

        // fail on equal by mapper with custom supplier
        assertThrows(MyEx.class, () ->
                Stream.of(
                                new Employee("a", 10),
                                new Employee("b", 10)
                        )
                        .gather(Packrat.throwIfNotDecreasingBy(Employee::age, MyEx::new))
                        .toList()
        );
    }

    @Test
    void increasing_strict_pass_and_fail_on_equal() {
        // pass
        assertDoesNotThrow(() -> Stream.of(1, 2, 3, 4).gather(Packrat.throwIfNotIncreasing()).toList());
        // fail on equal
        assertThrows(IllegalStateException.class, () ->
                Stream.of(1, 2, 2, 3).gather(Packrat.throwIfNotIncreasing()).toList()
        );
    }

    @Test
    void decreasing_strict_pass_and_fail_on_equal_and_increase() {
        // pass
        assertDoesNotThrow(() -> Stream.of(5, 3, 1).gather(Packrat.throwIfNotDecreasing()).toList());
        // fail on equal
        assertThrows(IllegalStateException.class, () ->
                Stream.of(5, 3, 3, 1).gather(Packrat.throwIfNotDecreasing()).toList()
        );
        // fail on increasing step
        assertThrows(IllegalStateException.class, () ->
                Stream.of(5, 4, 6).gather(Packrat.throwIfNotDecreasing()).toList()
        );
    }

    @Test
    void decreasing_or_equal_pass_and_fail_on_increase() {
        // pass (equals allowed)
        assertDoesNotThrow(() -> Stream.of(5, 5, 4, 4, 4, 0).gather(Packrat.throwIfNotDecreasingOrEqual()).toList());
        // fail on increasing step
        assertThrows(IllegalStateException.class, () ->
                Stream.of(5, 3, 4).gather(Packrat.throwIfNotDecreasingOrEqual()).toList()
        );
    }
}
