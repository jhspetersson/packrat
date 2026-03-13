package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LastUniqueBugTest {

    @Test
    void lastUniqueShouldMoveDuplicateToEnd() {
        var result = List.of("A", "B", "C", "A").stream()
                .gather(Packrat.lastUnique(3))
                .toList();

        assertEquals(List.of("B", "C", "A"), result);
    }

    @Test
    void lastUniqueShouldReflectMostRecentOrder() {
        var result = List.of(1, 2, 3, 2).stream()
                .gather(Packrat.lastUnique(3))
                .toList();

        assertEquals(List.of(1, 3, 2), result);
    }

    @Test
    void lastUniqueMultipleDuplicatesShouldMoveToEnd() {
        var result = List.of(1, 2, 3, 1, 3).stream()
                .gather(Packrat.lastUnique(3))
                .toList();

        assertEquals(List.of(2, 1, 3), result);
    }
}
