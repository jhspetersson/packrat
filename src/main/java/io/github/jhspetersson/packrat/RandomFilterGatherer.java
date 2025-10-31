package io.github.jhspetersson.packrat;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Gatherer;

/**
 * Filters elements randomly with the given acceptance probability.
 * <p>
 * Each incoming element is accepted independently with probability {@code probability}
 * and pushed downstream; otherwise it is skipped.
 *
 * @param <T> element type
 * @author jhspetersson
 */
class RandomFilterGatherer<T> implements Gatherer<T, Void, T> {
    private final double probability;

    /**
     * Creates a gatherer that accepts each element with the given probability.
     *
     * @param probability acceptance probability in the inclusive range [0.0, 1.0]
     * @throws IllegalArgumentException if {@code probability} is not in [0.0, 1.0]
     */
    RandomFilterGatherer(double probability) {
        if (!(probability >= 0.0 && probability <= 1.0)) {
            throw new IllegalArgumentException("probability must be in range [0.0, 1.0]");
        }
        this.probability = probability;
    }

    @Override
    public Integrator<Void, T, T> integrator() {
        var random = ThreadLocalRandom.current();
        return Integrator.of((_, element, downstream) -> {
            if (random.nextDouble() <= probability) {
                return downstream.push(element);
            }
            return !downstream.isRejecting();
        });
    }
}
