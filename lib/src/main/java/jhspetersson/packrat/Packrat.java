package jhspetersson.packrat;

import java.text.BreakIterator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

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
    public static <T, U> Gatherer<T, ?, T> distinctBy(Function<? super T, ? extends U> mapper) {
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
    public static <T, U> Gatherer<T, ?, T> filterBy(Function<? super T, ? extends U> mapper, U value) {
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
    public static <T, U> Gatherer<T, ?, T> filterBy(Function<? super T, ? extends U> mapper, U value, BiPredicate<? super U, ? super U> predicate) {
        return new FilteringGatherer<>(mapper, value, predicate);
    }

    /**
     * Returns elements in an increasing sequence using natural order comparator.
     * Elements out of the sequence, as well as repeating values, are dropped.
     *
     * @param <T> element type
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, T> increasing() {
        return increasing(Comparator.naturalOrder());
    }

    /**
     * Returns elements in an increasing sequence using provided comparator.
     * Elements out of the sequence, as well as repeating values, are dropped.
     *
     * @param <T> element type
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, T> increasing(Comparator<? super T> comparator) {
        return new IncreasingDecreasingGatherer<>(comparator, cmp -> cmp < 0);
    }

    /**
     * Returns elements in an increasing sequence using natural order comparator.
     * Repeating values are preserved. Elements out of the sequence are dropped.
     *
     * @param <T> element type
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, T> increasingOrEqual() {
        return increasingOrEqual(Comparator.naturalOrder());
    }

    /**
     * Returns elements in an increasing sequence using provided comparator.
     * Repeating values are preserved. Elements out of the sequence are dropped.
     *
     * @param <T> element type
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, T> increasingOrEqual(Comparator<? super T> comparator) {
        return new IncreasingDecreasingGatherer<>(comparator, cmp -> cmp <= 0);
    }

    /**
     * Returns elements in a decreasing sequence using natural order comparator.
     * Elements out of the sequence, as well as repeating values, are dropped.
     *
     * @param <T> element type
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, T> decreasing() {
        return decreasing(Comparator.naturalOrder());
    }

    /**
     * Returns elements in a decreasing sequence using provided comparator.
     * Elements out of the sequence, as well as repeating values, are dropped.
     *
     * @param <T> element type
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, T> decreasing(Comparator<? super T> comparator) {
        return new IncreasingDecreasingGatherer<>(comparator, cmp -> cmp > 0);
    }

    /**
     * Returns elements in a decreasing sequence using natural order comparator.
     * Repeating values are preserved. Elements out of the sequence are dropped.
     *
     * @param <T> element type
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, T> decreasingOrEqual() {
        return decreasingOrEqual(Comparator.naturalOrder());
    }

    /**
     * Returns elements in a decreasing sequence using provided comparator.
     * Repeating values are preserved. Elements out of the sequence are dropped.
     *
     * @param <T> element type
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, T> decreasingOrEqual(Comparator<? super T> comparator) {
        return new IncreasingDecreasingGatherer<>(comparator, cmp -> cmp >= 0);
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
     * Returns all elements, the first element is mapped with the supplied mapping function.
     *
     * @param mapper mapping function
     * @param <T> element type
     */
    public static <T> Gatherer<T, ?, T> mapFirst(Function<? super T, ? extends T> mapper) {
        return new MappingGatherer<>(0L, 1L, mapper);
    }

    /**
     * Returns all elements, the first mapN elements are mapped with the supplied mapping function.
     *
     * @param mapper mapping function
     * @param <T> element type
     */
    public static <T> Gatherer<T, ?, T> mapN(long mapN, Function<? super T, ? extends T> mapper) {
        return new MappingGatherer<>(0L, mapN, mapper);
    }

    /**
     * Returns all elements that after the first skipN are mapped with the supplied mapping function.
     *
     * @param mapper mapping function
     * @param <T> element type
     */
    public static <T> Gatherer<T, ?, T> skipAndMap(long skipN, Function<? super T, ? extends T> mapper) {
        return new MappingGatherer<>(skipN, -1L, mapper);
    }

    /**
     * Returns all elements, after skipN elements the first mapN elements are mapped with the supplied mapping function.
     *
     * @param mapper mapping function
     * @param <T> element type
     */
    public static <T> Gatherer<T, ?, T> skipAndMapN(long skipN, long mapN, Function<? super T, ? extends T> mapper) {
        return new MappingGatherer<>(skipN, mapN, mapper);
    }

    public static <T> Gatherer<T, ?, T> mapWhihle(Function<? super T, ? extends T> mapper, Predicate<? super T> predicate) {
        return new MapWhileUntilGatherer<>(mapper, predicate);
    }

    public static <T> Gatherer<T, ?, T> mapUntil(Function<? super T, ? extends T> mapper, Predicate<? super T> predicate) {
        return new MapWhileUntilGatherer<>(mapper, null, predicate);
    }

    /**
     * Returns elements mapped ("zipped") with the values from some other iterable.
     *
     * @param input iterable
     * @param mapper zipping function
     * @param <T> element type
     * @param <U> supplied iterable element type
     * @param <V> result ("zipped") type
     */
    public static <T, U, V> Gatherer<T, ?, V> zip(Iterable<? extends U> input, BiFunction<? super T, ? super U, ? extends V> mapper) {
        return new ZipGatherer<>(input, mapper);
    }

    /**
     * Returns elements mapped ("zipped") with the values from some other stream.
     *
     * @param input stream
     * @param mapper zipping function
     * @param <T> element type
     * @param <U> supplied stream element type
     * @param <V> result ("zipped") type
     */
    public static <T, U, V> Gatherer<T, ?, V> zip(Stream<? extends U> input, BiFunction<? super T, ? super U, ? extends V> mapper) {
        return new ZipGatherer<>(input, mapper);
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
    public static <T, U> Gatherer<T, ?, T> removeBy(Function<? super T, ? extends U> mapper, U value) {
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
    public static <T, U> Gatherer<T, ?, T> removeBy(Function<? super T, ? extends U> mapper, U value, BiPredicate<? super U, ? super U> predicate) {
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

    /**
     * Provides the result of the supplied collector as a single element into the stream.
     * Effectively converts any Collector into a Gatherer.
     *
     * @param collector Collector
     * @param <T> element type
     * @param <U> state type
     * @param <V> result type
     */
    public static <T, U, V> Gatherer<T, U, V> asGatherer(Collector<? super T, U, ? extends V> collector) {
        return new CollectingGatherer<>(collector);
    }

    private Packrat() {}
}