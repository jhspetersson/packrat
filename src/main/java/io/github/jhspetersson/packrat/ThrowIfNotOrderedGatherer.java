package io.github.jhspetersson.packrat;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import org.jspecify.annotations.NonNull;

/**
 * Validates that incoming elements follow the specified order.
 * <p>
 * Elements are passed downstream unchanged. If an element violates the order relative to the
 * previous element, an exception supplied by the provided supplier is thrown immediately
 * and the pipeline fails.
 *
 * @param <T> element type
 * @param <U> mapped comparable type used for order validation
 * @author jhspetersson
 */
class ThrowIfNotOrderedGatherer<T, U extends Comparable<? super U>> implements Gatherer<T, ThrowIfNotOrderedGatherer.State<U>, T> {
    private final Function<? super T, ? extends U> mapper;
    private final Supplier<? extends RuntimeException> exceptionSupplier;
    private final Predicate<Integer> predicate;

    ThrowIfNotOrderedGatherer(@NonNull Function<? super T, ? extends U> mapper,
                              @NonNull Supplier<? extends RuntimeException> exceptionSupplier,
                              @NonNull Predicate<Integer> predicate) {
        Objects.requireNonNull(mapper, "mapper cannot be null");
        Objects.requireNonNull(exceptionSupplier, "exceptionSupplier cannot be null");
        Objects.requireNonNull(predicate, "predicate cannot be null");

        this.mapper = mapper;
        this.exceptionSupplier = exceptionSupplier;
        this.predicate = predicate;
    }

    @Override
    public Supplier<State<U>> initializer() {
        return State::new;
    }

    @Override
    public Integrator<State<U>, T, T> integrator() {
        return Integrator.of((state, element, downstream) -> {
            var mapped = mapper.apply(element);
            if (state.prev != null) {
                var result = state.prev.compareTo(mapped);
                if (!predicate.test(result)) {
                    throw exceptionSupplier.get();
                }
            }
            state.prev = mapped;
            return downstream.push(element);
        });
    }

    static class State<U> {
        private U prev;
    }
}
