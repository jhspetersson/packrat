package jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("preview")
public class CollectingTest {
    @Test
    void collectingTest() {
        var numbers = Stream.of(1, 2, 3, 4, 5);
        var result = numbers.gather(Packrat.asGatherer(Collectors.toList())).toList();
        assertEquals(List.of(List.of(1, 2, 3, 4, 5)), result);
    }
}
