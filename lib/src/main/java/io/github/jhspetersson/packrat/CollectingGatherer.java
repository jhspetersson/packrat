package io.github.jhspetersson.packrat;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Gatherer;

/**
 * Provides the result of the supplied collector as a single element into the stream.
 * Effectively converts any Collector into a Gatherer.
 *
 * @param <T> element type
 * @param <U> state type
 * @param <V> result type
 * @author jhspetersson
 */
public class CollectingGatherer<T, U, V> implements Gatherer<T, U, V> {
    private final Collector<? super T, U, ? extends V> collector;

    public CollectingGatherer(Collector<? super T, U, ? extends V> collector) {
        Objects.requireNonNull(collector, "collector cannot be null");

        this.collector = collector;
    }

    @Override
    public Supplier<U> initializer() {
        return collector.supplier();
    }

    @Override
    public Integrator<U, T, V> integrator() {
        return Integrator.ofGreedy((state, element, _) -> {
            collector.accumulator().accept(state, element);
            return true;
        });
    }

    @Override
    public BinaryOperator<U> combiner() {
        return collector.combiner();
    }

    @Override
    public BiConsumer<U, Gatherer.Downstream<? super V>> finisher() {
        return (state, downstream) -> downstream.push(collector.finisher().apply(state));
    }
}
