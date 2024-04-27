package jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("preview")
public class PackratTest {
    @Test
    void shuffleTest() {
        var before = new ArrayList<Integer>();
        IntStream.range(0, 100).forEach(before::add);

        assertTrue(isOrdered(before));

        var after = before.stream().gather(Packrat.shuffle()).toList();

        assertFalse(isOrdered(after));
    }

    private static boolean isOrdered(List<Integer> list) {
        for (var i = 0; i < list.size() - 1; i++) {
            if (i != list.get(i)) {
                return false;
            }
        }
        return true;
    }
}
