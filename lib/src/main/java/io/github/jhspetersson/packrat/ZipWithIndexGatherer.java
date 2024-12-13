package io.github.jhspetersson.packrat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * Returns elements mapped ("zipped") with an increasing index.
 *
 * @param <T> element type
 * @param <U> mapped element type
 * @author jhspetersson
 */
@SuppressWarnings("preview")
class ZipWithIndexGatherer<T, U> implements Gatherer<T, long[], U> {
    private final BiFunction<Long, ? super T, ? extends U> mapper;
    private final long startIndex;

    ZipWithIndexGatherer(BiFunction<Long, ? super T, ? extends U> mapper, long startIndex) {
        Objects.requireNonNull(mapper, "mapper cannot be null");

        this.mapper = mapper;
        this.startIndex = startIndex;
    }

    @Override
    public Supplier<long[]> initializer() {
        return () -> new long[] { startIndex };
    }

    @Override
    public Integrator<long[], T, U> integrator() {
        return Integrator.of((state, element, downstream) -> {
            var mappedValue = mapper.apply(state[0]++, element);
            return downstream.push(mappedValue);
        });
    }
}