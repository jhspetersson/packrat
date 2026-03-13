package io.github.jhspetersson.packrat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import org.jspecify.annotations.NonNull;

/**
 * Returns lists ("chunks") of elements, where all elements in a chunk are equal after applying the mapper function.
 *
 * @param <T> element type
 * @param <U> mapped type for comparison
 * @author jhspetersson
 */
class EqualChunksGatherer<T, U> implements Gatherer<T, EqualChunksGatherer.State<T, U>, List<T>> {
    private final Function<? super T, ? extends U> mapper;
    private final Comparator<? super U> comparator;

    EqualChunksGatherer(@NonNull Function<? super T, ? extends U> mapper) {
        this(mapper, null);
    }

    EqualChunksGatherer(@NonNull Function<? super T, ? extends U> mapper,
                        Comparator<? super U> comparator) {
        Objects.requireNonNull(mapper, "mapper cannot be null");
        this.mapper = mapper;
        this.comparator = comparator;
    }

    @Override
    public Supplier<State<T, U>> initializer() {
        return State::new;
    }

    @Override
    public Integrator<State<T, U>, T, List<T>> integrator() {
        return Integrator.of((state, element, downstream) -> {
            var mappedValue = mapper.apply(element);

            if (state.first) {
                state.first = false;
                state.currentValue = mappedValue;
                state.chunk.add(element);
            } else {
                boolean areEqual;
                if (comparator != null) {
                    areEqual = comparator.compare(state.currentValue, mappedValue) == 0;
                } else {
                    areEqual = Objects.equals(state.currentValue, mappedValue);
                }

                if (areEqual) {
                    state.chunk.add(element);
                } else {
                    var chunk = List.copyOf(state.chunk);
                    state.chunk.clear();
                    state.chunk.add(element);
                    state.currentValue = mappedValue;
                    return downstream.push(chunk);
                }
            }

            return !downstream.isRejecting();
        });
    }

    @Override
    public BiConsumer<State<T, U>, Downstream<? super List<T>>> finisher() {
        return (state, downstream) -> {
            if (!state.chunk.isEmpty()) {
                var chunk = List.copyOf(state.chunk);
                downstream.push(chunk);
            }
        };
    }

    static class State<T, U> {
        final List<T> chunk = new ArrayList<>();
        U currentValue;
        boolean first = true;
    }
}
