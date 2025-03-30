package io.github.jhspetersson.packrat;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Gatherer;

/**
 * Filters all the elements going in some order specified by the supplied comparator.
 *
 * @param <T> element type
 * @author jhspetersson
 */
class IncreasingDecreasingGatherer<T> implements Gatherer<T, Void, T> {
    private final Comparator<? super T> comparator;
    private final Predicate<Integer> predicate;
    private T value;

    IncreasingDecreasingGatherer(Comparator<? super T> comparator, Predicate<Integer> predicate) {
        Objects.requireNonNull(comparator, "comparator cannot be null");
        Objects.requireNonNull(predicate, "predicate cannot be null");

        this.comparator = comparator;
        this.predicate = predicate;
    }

    @Override
    public Integrator<Void, T, T> integrator() {
        return Integrator.of((_, element, downstream) -> {
            if (value == null) {
                value = element;
                return downstream.push(element);
            }

            var result = comparator.compare(value, element);
            if (predicate.test(result)) {
                value = element;
                return downstream.push(element);
            }

            return true;
        });
    }
}