package jhspetersson.packrat;

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
     * Provides instance of {@link FilteringGatherer} that tests mapped element against the specific value with some predicate
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
     * Provides instance of {@link FilteringGatherer} that tests mapped element against the specific value with some predicate
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
     * Provides {@link ReverseGatherer} instance
     *
     * @return reverse gatherer
     * @param <T> element type
     */
    public static <T> ReverseGatherer<T> reverse() {
        return new ReverseGatherer<>();
    }

    /**
     * Provides {@link ShuffleGatherer} instance
     *
     * @return shuffle gatherer
     * @param <T> element type
     */
    public static <T> ShuffleGatherer<T> shuffle() {
        return new ShuffleGatherer<>();
    }

    private Packrat() {}
}