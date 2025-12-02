package io.github.jhspetersson.packrat;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import org.jspecify.annotations.NonNull;

/**
 * Maps the elements until the condition holds (or not, if is inverted)
 *
 * @param <T> element type
 * @author jhspetersson
 */
class MapWhileUntilGatherer<T> implements Gatherer<T, boolean[], T> {
    private final Function<? super T, ? extends T> mapper;
    private final Predicate<? super T> whilePredicate;
    private final Predicate<? super T> untilPredicate;

    MapWhileUntilGatherer(@NonNull Function<? super T, ? extends T> mapper, Predicate<? super T> whilePredicate) {
        this(mapper, whilePredicate, null);
    }

    MapWhileUntilGatherer(Function<? super T, ? extends T> mapper, Predicate<? super T> whilePredicate, Predicate<? super T> untilPredicate) {
        Objects.requireNonNull(mapper, "mapper cannot be null");

        this.mapper = mapper;
        this.whilePredicate = whilePredicate;
        this.untilPredicate = untilPredicate;
    }

    @Override
    public Supplier<boolean[]> initializer() {
        return () -> new boolean[] { true, false };
    }

    @Override
    public Integrator<boolean[], T, T> integrator() {
        return Integrator.of((state, element, downstream) -> {
            if (state[0] && whilePredicate != null) {
                var testWhile = whilePredicate.test(element);
                if (!testWhile) {
                    state[0] = false;
                }
            }

            if (!state[1] && untilPredicate != null) {
                var testUntil = untilPredicate.test(element);
                if (testUntil) {
                    state[1] = true;
                }
            }

            var value = state[0] && !state[1]
                    ? mapper.apply(element)
                    : element;
            return downstream.push(value);
        });
    }
}