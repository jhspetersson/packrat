package jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("preview")
public class AtLeastTest {
    @Test
    public void distinctByTest() {
        var numbers = Stream.of(1, 2, 3, 3, 3, 4, 5, 5, 6, 7, 8, 8, 8, 8, 9, 10);
        var atLeastThree = numbers.gather(Packrat.atLeast(3)).toList();
        assertEquals(List.of(3, 3, 3, 8, 8, 8, 8), atLeastThree);
    }
}
