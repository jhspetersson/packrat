package io.github.jhspetersson.packrat;

import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * Drops last n elements.
 *
 * @param <T> element type
 * @author jhspetersson
 */
class DropLastNGatherer<T> implements Gatherer<T, RingBuffer<T>, T> {
    private final long n;

    DropLastNGatherer(long n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be a non-negative number");
        }

        this.n = n;
    }

    @Override
    public Supplier<RingBuffer<T>> initializer() {
        return () -> new RingBuffer<>(Math.max(n, 1));
    }

    @Override
    public Integrator<RingBuffer<T>, T, T> integrator() {
        if (n == 0) {
            return Integrator.of((_, element, downstream) -> downstream.push(element));
        }

        return Integrator.of((buffer, element, downstream) -> {
            if (buffer.isFull()) {
                var oldest = buffer.removeFirst();
                buffer.add(element);
                return downstream.push(oldest);
            }
            buffer.add(element);
            return !downstream.isRejecting();
        });
    }
}
