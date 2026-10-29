package io.github.jhspetersson.packrat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import org.jspecify.annotations.NonNull;

/**
 * A {@link Gatherer} that drops trailing elements based on the last {@code n} unique keys
 * encountered in the upstream.
 * <p>
 * Underpins {@code Packrat.dropLastUnique(n)}, {@code Packrat.dropLastUniqueBy(n, mapper)} and
 * {@code Packrat.dropLastBy(n, mapper)}.
 * <p>
 * When {@code dropAllOccurrences} is {@code false} (the {@code dropLastUnique} /
 * {@code dropLastUniqueBy} behavior), only the final occurrence of each of the last {@code n}
 * unique keys is dropped. When {@code dropAllOccurrences} is {@code true} (the
 * {@code dropLastBy} behavior), every element whose key matches one of the last {@code n}
 * unique keys is dropped.
 *
 * @param <T> element type
 * @author jhspetersson
 */
class DropLastingGatherer<T> implements Gatherer<T, DropLastingGatherer.State<T>, T> {
    private final long n;
    private final boolean dropAllOccurrences;
    private final Function<? super T, ?> mapper;

    DropLastingGatherer(long n, boolean dropAllOccurrences, @NonNull Function<? super T, ?> mapper) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be a non-negative number");
        }

        Objects.requireNonNull(mapper, "mapper cannot be null");

        this.n = n;
        this.dropAllOccurrences = dropAllOccurrences;
        this.mapper = mapper;
    }

    @Override
    public Supplier<State<T>> initializer() {
        return State::new;
    }

    @Override
    public Integrator<State<T>, T, T> integrator() {
        if (n == 0) {
            return Integrator.ofGreedy((_, element, downstream) -> downstream.push(element));
        }

        return Integrator.ofGreedy((state, element, downstream) -> {
            state.elements.add(element);
            var key = mapper.apply(element);
            if (dropAllOccurrences) {
                state.keys.add(key);
            }
            state.lastIndexByKey.putLast(key, state.elements.size() - 1);
            if (state.lastIndexByKey.size() > n) {
                state.lastIndexByKey.pollFirstEntry();
            }
            return !downstream.isRejecting();
        });
    }

    @Override
    public BiConsumer<State<T>, Downstream<? super T>> finisher() {
        return (state, downstream) -> {
            if (n == 0) {
                return;
            }

            if (dropAllOccurrences) {
                for (var i = 0; i < state.elements.size(); i++) {
                    if (state.lastIndexByKey.containsKey(state.keys.get(i))) {
                        continue;
                    }

                    if (!downstream.push(state.elements.get(i))) {
                        return;
                    }
                }
            } else {
                // putLast keeps the last-occurrence indices in ascending order.
                var dropIndices = state.lastIndexByKey.values().iterator();
                var nextDropIndex = dropIndices.hasNext() ? dropIndices.next() : -1;

                for (var i = 0; i < state.elements.size(); i++) {
                    if (i == nextDropIndex) {
                        nextDropIndex = dropIndices.hasNext() ? dropIndices.next() : -1;
                        continue;
                    }

                    if (!downstream.push(state.elements.get(i))) {
                        return;
                    }
                }
            }
        };
    }

    static class State<T> {
        final List<T> elements = new ArrayList<>();
        final List<Object> keys = new ArrayList<>();
        final LinkedHashMap<Object, Integer> lastIndexByKey = new LinkedHashMap<>();
    }
}
