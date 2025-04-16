package io.github.jhspetersson.packrat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * Removes consecutive duplicates from a stream.
 * Only adjacent elements that are equal will be considered duplicates.
 * 
 * @param <T> element type
 * @author jhspetersson
 */
public class RemoveDuplicatesGatherer<T> implements Gatherer<T, List<T>, T> {
    @Override
    public Supplier<List<T>> initializer() {
        return ArrayList::new;
    }

    @Override
    public Integrator<List<T>, T, T> integrator() {
        return Integrator.of((state, element, downstream) -> {
            if (!state.isEmpty() && state.getFirst().equals(element)) {
                return true;
            } else {
                state.clear();
                state.add(element);
                return downstream.push(element);
            }
        });
    }
}
