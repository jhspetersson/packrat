package jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("preview")
public class IntoListTest {
    @Test
    void shuffleTest() {
        var before = new ArrayList<Integer>();
        IntStream.range(0, 100).forEach(before::add);

        assertTrue(isOrdered(before));

        var after = before.stream().gather(Packrat.shuffle()).toList();

        assertFalse(isOrdered(after));
    }

    @Test
    void reverseTest() {
        var before = new ArrayList<Integer>();
        IntStream.range(0, 100).forEach(before::add);

        assertTrue(isOrdered(before));

        var after = before.stream().gather(Packrat.reverse()).toList();

        assertTrue(isReverseOrdered(after));
    }

    @Test
    void rotateTest() {
        var before = new ArrayList<Integer>();
        IntStream.range(0, 10).forEach(before::add);

        assertTrue(isOrdered(before));

        var after = before.stream().gather(Packrat.rotate(3)).toList();

        assertEquals(List.of(7, 8, 9, 0, 1, 2, 3, 4, 5, 6), after);

        var after2 = before.stream().gather(Packrat.rotate(-4)).toList();

        assertEquals(List.of(4, 5, 6, 7, 8, 9, 0, 1, 2, 3), after2);
    }

    private static boolean isOrdered(List<Integer> list) {
        for (var i = 0; i < list.size() - 1; i++) {
            if (i != list.get(i)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isReverseOrdered(List<Integer> list) {
        for (var i = 0; i < list.size() - 1; i++) {
            if (list.size() - 1 - i != list.get(i)) {
                return false;
            }
        }
        return true;
    }
}
