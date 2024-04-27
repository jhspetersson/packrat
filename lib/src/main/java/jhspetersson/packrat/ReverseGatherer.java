package jhspetersson.packrat;

import java.util.Collections;

/**
 * Reverses the element stream.
 * <p>
 *
 * <pre>
 *   var reverseOrdered = IntStream.range(0, 10).boxed().gather(reverse()).toList();
 *   System.out.println(reverseOrdered);
 *
 *   [9, 8, 7, 6, 5, 4, 3, 2, 1, 0]
 * </pre>
 *
 * @param <T> element type
 * @author jhspetersson
 */
public class ReverseGatherer<T> extends IntoListGatherer<T> {
    public ReverseGatherer() {
        super(Collections::reverse);
    }
}
