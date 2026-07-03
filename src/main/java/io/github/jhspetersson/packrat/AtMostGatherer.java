package io.github.jhspetersson.packrat;

import java.util.ArrayList;
import java.util.Comparator;
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
 * Returns elements that appear at most <code>n</code> times in the stream.
 *
 * @param <T> element type
 * @param <U> mapped element type
 * @author jhspetersson
 */
class AtMostGatherer<T, U> implements Gatherer<T, AtMostGatherer.State<T, U>, T> {
    private final long atMost;
    private final Function<? super T, ? extends U> mapper;

    AtMostGatherer(long atMost, @NonNull Function<? super T, ? extends U> mapper) {
        if (atMost < 0) {
            throw new IllegalArgumentException("atMost must be a non-negative number");
        }
        Objects.requireNonNull(mapper, "mapper cannot be null");

        this.atMost = atMost;
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
            var count = state.counts.merge(mappedValue, 1L, Long::sum);
            if (count <= atMost) {
                state.elementsByKey.computeIfAbsent(mappedValue, _ -> new ArrayList<>())
                        .add(new IndexedElement<>(state.index, element));
            } else {
                // the key is disqualified for good, its buffered elements can never be emitted
                state.elementsByKey.remove(mappedValue);
            }
            state.index++;
            return !downstream.isRejecting();
        });
    }

    @Override
    public BiConsumer<State<T, U>, Downstream<? super T>> finisher() {
        return (state, downstream) -> {
            var elements = new ArrayList<IndexedElement<T>>();
            for (var keyElements : state.elementsByKey.values()) {
                elements.addAll(keyElements);
            }
            elements.sort(Comparator.comparingLong(IndexedElement::index));

            for (var entry : elements) {
                if (!downstream.push(entry.element())) {
                    return;
                }
            }
        };
    }

    record IndexedElement<T>(long index, T element) {}

    static class State<T, U> {
        final Map<U, List<IndexedElement<T>>> elementsByKey = new HashMap<>();
        final Map<U, Long> counts = new HashMap<>();
        long index;
    }
}
