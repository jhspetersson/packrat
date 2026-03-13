package io.github.jhspetersson.packrat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * Returns a sample of the specified size from the stream.
 *
 * @param <T> element type
 * @author jhspetersson
 */
class SamplingGatherer<T> implements Gatherer<T, SamplingGatherer.State<T>, T> {
    private final int n;

    SamplingGatherer(int n, int maxSpan) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be a non-negative number");
        }

        this.n = n;
    }

    @Override
    public Supplier<State<T>> initializer() {
        return () -> new State<>(new ArrayList<>(), 0);
    }

    @Override
    public Integrator<State<T>, T, T> integrator() {
        var random = ThreadLocalRandom.current();
        return Integrator.of((state, element, downstream) -> {
            if (state.list.size() < n) {
                state.list.add(new IndexedElement<>(state.counter, element));
            } else {
                var j = random.nextInt(state.counter + 1);
                if (j < n) {
                    state.list.set(j, new IndexedElement<>(state.counter, element));
                }
            }
            state.counter++;
            return !downstream.isRejecting();
        });
    }

    @Override
    public BinaryOperator<State<T>> combiner() {
        return (state, state2) -> {
            state.list.addAll(state2.list);
            return state;
        };
    }

    @Override
    public BiConsumer<State<T>, Downstream<? super T>> finisher() {
        return (state, downstream) -> {
            state.list.sort(Comparator.comparingInt(IndexedElement::index));
            for (var entry : state.list) {
                if (!downstream.push(entry.element())) {
                    break;
                }
            }
        };
    }

    record IndexedElement<T>(int index, T element) {}

    static class State<T> {
        List<IndexedElement<T>> list;
        int counter;

        public State(List<IndexedElement<T>> list, int counter) {
            this.list = list;
            this.counter = counter;
        }
    }
}
