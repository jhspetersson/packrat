package jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("preview")
public class AtLeastTest {
    @Test
    public void atLeastTest() {
        var numbers = Stream.of(1, 2, 3, 3, 3, 4, 5, 5, 6, 7, 8, 8, 8, 8, 9, 10);
        var result = numbers.gather(Packrat.atLeast(3)).toList();
        assertEquals(List.of(3, 3, 3, 8, 8, 8, 8), result);
    }

    @Test
    public void atLeastUnorderedTest() {
        var numbers = Stream.of(1, 10, 3, 2, 3, 8, 4, 9, 5, 6, 7, 8, 3, 8, 8, 5);
        var result = numbers.gather(Packrat.atLeast(3)).toList();
        assertEquals(List.of(3, 3, 3, 8, 8, 8, 8), result);
    }

    @Test
    public void atLeastEmptyTest() {
        var numbers = Stream.of(1, 2, 3, 3, 3, 4, 5, 5, 6, 7, 8, 8, 8, 8, 9, 10);
        var result = numbers.gather(Packrat.atLeast(10)).toList();
        assertTrue(result.isEmpty());
    }

    @Test
    public void atLeastEmptySourceTest() {
        var numbers = Stream.of();
        var result = numbers.gather(Packrat.atLeast(10)).toList();
        assertTrue(result.isEmpty());
    }

    @Test
    public void atLeastSingleValueTest() {
        var numbers = Stream.of(1, 1, 1);
        var result = numbers.gather(Packrat.atLeast(3)).toList();
        assertEquals(List.of(1, 1, 1), result);
    }
}
