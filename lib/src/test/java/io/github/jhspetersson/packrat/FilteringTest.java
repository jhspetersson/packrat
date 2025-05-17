package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.stream.Stream;

import static io.github.jhspetersson.packrat.Packrat.filterBy;
import static io.github.jhspetersson.packrat.Packrat.removeBy;
import static io.github.jhspetersson.packrat.TestUtils.getEmployees;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilteringTest {
    @Test
    void filterByTest() {
        var age40 = getEmployees().gather(filterBy(Employee::age, 40)).toList();

        assertEquals(1, age40.size());
        assertEquals("John Rodgers", age40.getFirst().name());
    }

    @Test
    void filterByWithPredicateTest() {
        var under30 = getEmployees().gather(filterBy(Employee::age, 30, (age, threshold) -> age <= threshold)).toList();

        assertEquals(3, under30.size());
        assertEquals("Mark Bloom", under30.getFirst().name());
        assertEquals("Rebecca Schneider", under30.get(1).name());
    }

    @Test
    void removeByTest() {
        var noAge40 = getEmployees().gather(removeBy(Employee::age, 40)).toList();

        assertEquals(4, noAge40.size());
        assertTrue(noAge40.stream().noneMatch(employee -> employee.age() == 40));
    }

    @Test
    void removeByWithPredicateTest() {
        var under30 = getEmployees().gather(removeBy(Employee::age, 30, (age, threshold) -> age >= threshold)).toList();

        assertEquals(3, under30.size());
        assertEquals("Mark Bloom", under30.getFirst().name());
        assertEquals("Rebecca Schneider", under30.get(1).name());
    }

    @Test
    void filterByWithNullValueTest() {
        Stream<Employee> employees = Stream.of(
                new Employee("Ann Smith", 35),
                new Employee(null, 40),
                new Employee("Mark Bloom", 21),
                new Employee(null, 24)
        );

        var nullNameEmployees = employees.gather(filterBy(Employee::name, null, Objects::equals)).toList();

        assertEquals(2, nullNameEmployees.size());
        assertEquals(40, nullNameEmployees.get(0).age());
        assertEquals(24, nullNameEmployees.get(1).age());
    }

    @Test
    void removeByWithNullValueTest() {
        Stream<Employee> employees = Stream.of(
                new Employee("Ann Smith", 35),
                new Employee(null, 40),
                new Employee("Mark Bloom", 21),
                new Employee(null, 24)
        );

        var nonNullNameEmployees = employees.gather(removeBy(Employee::name, null)).toList();

        assertEquals(2, nonNullNameEmployees.size());
        assertEquals("Ann Smith", nonNullNameEmployees.get(0).name());
        assertEquals("Mark Bloom", nonNullNameEmployees.get(1).name());
    }
}
