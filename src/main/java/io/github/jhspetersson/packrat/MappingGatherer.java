package io.github.jhspetersson.packrat;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import org.jspecify.annotations.NonNull;

/**
 * Returns elements with optional mapping applied to the specified number of them.
 *
 * @param <T> element type
 * @author jhspetersson
 */
class MappingGatherer<T> implements Gatherer<T, long[], T> {
    private final long skipN;
    private final long mapN;
    private final Function<? super T, ? extends T> mapper;

    MappingGatherer(long skipN, long mapN, @NonNull Function<? super T, ? extends T> mapper) {
        Objects.requireNonNull(mapper, "mapper cannot be null");

        this.skipN = skipN;
        this.mapN = mapN;
        this.mapper = mapper;
    }

    @Override
    public Supplier<long[]> initializer() {
        return () -> new long[2];
    }

    @Override
    public Integrator<long[], T, T> integrator() {
        return Integrator.of((state, element, downstream) -> {
            if (skipN > 0 && state[0] < skipN) {
                state[0] += 1;
                return downstream.push(element);
            } else if (mapN < 0 || state[1] < mapN) {
                state[1] += 1;
                var mappedValue = mapper.apply(element);
                return downstream.push(mappedValue);
            } else {
                return downstream.push(element);
            }
        });
    }
}