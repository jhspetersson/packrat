package io.github.jhspetersson.packrat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * Returns lists ("chunks") of elements, where all elements in a chunk are equal after applying the mapper function.
 *
 * @param <T> element type
 * @param <U> mapped type for comparison
 * @author jhspetersson
 */
class EqualChunksGatherer<T, U> implements Gatherer<T, List<T>, List<T>> {
    private final Function<? super T, ? extends U> mapper;
    private final Comparator<? super U> comparator;
    private U currentValue;
    private boolean first = true;

    EqualChunksGatherer(Function<? super T, ? extends U> mapper) {
        this(mapper, null);
    }

    EqualChunksGatherer(Function<? super T, ? extends U> mapper, Comparator<? super U> comparator) {
        Objects.requireNonNull(mapper, "mapper cannot be null");
        this.mapper = mapper;
        this.comparator = comparator;
    }

    @Override
    public Supplier<List<T>> initializer() {
        return ArrayList::new;
    }

    @Override
    public Integrator<List<T>, T, List<T>> integrator() {
        return Integrator.of((state, element, downstream) -> {
            var mappedValue = mapper.apply(element);

            if (first) {
                first = false;
                currentValue = mappedValue;
                state.add(element);
            } else {
                boolean areEqual;
                if (comparator != null) {
                    areEqual = comparator.compare(currentValue, mappedValue) == 0;
                } else {
                    areEqual = Objects.equals(currentValue, mappedValue);
                }

                if (areEqual) {
                    state.add(element);
                } else {
                    var chunk = List.copyOf(state);
                    state.clear();
                    state.add(element);
                    currentValue = mappedValue;
                    return downstream.push(chunk);
                }
            }

            return !downstream.isRejecting();
        });
    }

    @Override
    public BiConsumer<List<T>, Downstream<? super List<T>>> finisher() {
        return (state, downstream) -> {
            if (!state.isEmpty()) {
                var chunk = List.copyOf(state);
                downstream.push(chunk);
            }
        };
    }
}
