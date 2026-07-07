package io.github.jhspetersson.packrat;

import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * Returns every nth element from the stream.
 *
 * @param <T> element type
 * @author jhspetersson
 */
class NthGatherer<T> implements Gatherer<T, long[], T> {
    private final int n;

    NthGatherer(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be a positive number");
        }

        this.n = n;
    }

    @Override
    public Supplier<long[]> initializer() {
        return () -> new long[1];
    }

    @Override
    public Integrator<long[], T, T> integrator() {
        return Integrator.ofGreedy((state, element, downstream) -> {
            if (++state[0] % n == 0) {
                return downstream.push(element);
            }
            return !downstream.isRejecting();
        });
    }
}
