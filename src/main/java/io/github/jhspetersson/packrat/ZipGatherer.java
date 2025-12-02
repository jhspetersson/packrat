package io.github.jhspetersson.packrat;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;

/**
 * Returns elements mapped ("zipped") with the values from some other stream, iterable or iterator.
 *
 * @param <T> element type
 * @param <U> element type of the supplied stream or iterable
 * @param <V> mapped element type
 * @author jhspetersson
 */
class ZipGatherer<T, U, V> implements Gatherer<T, Void, V> {
    private final Iterator<? extends U> iterator;
    private final BiFunction<? super T, ? super U, ? extends V> mapper;

    ZipGatherer(@NonNull Iterable<? extends U> input,
                @NonNull BiFunction<? super T, ? super U, ? extends V> mapper) {
        this(input.iterator(), mapper);
    }

    ZipGatherer(@NonNull Stream<? extends U> input,
                @NonNull BiFunction<? super T, ? super U, ? extends V> mapper) {
        this(input.iterator(), mapper);
    }

    ZipGatherer(@NonNull Iterator<? extends U> iterator,
                @NonNull BiFunction<? super T, ? super U, ? extends V> mapper) {
        Objects.requireNonNull(iterator, "iterator cannot be null");
        Objects.requireNonNull(mapper, "mapper cannot be null");

        this.iterator = iterator;
        this.mapper = mapper;
    }

    @Override
    public Integrator<Void, T, V> integrator() {
        return Integrator.ofGreedy((_, element, downstream) -> {
            if (iterator.hasNext()) {
                var mappedValue = mapper.apply(element, iterator.next());
                return downstream.push(mappedValue);
            }
            return false;
        });
    }
}