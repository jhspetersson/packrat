package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import static io.github.jhspetersson.packrat.Packrat.filterBy;
import static io.github.jhspetersson.packrat.Packrat.removeBy;
import static io.github.jhspetersson.packrat.TestUtils.getEmployees;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("preview")
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
}
