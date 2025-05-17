package io.github.jhspetersson.packrat;

import java.util.Deque;
import java.util.function.BiConsumer;
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
        return () -> new FixedSizeDeque<>((int) Math.max(n, 16));
    }

    @Override
    public Integrator<Deque<T>, T, T> integrator() {
        if (n == 0) {
            return Integrator.of((_, element, downstream) -> downstream.push(element));
        }

        return Integrator.ofGreedy((deque, element, downstream) -> {
            deque.add(element);
            return !downstream.isRejecting();
        });
    }

    @Override
    public BiConsumer<Deque<T>, Downstream<? super T>> finisher() {
        return (deque, downstream) -> {
            var elementsToSkip = (int) Math.min(n, deque.size());
            var elementsToProcess = deque.size() - elementsToSkip;

            for (var i = 0; i < elementsToProcess; i++) {
                if (!downstream.push(deque.removeFirst())) {
                    break;
                }
            }
        };
    }
}
