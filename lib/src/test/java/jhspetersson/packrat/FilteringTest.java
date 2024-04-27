package jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static jhspetersson.packrat.Packrat.filterBy;
import static jhspetersson.packrat.Packrat.removeBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("preview")
public class FilteringTest {
    record Employee(String name, int age) {}

    @Test
    void filterByTest() {
        var age40 = getEmployees().gather(filterBy(Employee::age, 40)).toList();

        assertEquals(1, age40.size());
        assertEquals("John Rodgers", age40.getFirst().name());
    }

    @Test
    void filterByWithPredicateTest() {
        var under30 = getEmployees().gather(filterBy(Employee::age, 30, (age, threshold) -> age <= threshold)).toList();

        assertEquals(2, under30.size());
        assertEquals("Mark Bloom", under30.getFirst().name());
        assertEquals("Rebecca Schneider", under30.get(1).name());
    }

    @Test
    void removeByTest() {
        var noAge40 = getEmployees().gather(removeBy(Employee::age, 40)).toList();

        assertEquals(3, noAge40.size());
        assertTrue(noAge40.stream().noneMatch(employee -> employee.age() == 40));
    }

    @Test
    void removeByWithPredicateTest() {
        var under30 = getEmployees().gather(removeBy(Employee::age, 30, (age, threshold) -> age >= threshold)).toList();

        assertEquals(2, under30.size());
        assertEquals("Mark Bloom", under30.getFirst().name());
        assertEquals("Rebecca Schneider", under30.get(1).name());
    }

    private static Stream<Employee> getEmployees() {
        return Stream.of(
                new Employee("Ann Smith", 35),
                new Employee("John Rodgers", 40),
                new Employee("Mark Bloom", 21),
                new Employee("Rebecca Schneider", 24)
        );
    }
}
