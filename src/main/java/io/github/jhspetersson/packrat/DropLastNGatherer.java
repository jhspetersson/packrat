package io.github.jhspetersson.packrat;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * Drops last n elements.
 *
 * @param <T> element type
 * @author jhspetersson
 */
class DropLastNGatherer<T> implements Gatherer<T, Deque<T>, T> {
    private final long n;

    DropLastNGatherer(long n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be a non-negative number");
        }

        this.n = n;
    }

    @Override
    public Supplier<Deque<T>> initializer() {
        return ArrayDeque::new;
    }

    @Override
    public Integrator<Deque<T>, T, T> integrator() {
        if (n == 0) {
            return Integrator.of((_, element, downstream) -> downstream.push(element));
        }

        return Integrator.of((deque, element, downstream) -> {
            deque.addLast(element);
            if (deque.size() > n) {
                return downstream.push(deque.removeFirst());
            }
            return !downstream.isRejecting();
        });
    }
}
