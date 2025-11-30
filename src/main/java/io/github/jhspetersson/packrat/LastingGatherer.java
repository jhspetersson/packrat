package io.github.jhspetersson.packrat;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * A {@link Gatherer} that buffers the last elements of an upstream stream and emits them
 * at the end of processing.
 * <p>
 * This gatherer underpins the public APIs {@code Packrat.last(n)}, {@code Packrat.lastUnique(n)}
 * and {@code Packrat.lastUniqueBy(n, mapper)}:
 * <ul>
 *   <li>When constructed with {@code unique == false}, it returns the last {@code n} elements.</li>
 *   <li>When constructed with {@code unique == true}, it returns the last {@code n} elements
 *       that are unique with respect to a key produced by the supplied {@code mapper} function.
 *       The original elements are emitted in the order of their last occurrence; only the
 *       uniqueness check uses the mapped keys.</li>
 * </ul>
 *
 * <p>
 * If {@code n == 0}, no elements are emitted. For {@code n > 0}, the gatherer greedily consumes
 * the entire upstream before producing any output in its {@linkplain #finisher() finisher}.
 * The working memory is bounded by {@code O(n)}: a deque of up to {@code n} elements and, when
 * {@code unique} is enabled, a set of up to {@code n} mapped keys.
 *
 * <p>
 * Null mappers are not permitted. A negative {@code n} results in an
 * {@link IllegalArgumentException}.
 *
 * @param <T> element type
 * @author jhspetersson
 * @implNote This gatherer delays emission until upstream completion and therefore will not
 *           yield any elements to downstream until the source stream is fully traversed.
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

    LastingGatherer(long n, boolean unique, Function<? super T, ?> mapper) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be a non-negative number");
        }

        Objects.requireNonNull(mapper);

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
            if (state.containsElement(element)) {
                return true;
            }

            if (state.sizeEquals(n)) {
                state.removeElement();
            }

            state.addElement(element);

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
        Deque<T> deque;
        Set<Object> mappedElements;
        final boolean unique;
        final Function<? super T, ?> mapper;

        State(boolean unique, Function<? super T, ?> mapper) {
            this.deque = new ArrayDeque<>();
            this.mappedElements = new HashSet<>();
            this.unique = unique;
            this.mapper = mapper;
        }

        boolean sizeEquals(long size) {
            return deque.size() == size;
        }

        boolean containsElement(T element) {
            if (!unique) return false;
            var mappedElement = mapper.apply(element);
            return mappedElements.contains(mappedElement);
        }

        void addElement(T element) {
            deque.add(element);
            if (unique) {
                var mappedElement = mapper.apply(element);
                mappedElements.add(mappedElement);
            }
        }

        void removeElement() {
            var removedElement = deque.removeFirst();
            if (unique) {
                var mappedElement = mapper.apply(removedElement);
                mappedElements.remove(mappedElement);
            }
        }

        @Override
        public Iterator<T> iterator() {
            return deque.iterator();
        }
    }
}