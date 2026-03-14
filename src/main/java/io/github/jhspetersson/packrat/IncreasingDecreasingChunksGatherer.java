package io.github.jhspetersson.packrat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import org.jspecify.annotations.NonNull;

/**
 * Returns lists ("chunks") of elements, where each next element is less/greater and, optionally equal than the previous one.
 * Comparison is done with the supplied comparator.
 * Null elements are supported when a null-safe comparator is provided.
 *
 * @param <T> element type
 * @author jhspetersson
 */
class IncreasingDecreasingChunksGatherer<T> implements Gatherer<T, IncreasingDecreasingChunksGatherer.State<T>, List<T>> {
    private final Comparator<? super T> comparator;
    private final Predicate<Integer> predicate;

    IncreasingDecreasingChunksGatherer(@NonNull Comparator<? super T> comparator,
                                       @NonNull Predicate<Integer> predicate) {
        Objects.requireNonNull(comparator, "comparator cannot be null");
        Objects.requireNonNull(predicate, "predicate cannot be null");

        this.comparator = comparator;
        this.predicate = predicate;
    }

    @Override
    public Supplier<State<T>> initializer() {
        return State::new;
    }

    @Override
    public Integrator<State<T>, T, List<T>> integrator() {
        return Integrator.of((state, element, downstream) -> {
            if (state.first) {
                state.first = false;
                state.value = element;
                state.chunk.add(element);
            } else {
                var result = comparator.compare(state.value, element);
                state.value = element;
                if (predicate.test(result)) {
                    state.chunk.add(element);
                } else {
                    var chunk = Collections.unmodifiableList(new ArrayList<>(state.chunk));
                    state.chunk.clear();
                    state.chunk.add(element);
                    return downstream.push(chunk);
                }
            }

            return !downstream.isRejecting();
        });
    }

    @Override
    public BiConsumer<State<T>, Downstream<? super List<T>>> finisher() {
        return (state, downstream) -> {
            if (!state.chunk.isEmpty()) {
                var chunk = Collections.unmodifiableList(new ArrayList<>(state.chunk));
                downstream.push(chunk);
            }
        };
    }

    static class State<T> {
        final List<T> chunk = new ArrayList<>();
        T value;
        boolean first = true;
    }
}
