package io.github.jhspetersson.packrat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
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

    SamplingGatherer(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be a non-negative number");
        }

        this.n = n;
    }

    @Override
    public Supplier<State<T>> initializer() {
        return () -> new State<>(new ArrayList<>(), 0L);
    }

    @Override
    public Integrator<State<T>, T, T> integrator() {
        var random = ThreadLocalRandom.current();
        return Integrator.ofGreedy((state, element, downstream) -> {
            if (state.list.size() < n) {
                state.list.add(new IndexedElement<>(state.counter, element));
            } else {
                var j = random.nextLong(state.counter + 1);
                if (j < n) {
                    state.list.set((int) j, new IndexedElement<>(state.counter, element));
                }
            }
            state.counter++;
            return !downstream.isRejecting();
        });
    }

    @Override
    public BiConsumer<State<T>, Downstream<? super T>> finisher() {
        return (state, downstream) -> {
            state.list.sort(Comparator.comparingLong(IndexedElement::index));
            for (var entry : state.list) {
                if (!downstream.push(entry.element())) {
                    break;
                }
            }
        };
    }

    record IndexedElement<T>(long index, T element) {}

    static class State<T> {
        List<IndexedElement<T>> list;
        long counter;

        public State(List<IndexedElement<T>> list, long counter) {
            this.list = list;
            this.counter = counter;
        }
    }
}
