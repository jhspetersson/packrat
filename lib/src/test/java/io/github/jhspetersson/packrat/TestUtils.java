package io.github.jhspetersson.packrat;

import java.util.List;
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

    static boolean isOrdered(List<Integer> list) {
        var prev = list.getFirst();
        for (var i = 1; i < list.size() - 1; i++) {
            if (list.get(i) <= prev) {
                return false;
            }
            prev = list.get(i);
        }
        return true;
    }

    static boolean isOrderedSequence(List<Integer> list) {
        for (var i = 0; i < list.size() - 1; i++) {
            if (i != list.get(i)) {
                return false;
            }
        }
        return true;
    }

    static boolean isReverseOrderedSequence(List<Integer> list) {
        for (var i = 0; i < list.size() - 1; i++) {
            if (list.size() - 1 - i != list.get(i)) {
                return false;
            }
        }
        return true;
    }
}
