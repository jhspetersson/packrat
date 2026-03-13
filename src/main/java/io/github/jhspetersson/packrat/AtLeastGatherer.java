package io.github.jhspetersson.packrat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import org.jspecify.annotations.NonNull;

/**
 * Returns distinct elements that appear at least <code>n</code> times in the stream.
 *
 * @param <T> element type
 * @param <U> mapped element type
 * @author jhspetersson
 */
class AtLeastGatherer<T, U> implements Gatherer<T, AtLeastGatherer.State<T, U>, T> {
    private final long atLeast;
    private final Function<? super T, ? extends U> mapper;

    AtLeastGatherer(long atLeast, @NonNull Function<? super T, ? extends U> mapper) {
        if (atLeast < 0) {
            throw new IllegalArgumentException("atLeast must be a non-negative number");
        }
        Objects.requireNonNull(mapper, "mapper cannot be null");

        this.atLeast = atLeast;
        this.mapper = mapper;
    }

    @Override
    public Supplier<State<T, U>> initializer() {
        return State::new;
    }

    @Override
    public Integrator<State<T, U>, T, T> integrator() {
        return Integrator.ofGreedy((state, element, downstream) -> {
            var mappedValue = mapper.apply(element);
            state.elements.add(element);
            state.counts.merge(mappedValue, 1L, Long::sum);
            return !downstream.isRejecting();
        });
    }

    @Override
    public BiConsumer<State<T, U>, Downstream<? super T>> finisher() {
        return (state, downstream) -> {
            for (var element : state.elements) {
                var mappedValue = mapper.apply(element);
                if (state.counts.get(mappedValue) >= atLeast) {
                    if (!downstream.push(element)) {
                        return;
                    }
                }
            }
        };
    }

    static class State<T, U> {
        final List<T> elements = new ArrayList<>();
        final Map<U, Long> counts = new HashMap<>();
    }
}