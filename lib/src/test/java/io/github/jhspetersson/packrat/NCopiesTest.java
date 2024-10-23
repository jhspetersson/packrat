package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("preview")
public class NCopiesTest {
    @Test
    public void nCopiesTest() {
        var sum = IntStream.of(5).boxed().gather(Packrat.nCopies(20)).reduce(Integer::sum).orElseThrow();
        assertEquals(100, sum);
    }
}
