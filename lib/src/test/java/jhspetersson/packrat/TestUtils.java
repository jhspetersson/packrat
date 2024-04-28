package jhspetersson.packrat;

import java.util.stream.Stream;

class TestUtils {

    static Stream<Employee> getEmployees() {
        return Stream.of(
                new Employee("Ann Smith", 35),
                new Employee("John Rodgers", 40),
                new Employee("Mark Bloom", 21),
                new Employee("Rebecca Schneider", 24),
                new Employee("Luke Norman", 21)
        );
    }
}
