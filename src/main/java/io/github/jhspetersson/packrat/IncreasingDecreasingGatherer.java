package io.github.jhspetersson.packrat;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import org.jspecify.annotations.NonNull;

/**
 * Filters all the elements going in some order specified by the supplied comparator.
 *
 * @param <T> element type
 * @author jhspetersson
 */
class IncreasingDecreasingGatherer<T> implements Gatherer<T, IncreasingDecreasingGatherer.State<T>, T> {
    private final Comparator<? super T> comparator;
    private final Predicate<Integer> predicate;

    IncreasingDecreasingGatherer(@NonNull Comparator<? super T> comparator,
                                 @NonNull Predicate<Integer> predicate) {
        Objects.requireNonNull(comparator, "comparator cannot be null");
        Objects.requireNonNull(predicate, "predicate cannot be null");

        this.comparator = comparator;
        this.predicate = predicate;
    }

    @Override
    public Supplier<State<T>> initializer() {
        return State::new;
    }

    @Override
    public Integrator<State<T>, T, T> integrator() {
        return Integrator.of((state, element, downstream) -> {
            if (state.value == null) {
                state.value = element;
                return downstream.push(element);
            }

            var result = comparator.compare(state.value, element);
            if (predicate.test(result)) {
                state.value = element;
                return downstream.push(element);
            }

            return !downstream.isRejecting();
        });
    }

    static class State<T> {
        T value;
    }
}
