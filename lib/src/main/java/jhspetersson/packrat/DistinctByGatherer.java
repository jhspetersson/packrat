package jhspetersson.packrat;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * Returns elements with distinct values that result from a mapping by the supplied function.
 *
 * @param <T> element type
 * @param <U> mapped element type
 * @author jhspetersson
 */
@SuppressWarnings("preview")
class DistinctByGatherer<T, U> implements Gatherer<T, Set<U>, T> {
    private final Function<T, U> mapper;

    DistinctByGatherer(Function<T, U> mapper) {
        this.mapper = mapper;
    }

    @Override
    public Supplier<Set<U>> initializer() {
        return HashSet::new;
    }

    @Override
    public Integrator<Set<U>, T, T> integrator() {
        return Integrator.of((state, element, downstream) -> {
            var mappedValue = mapper.apply(element);
            if (!state.contains(mappedValue)) {
                state.add(mappedValue);
                downstream.push(element);
            }
            return true;
        });
    }
}