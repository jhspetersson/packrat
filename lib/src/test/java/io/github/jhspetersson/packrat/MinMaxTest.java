package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static io.github.jhspetersson.packrat.Packrat.filterBy;
import static io.github.jhspetersson.packrat.Packrat.removeBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("preview")
public class MinMaxTest {
    @Test
    void emptyTest() {
        var check = Stream.empty().gather(Packrat.minBy(element -> Long.parseLong(element.toString()))).toList();

        assertTrue(check.isEmpty());
    }

    @Test
    void minTest() {
        var check = Stream.of("10", "2", "1", "-12", "22", "35", "66", "77", "123", "4", "7", "29")
                        .gather(Packrat.minBy(Long::parseLong))
                        .toList();

        assertEquals(1, check.size());
        assertEquals("-12", check.getFirst());
    }

    @Test
    void maxTest() {
        var check = Stream.of("10", "2", "1", "-12", "22", "35", "66", "77", "123", "4", "7", "29")
                .gather(Packrat.maxBy(Long::parseLong))
                .toList();

        assertEquals(1, check.size());
        assertEquals("123", check.getFirst());
    }
}
