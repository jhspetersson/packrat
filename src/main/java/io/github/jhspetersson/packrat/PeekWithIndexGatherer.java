package io.github.jhspetersson.packrat;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import org.jspecify.annotations.NonNull;

/**
 * Peeks at each element along with its index, but passes the original element downstream.
 *
 * @param <T> element type
 * @author jhspetersson
 */
class PeekWithIndexGatherer<T> implements Gatherer<T, long[], T> {
    private final BiConsumer<Long, ? super T> consumer;
    private final long startIndex;

    PeekWithIndexGatherer(@NonNull BiConsumer<Long, ? super T> consumer, long startIndex) {
        Objects.requireNonNull(consumer, "consumer cannot be null");

        this.consumer = consumer;
        this.startIndex = startIndex;
    }

    @Override
    public Supplier<long[]> initializer() {
        return () -> new long[] { startIndex };
    }

    @Override
    public Integrator<long[], T, T> integrator() {
        return Integrator.of((state, element, downstream) -> {
            consumer.accept(state[0]++, element);
            return downstream.push(element);
        });
    }
}