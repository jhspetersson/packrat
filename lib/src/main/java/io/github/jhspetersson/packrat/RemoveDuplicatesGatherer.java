package io.github.jhspetersson.packrat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * Removes consecutive duplicates from a stream based on a mapping function.
 * Only adjacent elements that have equal mapped values will be considered duplicates.
 * If no mapping function is provided, elements are compared directly.
 * 
 * @param <T> element type
 * @param <U> mapped element type
 * @author jhspetersson
 */
public class RemoveDuplicatesGatherer<T, U> implements Gatherer<T, List<U>, T> {
    private final Function<? super T, ? extends U> mapper;

    RemoveDuplicatesGatherer(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "mapper cannot be null");
        this.mapper = mapper;
    }

    @Override
    public Supplier<List<U>> initializer() {
        return ArrayList::new;
    }

    @Override
    public Integrator<List<U>, T, T> integrator() {
        return Integrator.of((state, element, downstream) -> {
            var mappedValue = mapper.apply(element);
            if (!state.isEmpty() && Objects.equals(state.getFirst(), mappedValue)) {
                return true;
            } else {
                state.clear();
                state.add(mappedValue);
                return downstream.push(element);
            }
        });
    }
}
