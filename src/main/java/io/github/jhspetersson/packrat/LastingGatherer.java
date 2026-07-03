package io.github.jhspetersson.packrat;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
 * If {@code n == 0}, no elements are emitted. For {@code n > 0}, the gatherer greedily consumes
 * the entire upstream before producing any output in its {@linkplain #finisher() finisher}.
 * <p>
 * Null mappers are not permitted. A negative {@code n} results in an
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
        if (n < 0) {
            throw new IllegalArgumentException("n must be a non-negative number");
        }

        Objects.requireNonNull(mapper, "mapper cannot be null");

        this.n = n;
        this.unique = unique;
        this.mapper = mapper;
    }

    @Override
    public Supplier<State<T>> initializer() {
        return () -> new State<>(unique, mapper);
    }

    @Override
    public Integrator<State<T>, T, T> integrator() {
        if (n == 0) {
            return Integrator.of((_, _, _) -> false);
        }

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
        final Deque<T> deque;
        final LinkedHashMap<Object, T> lastByKey;
        final boolean unique;
        final Function<? super T, ?> mapper;

        State(boolean unique, Function<? super T, ?> mapper) {
            this.deque = unique ? null : new LinkedList<>();
            this.lastByKey = unique ? new LinkedHashMap<>() : null;
            this.unique = unique;
            this.mapper = mapper;
        }

        void addElement(T element, long n) {
            if (unique) {
                var mappedElement = mapper.apply(element);
                lastByKey.remove(mappedElement);
                lastByKey.put(mappedElement, element);
                if (lastByKey.size() > n) {
                    lastByKey.pollFirstEntry();
                }
            } else {
                if (deque.size() == n) {
                    deque.removeFirst();
                }
                deque.add(element);
            }
        }

        @Override
        public Iterator<T> iterator() {
            return unique ? lastByKey.values().iterator() : deque.iterator();
        }
    }
}