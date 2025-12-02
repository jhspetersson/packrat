package io.github.jhspetersson.packrat;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import org.jspecify.annotations.NonNull;

/**
 * Outputs the greatest or the smallest element in the stream, comparing is done after mapping function applied.
 *
 * @param <T> element type
 * @param <U> mapped element type
 * @author jhspetersson
 */
class MinMaxGatherer<T, U> implements Gatherer<T, MinMaxGatherer.State<T, U>, T> {
    private final Function<? super T, ? extends U> mapper;
    private final Comparator<? super U> comparator;
    private final Predicate<Integer> predicate;

    MinMaxGatherer(@NonNull Function<? super T, ? extends U> mapper,
                   @NonNull Comparator<? super U> comparator,
                   @NonNull Predicate<Integer> predicate) {
        Objects.requireNonNull(mapper, "mapper cannot be null");
        Objects.requireNonNull(comparator, "comparator cannot be null");
        Objects.requireNonNull(predicate, "predicate cannot be null");

        this.mapper = mapper;
        this.comparator = comparator;
        this.predicate = predicate;
    }

    @Override
    public Supplier<State<T, U>> initializer() {
        return State::new;
    }

    @Override
    public Integrator<State<T, U>, T, T> integrator() {
        return Integrator.ofGreedy((state, element, downstream) -> {
            var mappedValue = mapper.apply(element);
            var stateMappedElement = state.mappedElement;
            if (stateMappedElement != null) {
                var result = comparator.compare(mappedValue, stateMappedElement);
                if (predicate.test(result)) {
                    state.element = element;
                    state.mappedElement = mappedValue;
                }
            } else {
                state.element = element;
                state.mappedElement = mappedValue;
            }
            return !downstream.isRejecting();
        });
    }

    @Override
    public BiConsumer<State<T, U>, Downstream<? super T>> finisher() {
        return (state, downstream) -> {
            var element = state.element;
            if (element != null) {
                downstream.push(element);
            }
        };
    }

    static class State<T, U> {
        T element;
        U mappedElement;
    }
}