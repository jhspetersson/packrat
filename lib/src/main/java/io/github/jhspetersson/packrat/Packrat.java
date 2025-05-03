package io.github.jhspetersson.packrat;

import java.text.BreakIterator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
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
public final class Packrat {
    /**
     * Returns elements with distinct values that result from a mapping by the supplied function.
     *
     * @param mapper mapping function
     * @param <T> element type
     * @param <U> mapped element type
     * @return a gatherer that filters elements based on distinct mapped values
     * @throws NullPointerException if the mapper is null
     */
    public static <T, U> Gatherer<T, ?, T> distinctBy(Function<? super T, ? extends U> mapper) {
        return new DistinctByGatherer<>(mapper);
    }

    /**
     * Returns distinct elements that appear at least <code>n</code> times in the stream.
     *
     * @param n at least how many times the element has to appear in the stream
     * @param <T> element type
     * @return a gatherer that filters elements based on their frequency in the stream
     * @throws IllegalArgumentException if <code>n</code> is negative
     */
    public static <T> Gatherer<T, ?, T> atLeast(long n) {
        return atLeastBy(n, Function.identity());
    }

    /**
     * Returns distinct elements mapped by the supplied function that appear at least <code>n</code> times in the stream.
     *
     * @param n at least how many times the element has to appear in the stream
     * @param mapper mapping function
     * @param <T> element type
     * @param <U> mapped element type
     * @return a gatherer that filters elements based on the frequency of their mapped values in the stream
     * @throws IllegalArgumentException if <code>n</code> is negative
     * @throws NullPointerException if the mapper is null
     */
    public static <T, U> Gatherer<T, ?, T> atLeastBy(long n, Function<? super T, ? extends U> mapper) {
        return new AtLeastGatherer<>(n, mapper);
    }

    /**
     * Returns elements that appear at most <code>n</code> times in the stream.
     *
     * @param n at most how many times the element has to appear in the stream
     * @param <T> element type
     * @return a gatherer that filters elements based on their frequency in the stream
     * @throws IllegalArgumentException if <code>n</code> is negative
     */
    public static <T> Gatherer<T, ?, T> atMost(long n) {
        return atMostBy(n, Function.identity());
    }

    /**
     * Returns elements mapped by the supplied function that appear at most <code>n</code> times in the stream.
     *
     * @param n at most how many times the element has to appear in the stream
     * @param mapper mapping function
     * @param <T> element type
     * @param <U> mapped element type
     * @return a gatherer that filters elements based on the frequency of their mapped values in the stream
     * @throws IllegalArgumentException if <code>n</code> is negative
     * @throws NullPointerException if the mapper is null
     */
    public static <T, U> Gatherer<T, ?, T> atMostBy(long n, Function<? super T, ? extends U> mapper) {
        return new AtMostGatherer<>(n, mapper);
    }

    /**
     * Provides instance of {@link FilteringGatherer} that checks equality of the mapped element with the specific value.
     * Passed elements unmodified go down the stream.
     *
     * @param mapper mapping function
     * @param value specific value
     * @param <T> element type
     * @param <U> mapped element type
     * @return a gatherer that filters elements based on equality of their mapped values with the specific value
     * @throws NullPointerException if the mapper is null
     */
    public static <T, U> Gatherer<T, ?, T> filterBy(Function<? super T, ? extends U> mapper, U value) {
        return filterBy(mapper, value, Objects::equals);
    }

    /**
     * Provides instance of {@link FilteringGatherer} that tests a mapped element against the specific value with some predicate.
     * Passed elements unmodified go down the stream.
     *
     * @param mapper mapping function
     * @param value specific value
     * @param predicate testing predicate
     * @param <T> element type
     * @param <U> mapped element type
     * @return a gatherer that filters elements
     * based on testing their mapped values against the specific value with the provided predicate
     * @throws NullPointerException if the mapper or predicate is null
     */
    public static <T, U> Gatherer<T, ?, T> filterBy(Function<? super T, ? extends U> mapper, U value, BiPredicate<? super U, ? super U> predicate) {
        return new FilteringGatherer<>(mapper, value, predicate);
    }

    /**
     * Outputs the smallest element in the stream.
     * Comparing is done after the mapping function is applied.
     *
     * @param mapper mapping function
     * @param <T> element type
     * @param <U> mapped element type
     * @return a gatherer that outputs the smallest element in the stream based on the mapped values
     * @throws NullPointerException if the mapper is null
     */
    public static <T, U extends Comparable<U>> Gatherer<T, ?, T> minBy(Function<? super T, ? extends U> mapper) {
        return minBy(mapper, Comparator.naturalOrder());
    }

    /**
     * Outputs the smallest element in the stream.
     * Comparing with a given comparator is done after the mapping function is applied.
     *
     * @param mapper mapping function
     * @param comparator comparator
     * @param <T> element type
     * @param <U> mapped element type
     * @return a gatherer that outputs the smallest element in the stream based on the mapped values and the provided comparator
     * @throws NullPointerException if the mapper or comparator is null
     */
    public static <T, U extends Comparable<U>> Gatherer<T, ?, T> minBy(Function<? super T, ? extends U> mapper, Comparator<? super U> comparator) {
        return new MinMaxGatherer<>(mapper, comparator, cmp -> cmp < 0);
    }

    /**
     * Outputs the greatest element in the stream.
     * Comparing is done after the mapping function is applied.
     *
     * @param mapper mapping function
     * @param <T> element type
     * @param <U> mapped element type
     * @return a gatherer that outputs the greatest element in the stream based on the mapped values
     * @throws NullPointerException if the mapper is null
     */
    public static <T, U extends Comparable<U>> Gatherer<T, ?, T> maxBy(Function<? super T, ? extends U> mapper) {
        return maxBy(mapper, Comparator.naturalOrder());
    }

    /**
     * Outputs the greatest element in the stream.
     * Comparing with a given comparator is done after the mapping function is applied.
     *
     * @param mapper mapping function
     * @param comparator comparator
     * @param <T> element type
     * @param <U> mapped element type
     * @return a gatherer that outputs the greatest element in the stream based on the mapped values and the provided comparator
     * @throws NullPointerException if the mapper or comparator is null
     */
    public static <T, U extends Comparable<U>> Gatherer<T, ?, T> maxBy(Function<? super T, ? extends U> mapper, Comparator<? super U> comparator) {
        return new MinMaxGatherer<>(mapper, comparator, cmp -> cmp > 0);
    }

    /**
     * Returns elements in an increasing sequence using natural order comparator.
     * Elements out of the sequence, as well as repeating values, are dropped.
     *
     * @param <T> element type
     * @return a gatherer that filters elements to form an increasing sequence
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, T> increasing() {
        return increasing(Comparator.naturalOrder());
    }

    /**
     * Returns elements in an increasing sequence using the provided comparator.
     * Elements out of the sequence, as well as repeating values, are dropped.
     *
     * @param comparator comparator
     * @param <T> element type
     * @return a gatherer that filters elements to form an increasing sequence using the provided comparator
     * @throws NullPointerException if the comparator is null
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, T> increasing(Comparator<? super T> comparator) {
        return new IncreasingDecreasingGatherer<>(comparator, cmp -> cmp < 0);
    }

    /**
     * Returns elements in an increasing sequence using natural order comparator.
     * Repeating values are preserved. Elements out of the sequence are dropped.
     *
     * @param <T> element type
     * @return a gatherer that filters elements to form an increasing sequence, preserving repeating values
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, T> increasingOrEqual() {
        return increasingOrEqual(Comparator.naturalOrder());
    }

    /**
     * Returns elements in an increasing sequence using the provided comparator.
     * Repeating values are preserved. Elements out of the sequence are dropped.
     *
     * @param comparator comparator
     * @param <T> element type
     * @return a gatherer that filters elements to form an increasing sequence using the provided comparator,
     * preserving repeating values
     * @throws NullPointerException if the comparator is null
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, T> increasingOrEqual(Comparator<? super T> comparator) {
        return new IncreasingDecreasingGatherer<>(comparator, cmp -> cmp <= 0);
    }

    /**
     * Returns elements in a decreasing sequence using natural order comparator.
     * Elements out of the sequence, as well as repeating values, are dropped.
     *
     * @param <T> element type
     * @return a gatherer that filters elements to form a decreasing sequence
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, T> decreasing() {
        return decreasing(Comparator.naturalOrder());
    }

    /**
     * Returns elements in a decreasing sequence using the provided comparator.
     * Elements out of the sequence, as well as repeating values, are dropped.
     *
     * @param comparator comparator
     * @param <T> element type
     * @return a gatherer that filters elements to form a decreasing sequence using the provided comparator
     * @throws NullPointerException if the comparator is null
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, T> decreasing(Comparator<? super T> comparator) {
        return new IncreasingDecreasingGatherer<>(comparator, cmp -> cmp > 0);
    }

    /**
     * Returns elements in a decreasing sequence using natural order comparator.
     * Repeating values are preserved. Elements out of the sequence are dropped.
     *
     * @param <T> element type
     * @return a gatherer that filters elements to form a decreasing sequence, preserving repeating values
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, T> decreasingOrEqual() {
        return decreasingOrEqual(Comparator.naturalOrder());
    }

    /**
     * Returns elements in a decreasing sequence using the provided comparator.
     * Repeating values are preserved. Elements out of the sequence are dropped.
     *
     * @param comparator comparator
     * @param <T> element type
     * @return a gatherer that filters elements to form a decreasing sequence using the provided comparator,
     * preserving repeating values
     * @throws NullPointerException if the comparator is null
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, T> decreasingOrEqual(Comparator<? super T> comparator) {
        return new IncreasingDecreasingGatherer<>(comparator, cmp -> cmp >= 0);
    }

    /**
     * Returns lists ("chunks") of elements, where each next element is greater than the previous one.
     * Comparison is done with the natural order comparator.
     *
     * @param <T> element type
     * @return a gatherer that groups elements into lists where each element is greater than the previous one
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, List<T>> increasingChunks() {
        return increasingChunks(Comparator.naturalOrder());
    }

    /**
     * Returns lists ("chunks") of elements, where each next element is greater than the previous one.
     * Comparison is done with the supplied comparator.
     *
     * @param comparator comparator
     * @param <T> element type
     * @return a gatherer that groups elements into lists where each element is greater than the previous one,
     * using the provided comparator
     * @throws NullPointerException if the comparator is null
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, List<T>> increasingChunks(Comparator<? super T> comparator) {
        return new IncreasingDecreasingChunksGatherer<>(comparator, cmp -> cmp < 0);
    }

    /**
     * Returns lists ("chunks") of elements, where each next element is greater or equal than the previous one.
     * Comparison is done with the natural order comparator.
     *
     * @param <T> element type
     * @return a gatherer that groups elements into lists where each element is greater or equal than the previous one
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, List<T>> increasingOrEqualChunks() {
        return increasingOrEqualChunks(Comparator.naturalOrder());
    }

    /**
     * Returns lists ("chunks") of elements, where each next element is greater than the previous one.
     * Comparison is done with the supplied comparator.
     *
     * @param comparator comparator
     * @param <T> element type
     * @return a gatherer that groups elements into lists where each element is greater or equal than the previous one,
     * using the provided comparator
     * @throws NullPointerException if the comparator is null
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, List<T>> increasingOrEqualChunks(Comparator<? super T> comparator) {
        return new IncreasingDecreasingChunksGatherer<>(comparator, cmp -> cmp <= 0);
    }

    /**
     * Returns lists ("chunks") of elements where all elements in a chunk are equal.
     *
     * @param <T> element type
     * @return a gatherer that groups elements into lists where all elements are equal
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, List<T>> equalChunks() {
        return equalChunksBy(Function.identity());
    }

    /**
     * Returns lists ("chunks") of elements where all elements in a chunk are equal after applying the mapping function.
     *
     * @param mapper mapping function
     * @param <T> element type
     * @param <U> mapped element type
     * @return a gatherer that groups elements into lists where all elements are equal
     * after applying the mapping function
     * @throws NullPointerException if the mapper is null
     */
    public static <T, U> Gatherer<T, ?, List<T>> equalChunksBy(Function<? super T, ? extends U> mapper) {
        return new EqualChunksGatherer<>(mapper);
    }

    /**
     * Returns lists ("chunks") of elements where all elements in a chunk are equal after applying the mapping function.
     * Comparison is done with the supplied comparator.
     *
     * @param mapper mapping function
     * @param comparator comparator for comparing mapped values
     * @param <T> element type
     * @param <U> mapped element type
     * @return a gatherer that groups elements into lists where all elements are equal
     * after applying the mapping function, using the provided comparator
     * @throws NullPointerException if the mapper or comparator is null
     */
    public static <T, U> Gatherer<T, ?, List<T>> equalChunksBy(Function<? super T, ? extends U> mapper, Comparator<? super U> comparator) {
        return new EqualChunksGatherer<>(mapper, comparator);
    }

    /**
     * Returns lists ("chunks") of elements where all elements in a chunk are equal.
     * Comparison is done with the supplied comparator.
     *
     * @param comparator comparator for comparing elements
     * @param <T> element type
     * @return a gatherer that groups elements into lists where all elements are equal,
     * using the provided comparator
     * @throws NullPointerException if the comparator is null
     */
    public static <T> Gatherer<T, ?, List<T>> equalChunks(Comparator<? super T> comparator) {
        return new EqualChunksGatherer<>(Function.identity(), comparator);
    }

    /**
     * Returns lists ("chunks") of elements, where each next element is less than the previous one.
     * Comparison is done with the natural order comparator.
     *
     * @param <T> element type
     * @return a gatherer that groups elements into lists where each element is less than the previous one
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, List<T>> decreasingChunks() {
        return decreasingChunks(Comparator.naturalOrder());
    }

    /**
     * Returns lists ("chunks") of elements, where each next element is less than the previous one.
     * Comparison is done with the supplied comparator.
     *
     * @param comparator comparator
     * @param <T> element type
     * @return a gatherer that groups elements into lists where each element is less than the previous one,
     * using the provided comparator
     * @throws NullPointerException if the comparator is null
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, List<T>> decreasingChunks(Comparator<? super T> comparator) {
        return new IncreasingDecreasingChunksGatherer<>(comparator, cmp -> cmp > 0);
    }

    /**
     * Returns lists ("chunks") of elements, where each next element is less or equal than the previous one.
     * Comparison is done with the natural order comparator.
     *
     * @param <T> element type
     * @return a gatherer that groups elements into lists where each element is less or equal than the previous one
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, List<T>> decreasingOrEqualChunks() {
        return decreasingOrEqualChunks(Comparator.naturalOrder());
    }

    /**
     * Returns lists ("chunks") of elements, where each next element is less or equal than the previous one.
     * Comparison is done with the supplied comparator.
     *
     * @param comparator comparator
     * @param <T> element type
     * @return a gatherer that groups elements into lists where each element is less or equal than the previous one,
     * using the provided comparator
     * @throws NullPointerException if the comparator is null
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, List<T>> decreasingOrEqualChunks(Comparator<? super T> comparator) {
        return new IncreasingDecreasingChunksGatherer<>(comparator, cmp -> cmp >= 0);
    }

    /**
     * Returns <code>n</code> copies of every element.
     *
     * @param n how many copies, value equal to zero effectively empties the stream.
     * @param <T> element type
     * @return a gatherer that produces <code>n</code> copies of each element in the stream
     * @throws IllegalArgumentException if <code>n</code> is negative
     */
    public static <T> Gatherer<T, ?, T> nCopies(long n) {
        return new NCopiesGatherer<>(n);
    }

    /**
     * Collects the whole stream and repeats it <code>n</code> times.
     *
     * @param n how many times to repeat the stream, value equal to zero effectively empties the stream
     * @param <T> element type
     * @return a gatherer that collects the whole stream and repeats it <code>n</code> times
     * @throws IllegalArgumentException if <code>n</code> is negative
     */
    public static <T> Gatherer<T, ?, T> repeat(long n) {
        return new RepeatGatherer<>(n);
    }

    /**
     * Returns all elements, the first element is mapped with the supplied mapping function.
     *
     * @param mapper mapping function
     * @param <T> element type
     * @return a gatherer that maps only the first element in the stream using the provided function
     * @throws NullPointerException if the mapper is null
     */
    public static <T> Gatherer<T, ?, T> mapFirst(Function<? super T, ? extends T> mapper) {
        return skipAndMapN(0L, 1L, mapper);
    }

    /**
     * Returns all elements, the first <code>mapN</code> elements are mapped with the supplied mapping function.
     *
     * @param mapN number of elements to map
     * @param mapper mapping function
     * @param <T> element type
     * @return a gatherer that maps the first <code>mapN</code> elements in the stream using the provided function
     * @throws NullPointerException if the mapper is null
     */
    public static <T> Gatherer<T, ?, T> mapN(long mapN, Function<? super T, ? extends T> mapper) {
        return skipAndMapN(0L, mapN, mapper);
    }

    /**
     * Returns all elements that after the first <code>skipN</code> are mapped with the supplied mapping function.
     *
     * @param skipN number of elements to skip before mapping
     * @param mapper mapping function
     * @param <T> element type
     * @return a gatherer that maps all elements after skipping the first <code>skipN</code> elements
     * @throws NullPointerException if the mapper is null
     */
    public static <T> Gatherer<T, ?, T> skipAndMap(long skipN, Function<? super T, ? extends T> mapper) {
        return skipAndMapN(skipN, -1L, mapper);
    }

    /**
     * Returns all elements, after <code>skipN</code> elements the first <code>mapN</code> elements are mapped with the supplied mapping function.
     *
     * @param skipN number of elements to skip before mapping
     * @param mapN number of elements to map after skipping
     * @param mapper mapping function
     * @param <T> element type
     * @return a gatherer that maps <code>mapN</code> elements after skipping <code>skipN</code> elements
     * @throws NullPointerException if the mapper is null
     */
    public static <T> Gatherer<T, ?, T> skipAndMapN(long skipN, long mapN, Function<? super T, ? extends T> mapper) {
        return new MappingGatherer<>(skipN, mapN, mapper);
    }

    /**
     * Maps elements using the supplied mapping function while the predicate evaluates to true.
     * Once the predicate evaluates to false, no further elements are mapped, and the original elements are passed downstream.
     *
     * @param mapper mapping function
     * @param predicate predicate to determine when to stop mapping
     * @param <T> element type
     * @return a gatherer that maps elements while the predicate evaluates to true
     * @throws NullPointerException if the mapper or predicate is null
     */
    public static <T> Gatherer<T, ?, T> mapWhile(Function<? super T, ? extends T> mapper, Predicate<? super T> predicate) {
        return new MapWhileUntilGatherer<>(mapper, predicate);
    }

    /**
     * Maps elements using the supplied mapping function until the predicate evaluates to true.
     * Once the predicate evaluates to true, no further elements are mapped, and the original elements are passed downstream.
     *
     * @param mapper mapping function
     * @param predicate predicate to determine when to stop mapping
     * @param <T> element type
     * @return a gatherer that maps elements until the predicate evaluates to true
     * @throws NullPointerException if the mapper or predicate is null
     */
    public static <T> Gatherer<T, ?, T> mapUntil(Function<? super T, ? extends T> mapper, Predicate<? super T> predicate) {
        return new MapWhileUntilGatherer<>(mapper, null, predicate);
    }

    /**
     * Optionally flattens elements mapped to streams depending on the supplied predicate.
     *
     * @param mapper mapping function, usually calls <code>element::stream</code> method
     * @param predicate deciding predicate when to call the mapping function
     * @param <T> element type
     * @return a gatherer that optionally flattens elements based on the predicate
     * @throws NullPointerException if the mapper or predicate is null
     */
    public static <T> Gatherer<T, ?, ?> flatMapIf(Function<? super T, Stream<? extends T>> mapper, Predicate<? super T> predicate) {
        return new FlatMapGatherer<>(mapper, predicate);
    }

    /**
     * Returns map entries from elements of the stream mapped ("zipped") with the values from some other iterable.
     *
     * @param input iterable
     * @param <T> element type
     * @param <U> supplied iterable element type
     * @return a gatherer that produces map entries from zipping stream elements with the values from the iterable source
     * @throws NullPointerException if the input iterable is null
     * @see java.util.Map.Entry
     */
    public static <T, U> Gatherer<T, ?, Map.Entry<T, ? extends U>> zip(Iterable<? extends U> input) {
        return zip(input, Map::entry);
    }

    /**
     * Returns elements mapped ("zipped") with the values from some other iterable.
     *
     * @param input iterable
     * @param mapper zipping function
     * @param <T> element type
     * @param <U> supplied iterable element type
     * @param <V> result ("zipped") type
     * @return a gatherer that produces elements by zipping stream elements with the values from the iterable source using the mapper function
     * @throws NullPointerException if the input iterable or mapper is null
     */
    public static <T, U, V> Gatherer<T, ?, V> zip(Iterable<? extends U> input, BiFunction<? super T, ? super U, ? extends V> mapper) {
        return new ZipGatherer<>(input, mapper);
    }

    /**
     * Returns map entries from elements of the stream mapped ("zipped") with the values from some other stream.
     *
     * @param input iterable
     * @param <T> element type
     * @param <U> supplied iterable element type
     * @return a gatherer that produces map entries from zipping stream elements with another stream's values
     * @throws NullPointerException if the input stream is null
     * @see java.util.Map.Entry
     */
    public static <T, U> Gatherer<T, ?, Map.Entry<T, ? extends U>> zip(Stream<? extends U> input) {
        return zip(input, Map::entry);
    }

    /**
     * Returns elements mapped ("zipped") with the values from some other stream.
     *
     * @param input stream
     * @param mapper zipping function
     * @param <T> element type
     * @param <U> supplied stream element type
     * @param <V> result ("zipped") type
     * @return a gatherer that produces elements by zipping stream elements with another stream's values using the mapper function
     * @throws NullPointerException if the input stream or mapper is null
     */
    public static <T, U, V> Gatherer<T, ?, V> zip(Stream<? extends U> input, BiFunction<? super T, ? super U, ? extends V> mapper) {
        return new ZipGatherer<>(input, mapper);
    }

    /**
     * Returns map entries from elements of the stream mapped ("zipped") with the values from some other iterator.
     *
     * @param iterator iterator
     * @param <T> element type
     * @param <U> supplied iterable element type
     * @return a gatherer that produces map entries from zipping stream elements with iterator values
     * @throws NullPointerException if the iterator is null
     * @see java.util.Map.Entry
     */
    public static <T, U> Gatherer<T, ?, Map.Entry<T, ? extends U>> zip(Iterator<? extends U> iterator) {
        return zip(iterator, Map::entry);
    }

    /**
     * Returns elements mapped ("zipped") with the values from some other iterator.
     *
     * @param iterator iterator
     * @param mapper zipping function
     * @param <T> element type
     * @param <U> supplied stream element type
     * @param <V> result ("zipped") type
     * @return a gatherer that produces elements by zipping stream elements with iterator values using the mapper function
     * @throws NullPointerException if the iterator or mapper is null
     */
    public static <T, U, V> Gatherer<T, ?, V> zip(Iterator<? extends U> iterator, BiFunction<? super T, ? super U, ? extends V> mapper) {
        return new ZipGatherer<>(iterator, mapper);
    }

    /**
     * Returns elements mapped ("zipped") with an increasing index.
     * Output type is {@link java.util.Map.Entry} with a Long key and an element as a value.
     *
     * @param <T> element type
     * @return a gatherer that produces map entries from zipping stream elements with their indices
     * @see java.util.Map.Entry
     */
    public static <T> Gatherer<T, ?, Map.Entry<Long, ? extends T>> zipWithIndex() {
        return zipWithIndex(Map::entry);
    }

    /**
     * Returns elements mapped ("zipped") with an increasing index.
     * Output type is {@link java.util.Map.Entry} with a Long key and an element as a value.
     *
     * @param startIndex starting index
     * @param <T> element type
     * @return a gatherer that produces map entries from zipping stream elements with their indices starting from the specified index
     * @see java.util.Map.Entry
     */
    public static <T> Gatherer<T, ?, Map.Entry<Long, ? extends T>> zipWithIndex(long startIndex) {
        return zipWithIndex(Map::entry, startIndex);
    }

    /**
     * Returns elements mapped ("zipped") with an increasing index.
     * Mapping function receives the index as the first argument.
     *
     * @param mapper zipping function
     * @param <T> element type
     * @param <U> result ("zipped") type
     * @return a gatherer that produces elements by zipping stream elements with their indices using the mapper function
     * @throws NullPointerException if the mapper is null
     */
    public static <T, U> Gatherer<T, ?, U> zipWithIndex(BiFunction<Long, ? super T, ? extends U> mapper) {
        return zipWithIndex(mapper, 0L);
    }

    /**
     * Returns elements mapped ("zipped") with an increasing index.
     * Mapping function receives the index as the first argument.
     *
     * @param mapper zipping function
     * @param startIndex starting index
     * @param <T> element type
     * @param <U> result ("zipped") type
     * @return a gatherer
     * that produces elements by zipping stream elements with their indices starting from the specified index
     * using the mapper function
     * @throws NullPointerException if the mapper is null
     */
    public static <T, U> Gatherer<T, ?, U> zipWithIndex(BiFunction<Long, ? super T, ? extends U> mapper, long startIndex) {
        return new ZipWithIndexGatherer<>(mapper, startIndex);
    }

    /**
     * Returns elements mapped with an increasing index.
     * Mapping function receives the index as the first argument.
     * This is a synonym for {@link #zipWithIndex(BiFunction)}.
     *
     * @param mapper mapping function
     * @param <T> element type
     * @param <U> result type
     * @return a gatherer that produces elements by mapping stream elements with their indices using the mapper function
     * @throws NullPointerException if the mapper is null
     */
    public static <T, U> Gatherer<T, ?, U> mapWithIndex(BiFunction<Long, ? super T, ? extends U> mapper) {
        return zipWithIndex(mapper);
    }

    /**
     * Returns elements mapped with an increasing index.
     * Mapping function receives the index as the first argument.
     * This is a synonym for {@link #zipWithIndex(BiFunction, long)}.
     *
     * @param mapper mapping function
     * @param startIndex starting index
     * @param <T> element type
     * @param <U> result type
     * @return a gatherer
     * that produces elements by mapping stream elements with their indices starting from the specified index
     * using the mapper function
     * @throws NullPointerException if the mapper is null
     */
    public static <T, U> Gatherer<T, ?, U> mapWithIndex(BiFunction<Long, ? super T, ? extends U> mapper, long startIndex) {
        return zipWithIndex(mapper, startIndex);
    }

    /**
     * Returns characters as strings parsed from the stream elements.
     *
     * @param <T> element type
     * @return a gatherer that produces characters as strings from the stream elements
     */
    public static <T> Gatherer<T, ?, String> chars() {
        return new BreakingGatherer<>(BreakIterator.getCharacterInstance());
    }

    /**
     * Returns characters as strings parsed from the stream elements using the specified locale.
     *
     * @param locale the locale to use for character breaking
     * @param <T> element type
     * @return a gatherer that produces characters as strings from the stream elements using the specified locale
     * @throws NullPointerException if the locale is null
     */
    public static <T> Gatherer<T, ?, String> chars(Locale locale) {
        return new BreakingGatherer<>(BreakIterator.getCharacterInstance(locale));
    }

    /**
     * Returns words as strings parsed from the stream elements.
     *
     * @param <T> element type
     * @return a gatherer that produces words as strings from the stream elements
     */
    public static <T> Gatherer<T, ?, String> words() {
        return new BreakingGatherer<>(BreakIterator.getWordInstance(), true);
    }

    /**
     * Returns words as strings parsed from the stream elements using the specified locale.
     *
     * @param locale the locale to use for word breaking
     * @param <T> element type
     * @return a gatherer that produces words as strings from the stream elements using the specified locale
     * @throws NullPointerException if the locale is null
     */
    public static <T> Gatherer<T, ?, String> words(Locale locale) {
        return new BreakingGatherer<>(BreakIterator.getWordInstance(locale), true);
    }

    /**
     * Returns sentences as strings parsed from the stream elements.
     *
     * @param <T> element type
     * @return a gatherer that produces sentences as strings from the stream elements
     */
    public static <T> Gatherer<T, ?, String> sentences() {
        return new BreakingGatherer<>(BreakIterator.getSentenceInstance());
    }

    /**
     * Returns sentences as strings parsed from the stream elements using the specified locale.
     *
     * @param locale the locale to use for sentence breaking
     * @param <T> element type
     * @return a gatherer that produces sentences as strings from the stream elements using the specified locale
     * @throws NullPointerException if the locale is null
     */
    public static <T> Gatherer<T, ?, String> sentences(Locale locale) {
        return new BreakingGatherer<>(BreakIterator.getSentenceInstance(locale));
    }

    /**
     * Peeks at each element along with its index but passes the original element downstream.
     * The index starts from 0.
     *
     * @param consumer consumer function that accepts index and element
     * @param <T> element type
     * @return a gatherer that peeks at each element with its index and passes the original element downstream
     * @throws NullPointerException if the consumer is null
     */
    public static <T> Gatherer<T, ?, T> peekWithIndex(BiConsumer<Long, ? super T> consumer) {
        return peekWithIndex(consumer, 0);
    }

    /**
     * Peeks at each element along with its index but passes the original element downstream.
     *
     * @param consumer consumer function that accepts index and element
     * @param startIndex starting index
     * @param <T> element type
     * @return a gatherer that peeks at each element with its index starting from the specified index
     * and passes the original element downstream
     * @throws NullPointerException if the consumer is null
     */
    public static <T> Gatherer<T, ?, T> peekWithIndex(BiConsumer<Long, ? super T> consumer, long startIndex) {
        return new PeekWithIndexGatherer<>(consumer, startIndex);
    }

    /**
     * Filters elements based on their index and a predicate.
     * The index starts from 0.
     *
     * @param predicate predicate function that accepts index and element
     * @param <T> element type
     * @return a gatherer that filters elements based on their index and the predicate
     * @throws NullPointerException if the predicate is null
     */
    public static <T> Gatherer<T, ?, T> filterWithIndex(BiPredicate<Long, ? super T> predicate) {
        return filterWithIndex(predicate, 0);
    }

    /**
     * Filters elements based on their index and a predicate.
     *
     * @param predicate predicate function that accepts index and element
     * @param startIndex starting index
     * @param <T> element type
     * @return a gatherer that filters elements based on their index starting from the specified index and the predicate
     * @throws NullPointerException if the predicate is null
     */
    public static <T> Gatherer<T, ?, T> filterWithIndex(BiPredicate<Long, ? super T> predicate, long startIndex) {
        return new FilteringWithIndexGatherer<>(predicate, startIndex);
    }

    /**
     * Removes elements based on their index and a predicate.
     * The index starts from 0.
     *
     * @param predicate predicate function that accepts index and element
     * @param <T> element type
     * @return a gatherer that removes elements based on their index and the predicate
     * @throws NullPointerException if the predicate is null
     */
    public static <T> Gatherer<T, ?, T> removeWithIndex(BiPredicate<Long, ? super T> predicate) {
        return removeWithIndex(predicate, 0);
    }

    /**
     * Removes elements based on their index and a predicate.
     *
     * @param predicate predicate function that accepts index and element
     * @param startIndex starting index
     * @param <T> element type
     * @return a gatherer that removes elements based on their index starting from the specified index and the predicate
     * @throws NullPointerException if the predicate is null
     */
    public static <T> Gatherer<T, ?, T> removeWithIndex(BiPredicate<Long, ? super T> predicate, long startIndex) {
        return new FilteringWithIndexGatherer<>(predicate, startIndex, true);
    }

    /**
     * Provides instance of {@link FilteringGatherer} that checks equality of the mapped element with the specific value.
     * Passed elements do NOT go down the stream.
     *
     * @param mapper mapping function
     * @param value specific value
     * @param <T> element type
     * @param <U> mapped element type
     * @return a gatherer that removes elements based on equality of their mapped values with the specific value
     * @throws NullPointerException if the mapper is null
     */
    public static <T, U> Gatherer<T, ?, T> removeBy(Function<? super T, ? extends U> mapper, U value) {
        return removeBy(mapper, value, Objects::equals);
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
     * @return a gatherer
     * that removes elements based on testing their mapped values against the specific value with the provided predicate
     * @throws NullPointerException if the mapper or predicate is null
     */
    public static <T, U> Gatherer<T, ?, T> removeBy(Function<? super T, ? extends U> mapper, U value, BiPredicate<? super U, ? super U> predicate) {
        return new FilteringGatherer<>(mapper, value, predicate, true);
    }

    /**
     * Removes consecutive duplicates from the stream.
     * Only adjacent elements that have equal mapped values will be considered duplicates.
     *
     * <pre>
     *   var listWithCopies = List.of(0, 1, 2, 2, 3, 4, 5, 5, 6, 7, 8, 8, 8, 9, 8, 7, 7, 6, 5, 4, 4, 4, 3, 2, 1, 0);
     *   var unique = listWithCopies.stream().gather(removeDuplicates()).toList();
     *   System.out.println(unique);
     *
     *   [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0]
     * </pre>
     *
     * @param <T> element type
     * @return a gatherer that removes consecutive duplicate elements from the stream
     */
    public static <T> Gatherer<T, ?, T> removeDuplicates() {
        return removeDuplicatesBy(Function.identity());
    }

    /**
     * Removes consecutive duplicates from the stream based on a mapping function.
     * Only adjacent elements that have equal mapped values will be considered duplicates.
     *
     * @param mapper mapping function
     * @param <T> element type
     * @param <U> mapped element type
     * @return a gatherer that removes consecutive duplicate elements from the stream based on their mapped values
     * @throws NullPointerException if the mapper is null
     */
    public static <T, U> Gatherer<T, ?, T> removeDuplicatesBy(Function<? super T, ? extends U> mapper) {
        return new RemoveDuplicatesGatherer<>(mapper);
    }

    /**
     * Reverses the element stream.
     *
     * <pre>
     *   var reverseOrdered = IntStream.range(0, 10).boxed().gather(reverse()).toList();
     *   System.out.println(reverseOrdered);
     *
     *   [9, 8, 7, 6, 5, 4, 3, 2, 1, 0]
     * </pre>
     *
     * @param <T> element type
     * @return reverse gatherer
     */
    public static <T> Gatherer<T, ?, T> reverse() {
        return new IntoListGatherer<>(Collections::reverse);
    }

    /**
     * Rotates the element stream.
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
     * @param <T> element type
     * @return rotation gatherer
     */
    public static <T> Gatherer<T, ?, T> rotate(int distance) {
        return new IntoListGatherer<>(list -> Collections.rotate(list, distance));
    }

    /**
     * Shuffles the element stream.
     *
     * <pre>
     *   var randomlyOrdered = IntStream.range(0, 10).boxed().gather(shuffle()).toList();
     *   System.out.println(randomlyOrdered);
     *
     *   [2, 7, 6, 9, 8, 5, 1, 3, 0, 4]
     * </pre>
     *
     * @param <T> element type
     * @return shuffle gatherer
     */
    public static <T> Gatherer<T, ?, T> shuffle() {
        return new IntoListGatherer<>(Collections::shuffle);
    }

    /**
     * Returns a sample of the specified size from the stream of elements.
     *
     * @param n sample size
     * @param <T> element type
     * @return sampling gatherer
     */
    public static <T> Gatherer<T, ?, T> sample(int n) {
        var maxSpan = n < 512
                ? 1024
                : n < Integer.MAX_VALUE / 2
                    ? n * 2
                    : n + (Integer.MAX_VALUE - n) / 2;
        return sample(n, maxSpan);
    }

    /**
     * Returns a sample of the specified size from the stream of elements.
     *
     * @param n sample size
     * @param maxSpan maximum count of the elements to inspect
     * @param <T> element type
     * @return sampling gatherer
     * @throws IllegalArgumentException if <code>n</code> is negative
     * @throws IllegalArgumentException if <code>maxSpan</code> is not positive
     * @throws IllegalArgumentException if <code>maxSpan</code> is less or equal <code>n</code>
     */
    public static <T> Gatherer<T, ?, T> sample(int n, int maxSpan) {
        return new SamplingGatherer<>(n, maxSpan);
    }

    /**
     * Returns every nth element from the stream.
     *
     * @param n take every nth element
     * @param <T> element type
     * @return a gatherer that outputs every nth element from the stream
     * @throws IllegalArgumentException if <code>n</code> is less than or equal to zero
     */
    public static <T> Gatherer<T, ?, T> nth(int n) {
        return new NthGatherer<>(n);
    }

    /**
     * Drops every nth element from the stream.
     *
     * @param n drop every nth element
     * @param <T> element type
     * @return a gatherer that drops every nth element from the stream
     * @throws IllegalArgumentException if <code>n</code> is less than or equal to zero
     */
    public static <T> Gatherer<T, ?, T> dropNth(int n) {
        return new DropNthGatherer<>(n);
    }

    /**
     * Returns last element.
     *
     * @param <T> element type
     * @return a gatherer that returns the last element from the stream
     */
    public static <T> Gatherer<T, ?, T> last() {
        return last(1);
    }

    /**
     * Returns last <code>n</code> elements.
     *
     * @param n count of last elements to return
     * @param <T> element type
     * @return a gatherer that returns the last <code>n</code> elements from the stream
     * @throws IllegalArgumentException if <code>n</code> is negative
     */
    public static <T> Gatherer<T, ?, T> last(long n) {
        return new LastingGatherer<>(n);
    }

    /**
     * Returns last <code>n</code> unique elements.
     *
     * @param n count of last unique elements to return
     * @param <T> element type
     * @return a gatherer that returns the last <code>n</code> unique elements from the stream
     * @throws IllegalArgumentException if <code>n</code> is negative
     */
    public static <T> Gatherer<T, ?, T> lastUnique(long n) {
        return new LastingGatherer<>(n, true);
    }

    /**
     * Drops last element.
     *
     * @param <T> element type
     * @return a gatherer that drops the last element from the stream
     */
    public static <T> Gatherer<T, ?, T> dropLast() {
        return dropLast(1);
    }

    /**
     * Drops last <code>n</code> elements.
     *
     * @param n count of last elements to drop
     * @param <T> element type
     * @return a gatherer that drops the last <code>n</code> elements from the stream
     * @throws IllegalArgumentException if <code>n</code> is negative
     */
    public static <T> Gatherer<T, ?, T> dropLast(long n) {
        return new DropLastNGatherer<>(n);
    }

    /**
     * Provides the result of the supplied collector as a single element into the stream.
     * Effectively converts any Collector into a Gatherer.
     *
     * @param collector Collector
     * @param <T> element type
     * @param <U> state type
     * @param <V> result type
     * @return a gatherer that provides the result of the supplied collector as a single element
     * @throws NullPointerException if the collector is null
     */
    public static <T, U, V> Gatherer<T, U, V> asGatherer(Collector<? super T, U, ? extends V> collector) {
        return new CollectingGatherer<>(collector);
    }

    private Packrat() {}
}
