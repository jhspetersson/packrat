package io.github.jhspetersson.packrat;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import org.jspecify.annotations.NonNull;

/**
 * Removes consecutive duplicates from a stream based on a mapping function.
 * Only adjacent elements that have equal mapped values will be considered duplicates.
 * If no mapping function is provided, elements are compared directly.
 *
 * @param <T> element type
 * @param <U> mapped element type
 * @author jhspetersson
 */
class RemoveDuplicatesGatherer<T, U> implements Gatherer<T, RemoveDuplicatesGatherer.State<U>, T> {
    private final Function<? super T, ? extends U> mapper;

    RemoveDuplicatesGatherer(@NonNull Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "mapper cannot be null");
        this.mapper = mapper;
    }

    @Override
    public Supplier<State<U>> initializer() {
        return State::new;
    }

    @Override
    public Integrator<State<U>, T, T> integrator() {
        return Integrator.of((state, element, downstream) -> {
            var mappedValue = mapper.apply(element);
            if (state.hasValue && Objects.equals(state.value, mappedValue)) {
                return !downstream.isRejecting();
            } else {
                state.hasValue = true;
                state.value = mappedValue;
                return downstream.push(element);
            }
        });
    }

    static class State<U> {
        boolean hasValue;
        U value;
    }
}
