package jhspetersson.packrat;

import java.util.stream.Gatherer;

/**
 * Returns elements with distinct values that result from a mapping by the supplied function.
 *
 * @param <T> element type
 * @author jhspetersson
 */
@SuppressWarnings("preview")
class NCopiesGatherer<T> implements Gatherer<T, Object, T> {
    private final long n;

    NCopiesGatherer(long n) {
        this.n = n;
    }

    @Override
    public Integrator<Object, T, T> integrator() {
        return Integrator.of((_, element, downstream) -> {
            for (var i = 1L; i <= n; i++) {
                downstream.push(element);
            }
            return true;
        });
    }
}