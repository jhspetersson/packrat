package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import static io.github.jhspetersson.packrat.TestUtils.getEmployees;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("preview")
public class DistinctByTest {
    @Test
    public void distinctByTest() {
        var allEmployees = getEmployees().count();
        assertEquals(5, allEmployees);
        var distinctAge = getEmployees().gather(Packrat.distinctBy(Employee::age)).count();
        assertEquals(4, distinctAge);
    }
}
