package io.github.jhspetersson.packrat;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import org.jspecify.annotations.NonNull;

/**
 * Returns all occurrences of elements that appear at least <code>n</code> times in the stream.
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
        return Integrator.of((state, element, downstream) -> {
            var counter = state.counts.computeIfAbsent(mapper.apply(element), _ -> new long[1]);
            counter[0]++;
            var elements = state.elements;
            if (elements.isEmpty() && counter[0] >= atLeast) {
                return downstream.push(element);
            }
            elements.addLast(new Entry<>(element, counter));
            Entry<T> head;
            while ((head = elements.peekFirst()) != null && head.counter()[0] >= atLeast) {
                elements.pollFirst();
                if (!downstream.push(head.element())) {
                    return false;
                }
            }
            return true;
        });
    }

    @Override
    public BiConsumer<State<T, U>, Downstream<? super T>> finisher() {
        return (state, downstream) -> {
            for (var entry : state.elements) {
                if (entry.counter()[0] >= atLeast) {
                    if (!downstream.push(entry.element())) {
                        return;
                    }
                }
            }
        };
    }

    /**
     * Buffered element with the counter shared by all the elements having the same mapped value.
     */
    record Entry<T>(T element, long[] counter) {}

    static class State<T, U> {
        final ArrayDeque<Entry<T>> elements = new ArrayDeque<>();
        final Map<U, long[]> counts = new HashMap<>();
    }
}