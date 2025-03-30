package io.github.jhspetersson.packrat;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * Returns last n elements.
 *
 * @param <T> element type
 * @author jhspetersson
 */
class LastingGatherer<T> implements Gatherer<T, LastingGatherer.State<T>, T> {
    private final long n;
    private final boolean unique;

    LastingGatherer(long n) {
        this(n, false);
    }

    LastingGatherer(long n, boolean unique) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be a positive number");
        }

        this.n = n;
        this.unique = unique;
    }

    @Override
    public Supplier<State<T>> initializer() {
        return () -> new State<>(new ArrayDeque<>(), new HashSet<>());
    }

    @Override
    public Integrator<State<T>, T, T> integrator() {
        return Integrator.ofGreedy((state, element, _) -> {
            if (unique && state.containsElement(element)) {
                return true;
            }

            if (state.sizeEquals(n)) {
                state.removeElement(element);
            }

            state.addElement(element);

            return true;
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
        Set<T> elements;

        State(Deque<T> deque, Set<T> elements) {
            this.deque = deque;
            this.elements = elements;
        }

        boolean sizeEquals(long size) {
            return deque.size() == size;
        }

        boolean containsElement(T element) {
            return elements.contains(element);
        }

        void addElement(T element) {
            deque.add(element);
            elements.add(element);
        }

        void removeElement(T element) {
            var removedElement = deque.removeFirst();
            elements.remove(removedElement);
        }

        @Override
        public Iterator<T> iterator() {
            return deque.iterator();
        }
    }
}