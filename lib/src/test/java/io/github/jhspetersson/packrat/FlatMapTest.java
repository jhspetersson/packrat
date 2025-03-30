package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlatMapTest {
    @Test
    public void flatMapIfTest() {
        var strings = Stream.of("A", "B", "CDE", "F", "G", "H", "IJ", "KL", "M", "NOP");
        var result = strings.gather(Packrat.flatMapIf(s -> Arrays.stream(s.split("")), s -> s.length() > 1)).toList();
        assertEquals(List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P"), result);
    }
}
