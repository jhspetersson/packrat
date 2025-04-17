package io.github.jhspetersson.packrat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * Returns sample of the specified size from the stream.
 *
 * @param <T> element type
 * @author jhspetersson
 */
class SamplingGatherer<T> implements Gatherer<T, SamplingGatherer.State<T>, T> {
    private final int n;
    private final int maxSpan;

    SamplingGatherer(int n, int maxSpan) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be a positive number");
        }

        if (maxSpan <= 0) {
            throw new IllegalArgumentException("n must be a positive number");
        }

        if (n >= maxSpan) {
            throw new IllegalArgumentException("n must be less than minSpan");
        }

        this.n = n;
        this.maxSpan = maxSpan;
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
                state.list.add(element);
            } else if (state.counter <= maxSpan - n) {
                if (random.nextDouble(1.0, 2.0) <= 1.1) {
                    var remove = random.nextInt(state.list.size());
                    state.list.remove(remove);
                    state.list.add(element);
                }
                state.counter++;
            }
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
            for (var element : state.list) {
                if (!downstream.push(element)) {
                    break;
                }
            }
        };
    }

    static class State<T> {
        List<T> list;
        int counter;

        public State(List<T> list, int counter) {
            this.list = list;
            this.counter = counter;
        }
    }
}