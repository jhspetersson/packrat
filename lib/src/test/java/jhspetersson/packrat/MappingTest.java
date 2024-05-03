package jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("preview")
public class MappingTest {
    @Test
    public void mapFirstTest() {
        var mapped = IntStream.rangeClosed(1, 10).boxed().gather(Packrat.mapFirst(n -> n * 10)).toList();
        assertEquals(List.of(10, 2, 3, 4, 5, 6, 7, 8, 9, 10), mapped);
    }

    @Test
    public void mapNTest() {
        var mapped = IntStream.rangeClosed(1, 10).boxed().gather(Packrat.mapN(5, n -> n * 10)).toList();
        assertEquals(List.of(10, 20, 30, 40, 50, 6, 7, 8, 9, 10), mapped);
    }

    @Test
    public void skipAndMapTest() {
        var mapped = IntStream.rangeClosed(1, 10).boxed().gather(Packrat.skipAndMap(3, n -> n * 10)).toList();
        assertEquals(List.of(1, 2, 3, 40, 50, 60, 70, 80, 90, 100), mapped);
    }

    @Test
    public void skipAndMapNTest() {
        var mapped = IntStream.rangeClosed(1, 10).boxed().gather(Packrat.skipAndMapN(3, 5, n -> n * 10)).toList();
        assertEquals(List.of(1, 2, 3, 40, 50, 60, 70, 80, 9, 10), mapped);
    }
}
