package jhspetersson.packrat;

import java.text.BreakIterator;
import java.util.Collections;
import java.util.Locale;
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
     * Returns elements with distinct values that result from a mapping by the supplied function.
     *
     * @param mapper mapping function
     * @param <T> element type
     * @param <U> mapped element type
     */
    public static <T, U> Gatherer<T, ?, T> distinctBy(Function<T, U> mapper) {
        return new DistinctByGatherer<>(mapper);
    }

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
     * Returns n copies of every element.
     *
     * @param n how many copies, value less than or equal to zero effectively empties the stream.
     * @param <T> element type
     */
    public static <T> Gatherer<T, ?, T> nCopies(long n) {
        return new NCopiesGatherer<>(n);
    }

    /**
     * Returns characters as strings parsed from the stream elements.
     *
     * @param <T> element type
     */
    public static <T> Gatherer<T, ?, String> chars() {
        return new BreakingGatherer<>(BreakIterator.getCharacterInstance());
    }

    public static <T> Gatherer<T, ?, String> chars(Locale locale) {
        return new BreakingGatherer<>(BreakIterator.getCharacterInstance(locale));
    }

    /**
     * Returns words as strings parsed from the stream elements.
     *
     * @param <T> element type
     */
    public static <T> Gatherer<T, ?, String> words() {
        return new BreakingGatherer<>(BreakIterator.getWordInstance(), true);
    }

    public static <T> Gatherer<T, ?, String> words(Locale locale) {
        return new BreakingGatherer<>(BreakIterator.getWordInstance(locale), true);
    }

    /**
     * Returns sentences as strings parsed from the stream elements.
     *
     * @param <T> element type
     */
    public static <T> Gatherer<T, ?, String> sentences() {
        return new BreakingGatherer<>(BreakIterator.getSentenceInstance());
    }

    public static <T> Gatherer<T, ?, String> sentences(Locale locale) {
        return new BreakingGatherer<>(BreakIterator.getSentenceInstance(locale));
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
     * Rotates the element stream.
     * <p>
     *
     * <pre>
     *   var positiveRotation = IntStream.range(0, 10).boxed().gather(Packrat.rotate(3)).toList();
     *   System.out.println(positiveRotation);
     *
     *   [7, 8, 9, 0, 1, 2, 3, 4, 5, 6]
     * </pre>
     *
     * <pre>
     *   var negativeRotation = IntStream.range(0, 10).boxed().gather(Packrat.rotate(-4)).toList();
     *   System.out.println(negativeRotation);
     *
     *   [4, 5, 6, 7, 8, 9, 0, 1, 2, 3]
     * </pre>
     *
     * @see java.util.Collections#rotate
     * @param distance rotation distance, any number
     * @return rotation gatherer
     * @param <T> element type
     */
    public static <T> Gatherer<T, ?, T> rotate(int distance) {
        return new IntoListGatherer<>(list -> Collections.rotate(list, distance));
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