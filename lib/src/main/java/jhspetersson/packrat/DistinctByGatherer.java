package jhspetersson.packrat;

import java.util.HashSet;
import java.util.Objects;
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
class DistinctByGatherer<T, U> implements Gatherer<T, Set<? super U>, T> {
    private final Function<? super T, ? extends U> mapper;

    DistinctByGatherer(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "mapper cannot be null");

        this.mapper = mapper;
    }

    @Override
    public Supplier<Set<? super U>> initializer() {
        return HashSet::new;
    }

    @Override
    public Integrator<Set<? super U>, T, T> integrator() {
        return Integrator.of((state, element, downstream) -> {
            var mappedValue = mapper.apply(element);
            if (!state.contains(mappedValue)) {
                state.add(mappedValue);
                return downstream.push(element);
            }
            return true;
        });
    }
}