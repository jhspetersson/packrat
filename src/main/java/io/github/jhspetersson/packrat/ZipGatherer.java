package io.github.jhspetersson.packrat;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
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
class ZipGatherer<T, U, V> implements Gatherer<T, ZipGatherer.State<U>, V> {
    private final Supplier<Iterator<? extends U>> iteratorSupplier;
    private final BiFunction<? super T, ? super U, ? extends V> mapper;
    private final Runnable closeHandler;

    ZipGatherer(@NonNull Iterable<? extends U> input,
                @NonNull BiFunction<? super T, ? super U, ? extends V> mapper) {
        Objects.requireNonNull(input, "input cannot be null");
        Objects.requireNonNull(mapper, "mapper cannot be null");

        this.iteratorSupplier = input::iterator;
        this.mapper = mapper;
        this.closeHandler = null;
    }

    ZipGatherer(@NonNull Stream<? extends U> input,
                @NonNull BiFunction<? super T, ? super U, ? extends V> mapper) {
        Objects.requireNonNull(input, "input cannot be null");
        Objects.requireNonNull(mapper, "mapper cannot be null");

        this.iteratorSupplier = input::iterator;
        this.mapper = mapper;
        this.closeHandler = input::close;
    }

    ZipGatherer(@NonNull Iterator<? extends U> iterator,
                @NonNull BiFunction<? super T, ? super U, ? extends V> mapper) {
        Objects.requireNonNull(iterator, "iterator cannot be null");
        Objects.requireNonNull(mapper, "mapper cannot be null");

        this.iteratorSupplier = () -> iterator;
        this.mapper = mapper;
        this.closeHandler = null;
    }

    @Override
    public Supplier<State<U>> initializer() {
        return () -> new State<>(iteratorSupplier.get());
    }

    @Override
    public Integrator<State<U>, T, V> integrator() {
        return Integrator.of((state, element, downstream) -> {
            if (state.iterator.hasNext()) {
                var mappedValue = mapper.apply(element, state.iterator.next());
                return downstream.push(mappedValue);
            }
            return false;
        });
    }

    @Override
    public BiConsumer<State<U>, Downstream<? super V>> finisher() {
        return (_, _) -> {
            if (closeHandler != null) {
                closeHandler.run();
            }
        };
    }

    static class State<U> {
        final Iterator<? extends U> iterator;

        State(Iterator<? extends U> iterator) {
            this.iterator = iterator;
        }
    }
}
