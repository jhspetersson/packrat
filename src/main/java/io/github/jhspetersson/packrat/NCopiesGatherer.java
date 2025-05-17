package io.github.jhspetersson.packrat;

import java.util.stream.Gatherer;

/**
 * Returns <code>n</code> copies of every element.
 *
 * @param <T> element type
 * @author jhspetersson
 */
class NCopiesGatherer<T> implements Gatherer<T, Void, T> {
    private final long n;

    /**
     * Creates a new instance of {@link NCopiesGatherer}.
     *
     * @param n the number of copies to return
     * @throws IllegalArgumentException if <code>n</code> is negative
     */
    NCopiesGatherer(long n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be a non-negative number");
        }

        this.n = n;
    }

    @Override
    public Integrator<Void, T, T> integrator() {
        return Integrator.of((_, element, downstream) -> {
            for (var i = 1L; i <= n; i++) {
                if (!downstream.push(element)) {
                    return false;
                }
            }
            return !downstream.isRejecting();
        });
    }
}