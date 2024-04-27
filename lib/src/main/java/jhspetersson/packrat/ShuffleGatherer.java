package jhspetersson.packrat;

import java.util.Collections;

/**
 * Shuffles the element stream.
 * <p>
 *
 * <pre>
 *   var randomlyOrdered = IntStream.range(0, 10).boxed().gather(shuffle()).toList();
 *   System.out.println(randomlyOrdered);
 *
 *   [2, 7, 6, 9, 8, 5, 1, 3, 0, 4]
 * </pre>
 *
 * @param <T> element type
 * @author jhspetersson
 */
public class ShuffleGatherer<T> extends IntoListGatherer<T> {
    public ShuffleGatherer() {
        super(Collections::shuffle);
    }
}
