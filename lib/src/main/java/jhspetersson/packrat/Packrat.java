package jhspetersson.packrat;

import java.util.Collections;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Gatherer;

/**
 * Provides Gatherer instances from its fabric methods.
 *
 * @author jhspetersson
 */
@SuppressWarnings("preview")
public final class Packrat {
    /**
     * Provides instance of {@link FilteringGatherer} that checks equality of the mapped element with the specific value.
     * Passed elements unmodified go down the stream.
     *
     * @param mapper mapping function
     * @param value specific value
     * @param <T> element type
     * @param <U> mapped element type
     */
    public static <T, U> Gatherer<T, ?, T> filterBy(Function<T, U> mapper, U value) {
        return new FilteringGatherer<>(mapper, value);
    }

    /**
     * Provides instance of {@link FilteringGatherer} that tests mapped element against the specific value with some predicate.
     * Passed elements unmodified go down the stream.
     *
     * @param mapper mapping function
     * @param value specific value
     * @param predicate testing predicate
     * @param <T> element type
     * @param <U> mapped element type
     */
    public static <T, U> Gatherer<T, ?, T> filterBy(Function<T, U> mapper, U value, BiPredicate<U, U> predicate) {
        return new FilteringGatherer<>(mapper, value, predicate);
    }

    /**
     * Provides instance of {@link FilteringGatherer} that checks equality of the mapped element with the specific value.
     * Passed elements do NOT go down the stream.
     *
     * @param mapper mapping function
     * @param value specific value
     * @param <T> element type
     * @param <U> mapped element type
     */
    public static <T, U> Gatherer<T, ?, T> removeBy(Function<T, U> mapper, U value) {
        return new FilteringGatherer<>(mapper, value, true);
    }

    /**
     * Provides instance of {@link FilteringGatherer} that tests mapped element against the specific value with some predicate.
     * Passed elements do NOT go down the stream.
     *
     * @param mapper mapping function
     * @param value specific value
     * @param predicate testing predicate
     * @param <T> element type
     * @param <U> mapped element type
     */
    public static <T, U> Gatherer<T, ?, T> removeBy(Function<T, U> mapper, U value, BiPredicate<U, U> predicate) {
        return new FilteringGatherer<>(mapper, value, predicate, true);
    }

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
     * @return reverse gatherer
     * @param <T> element type
     */
    public static <T> Gatherer<T, ?, T> reverse() {
        return new IntoListGatherer<>(Collections::reverse);
    }

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
     * @return shuffle gatherer
     * @param <T> element type
     */
    public static <T> Gatherer<T, ?, T> shuffle() {
        return new IntoListGatherer<>(Collections::shuffle);
    }

    private Packrat() {}
}