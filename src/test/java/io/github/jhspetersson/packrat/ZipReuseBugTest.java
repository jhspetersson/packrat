package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZipReuseBugTest {

    @Test
    void zipWithIterableShouldBeReusable() {
        var source = List.of(10, 20, 30);
        var gatherer = Packrat.zip(source, (Integer a, Integer b) -> a + b);

        var result1 = Stream.of(1, 2, 3).gather(gatherer).toList();
        assertEquals(List.of(11, 22, 33), result1);

        var result2 = Stream.of(4, 5, 6).gather(gatherer).toList();
        assertEquals(List.of(14, 25, 36), result2);
    }
}
