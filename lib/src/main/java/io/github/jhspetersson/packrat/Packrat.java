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
     */
    public static <T, U> Gatherer<T, ?, T> distinctBy(Function<? super T, ? extends U> mapper) {
        return new DistinctByGatherer<>(mapper);
    }

    /**
     * Returns distinct elements that appear at least <code>n</code> times in the stream.
     *
     * @param n at least how many times the element has to appear in the stream
     * @param <T> element type
     */
    public static <T> Gatherer<T, ?, T> atLeast(long n) {
        return atLeastBy(n, Function.identity());
    }

    /**
     * Returns distinct elements mapped by the supplied function that appear at least <code>n</code> times in the stream.
     *
     * @param n at least how many times the element has to appear in the stream
     * @param <T> element type
     */
    public static <T, U> Gatherer<T, ?, T> atLeastBy(long n, Function<? super T, ? extends U> mapper) {
        return new AtLeastGatherer<>(n, mapper);
    }

    /**
     * Returns elements that appear at most <code>n</code> times in the stream.
     *
     * @param n at most how many times the element has to appear in the stream
     * @param <T> element type
     */
    public static <T> Gatherer<T, ?, T> atMost(long n) {
        return atMostBy(n, Function.identity());
    }

    /**
     * Returns elements mapped by the supplied function that appear at most <code>n</code> times in the stream.
     *
     * @param n at most how many times the element has to appear in the stream
     * @param <T> element type
     * @param <U> mapped element type
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
     */
    public static <T, U> Gatherer<T, ?, T> filterBy(Function<? super T, ? extends U> mapper, U value) {
        return filterBy(mapper, value, Objects::equals);
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
     * Outputs the smallest element in the stream, comparing is done after mapping function applied.
     *
     * @param mapper mapping function
     * @param <T> element type
     * @param <U> mapped element type
     */
    public static <T, U extends Comparable<U>> Gatherer<T, ?, T> minBy(Function<? super T, ? extends U> mapper) {
        return minBy(mapper, Comparator.naturalOrder());
    }

    /**
     * Outputs the smallest element in the stream, comparing with given comparator is done after mapping function applied.
     *
     * @param mapper mapping function
     * @param comparator comparator
     * @param <T> element type
     * @param <U> mapped element type
     */
    public static <T, U extends Comparable<U>> Gatherer<T, ?, T> minBy(Function<? super T, ? extends U> mapper, Comparator<? super U> comparator) {
        return new MinMaxGatherer<>(mapper, comparator, cmp -> cmp < 0);
    }

    /**
     * Outputs the greatest element in the stream, comparing is done after mapping function applied.
     *
     * @param mapper mapping function
     * @param <T> element type
     * @param <U> mapped element type
     */
    public static <T, U extends Comparable<U>> Gatherer<T, ?, T> maxBy(Function<? super T, ? extends U> mapper) {
        return maxBy(mapper, Comparator.naturalOrder());
    }

    /**
     * Outputs the greatest element in the stream, comparing with given comparator is done after mapping function applied.
     *
     * @param mapper mapping function
     * @param comparator comparator
     * @param <T> element type
     * @param <U> mapped element type
     */
    public static <T, U extends Comparable<U>> Gatherer<T, ?, T> maxBy(Function<? super T, ? extends U> mapper, Comparator<? super U> comparator) {
        return new MinMaxGatherer<>(mapper, comparator, cmp -> cmp > 0);
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
     * Returns lists ("chunks") of elements, where each next element is greater than the previous one.
     * Comparison is done with the natural order comparator.
     *
     * @param <T> element type
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, List<T>> increasingChunks() {
        return increasingChunks(Comparator.naturalOrder());
    }

    /**
     * Returns lists ("chunks") of elements, where each next element is greater than the previous one.
     * Comparison is done with the supplied comparator.
     *
     * @param <T> element type
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, List<T>> increasingChunks(Comparator<? super T> comparator) {
        return new IncreasingDecreasingChunksGatherer<>(comparator, cmp -> cmp < 0);
    }

    /**
     * Returns lists ("chunks") of elements, where each next element is greater or equal than the previous one.
     * Comparison is done with the natural order comparator.
     *
     * @param <T> element type
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, List<T>> increasingOrEqualChunks() {
        return increasingOrEqualChunks(Comparator.naturalOrder());
    }

    /**
     * Returns lists ("chunks") of elements, where each next element is greater than the previous one.
     * Comparison is done with the supplied comparator.
     *
     * @param <T> element type
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, List<T>> increasingOrEqualChunks(Comparator<? super T> comparator) {
        return new IncreasingDecreasingChunksGatherer<>(comparator, cmp -> cmp <= 0);
    }

    /**
     * Returns lists ("chunks") of elements, where each next element is less than the previous one.
     * Comparison is done with the natural order comparator.
     *
     * @param <T> element type
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, List<T>> decreasingChunks() {
        return decreasingChunks(Comparator.naturalOrder());
    }

    /**
     * Returns lists ("chunks") of elements, where each next element is less than the previous one.
     * Comparison is done with the supplied comparator.
     *
     * @param <T> element type
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, List<T>> decreasingChunks(Comparator<? super T> comparator) {
        return new IncreasingDecreasingChunksGatherer<>(comparator, cmp -> cmp > 0);
    }

    /**
     * Returns lists ("chunks") of elements, where each next element is less or equal than the previous one.
     * Comparison is done with the natural order comparator.
     *
     * @param <T> element type
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, List<T>> decreasingOrEqualChunks() {
        return decreasingOrEqualChunks(Comparator.naturalOrder());
    }

    /**
     * Returns lists ("chunks") of elements, where each next element is less or equal than the previous one.
     * Comparison is done with the supplied comparator.
     *
     * @param <T> element type
     */
    public static <T extends Comparable<? super T>> Gatherer<T, ?, List<T>> decreasingOrEqualChunks(Comparator<? super T> comparator) {
        return new IncreasingDecreasingChunksGatherer<>(comparator, cmp -> cmp >= 0);
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
        return skipAndMapN(0L, 1L, mapper);
    }

    /**
     * Returns all elements, the first mapN elements are mapped with the supplied mapping function.
     *
     * @param mapper mapping function
     * @param <T> element type
     */
    public static <T> Gatherer<T, ?, T> mapN(long mapN, Function<? super T, ? extends T> mapper) {
        return skipAndMapN(0L, mapN, mapper);
    }

    /**
     * Returns all elements that after the first skipN are mapped with the supplied mapping function.
     *
     * @param mapper mapping function
     * @param <T> element type
     */
    public static <T> Gatherer<T, ?, T> skipAndMap(long skipN, Function<? super T, ? extends T> mapper) {
        return skipAndMapN(skipN, -1L, mapper);
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
     * Optionally flattens elements mapped to streams depending on the supplied predicate.
     *
     * @param mapper mapping function, usually calls <code>element::stream</code> method
     * @param predicate deciding predicate when to call the mapping function
     * @param <T> element type
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
     */
    public static <T, U, V> Gatherer<T, ?, V> zip(Iterator<? extends U> iterator, BiFunction<? super T, ? super U, ? extends V> mapper) {
        return new ZipGatherer<>(iterator, mapper);
    }

    /**
     * Returns elements mapped ("zipped") with an increasing index.
     * Output type is {@link java.util.Map.Entry} with a Long key and an element as a value.
     *
     * @param <T> element type
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
     */
    public static <T, U> Gatherer<T, ?, U> zipWithIndex(BiFunction<Long, ? super T, ? extends U> mapper, long startIndex) {
        return new ZipWithIndexGatherer<>(mapper, startIndex);
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
     * Peeks at each element along with its index, but passes the original element downstream.
     * The index starts from 0.
     *
     * @param consumer consumer function that accepts index and element
     * @param <T> element type
     */
    public static <T> Gatherer<T, ?, T> peekWithIndex(BiConsumer<Long, ? super T> consumer) {
        return peekWithIndex(consumer, 0);
    }

    /**
     * Peeks at each element along with its index, but passes the original element downstream.
     *
     * @param consumer consumer function that accepts index and element
     * @param startIndex starting index
     * @param <T> element type
     */
    public static <T> Gatherer<T, ?, T> peekWithIndex(BiConsumer<Long, ? super T> consumer, long startIndex) {
        return new PeekWithIndexGatherer<>(consumer, startIndex);
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
     */
    public static <T, U> Gatherer<T, ?, T> removeBy(Function<? super T, ? extends U> mapper, U value, BiPredicate<? super U, ? super U> predicate) {
        return new FilteringGatherer<>(mapper, value, predicate, true);
    }

    /**
     * Removes consecutive duplicates from the stream.
     * Only adjacent elements that have equal mapped values will be considered duplicates.
     * <p>
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
     */
    public static <T, U> Gatherer<T, ?, T> removeDuplicatesBy(Function<? super T, ? extends U> mapper) {
        return new RemoveDuplicatesGatherer<>(mapper);
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
     * Returns a sample of the specified size from the stream of elements.
     * <p>
     *
     * @param n sample size
     * @return sampling gatherer
     * @param <T> element type
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
     * <p>
     *
     * @param n sample size
     * @param maxSpan maximum count of the elements to inspect
     * @return sampling gatherer
     * @param <T> element type
     */
    public static <T> Gatherer<T, ?, T> sample(int n, int maxSpan) {
        return new SamplingGatherer<>(n, maxSpan);
    }

    /**
     * Returns last n elements.
     *
     * @param n count of last elements to return
     * @return lasting gatherer
     * @param <T> element type
     */
    public static <T> Gatherer<T, ?, T> last(long n) {
        return new LastingGatherer<>(n);
    }

    /**
     * Returns last n unique elements.
     *
     * @param n count of last unique elements to return
     * @return lasting gatherer
     * @param <T> element type
     */
    public static <T> Gatherer<T, ?, T> lastUnique(long n) {
        return new LastingGatherer<>(n, true);
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
