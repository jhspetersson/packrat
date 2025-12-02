package io.github.jhspetersson.packrat;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import org.jspecify.annotations.NonNull;

/**
 * Filters elements based on their index and a predicate.
 *
 * @param <T> element type
 * @author jhspetersson
 */
class FilteringWithIndexGatherer<T> implements Gatherer<T, long[], T> {
    private final BiPredicate<Long, ? super T> predicate;
    private final long startIndex;
    private final boolean invert;

    FilteringWithIndexGatherer(@NonNull BiPredicate<Long, ? super T> predicate, long startIndex) {
        this(predicate, startIndex, false);
    }

    FilteringWithIndexGatherer(@NonNull BiPredicate<Long, ? super T> predicate, long startIndex, boolean invert) {
        Objects.requireNonNull(predicate, "predicate cannot be null");

        this.predicate = predicate;
        this.startIndex = startIndex;
        this.invert = invert;
    }

    @Override
    public Supplier<long[]> initializer() {
        return () -> new long[] { startIndex };
    }

    @Override
    public Integrator<long[], T, T> integrator() {
        return Integrator.of((state, element, downstream) -> {
            var currentIndex = state[0]++;
            var testResult = predicate.test(currentIndex, element);
            if (testResult ^ invert) {
                return downstream.push(element);
            }
            return !downstream.isRejecting();
        });
    }
}
