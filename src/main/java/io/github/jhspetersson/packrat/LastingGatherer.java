package io.github.jhspetersson.packrat;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import org.jspecify.annotations.NonNull;

/**
 * A {@link Gatherer} that buffers the last elements of an upstream stream and emits them
 * at the end of processing.
 * <p>
 * This gatherer underpins the public APIs {@code Packrat.last(n)}, {@code Packrat.lastUnique(n)}
 * and {@code Packrat.lastUniqueBy(n, mapper)}:
 * <p>
 * The public factories handle {@code n == 0} separately. This gatherer requires a positive
 * count and consumes the entire upstream before producing output in its {@linkplain #finisher() finisher}.
 * <p>
 * Null mappers are not permitted. A non-positive {@code n} results in an
 * {@link IllegalArgumentException}.
 *
 * @param <T> element type
 * @author jhspetersson
 */
class LastingGatherer<T> implements Gatherer<T, LastingGatherer.State<T>, T> {
    private final long n;
    private final boolean unique;
    private final Function<? super T, ?> mapper;

    LastingGatherer(long n) {
        this(n, false);
    }

    LastingGatherer(long n, boolean unique) {
        this(n, unique, Function.identity());
    }

    LastingGatherer(long n, boolean unique, @NonNull Function<? super T, ?> mapper) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be a positive number");
        }

        Objects.requireNonNull(mapper, "mapper cannot be null");

        this.n = n;
        this.unique = unique;
        this.mapper = mapper;
    }

    @Override
    public Supplier<State<T>> initializer() {
        return () -> new State<>(n, unique, mapper);
    }

    @Override
    public Integrator<State<T>, T, T> integrator() {
        return Integrator.ofGreedy((state, element, downstream) -> {
            state.addElement(element, n);

            return !downstream.isRejecting();
        });
    }

    @Override
    public BiConsumer<State<T>, Downstream<? super T>> finisher() {
        return (state, downstream) -> {
            for (var element : state) {
                if (!downstream.push(element)) {
                    break;
                }
            }
        };
    }

    static class State<T> implements Iterable<T> {
        final RingBuffer<T> buffer;
        final LinkedHashMap<Object, T> lastByKey;
        final boolean unique;
        final Function<? super T, ?> mapper;

        State(long n, boolean unique, Function<? super T, ?> mapper) {
            this.buffer = unique ? null : new RingBuffer<>(n);
            this.lastByKey = unique ? new LinkedHashMap<>() : null;
            this.unique = unique;
            this.mapper = mapper;
        }

        void addElement(T element, long n) {
            if (unique) {
                var mappedElement = mapper.apply(element);
                lastByKey.putLast(mappedElement, element);
                if (lastByKey.size() > n) {
                    lastByKey.pollFirstEntry();
                }
            } else {
                if (buffer.isFull()) {
                    buffer.removeFirst();
                }
                buffer.add(element);
            }
        }

        @Override
        public Iterator<T> iterator() {
            return unique ? lastByKey.values().iterator() : buffer.iterator();
        }
    }
}
