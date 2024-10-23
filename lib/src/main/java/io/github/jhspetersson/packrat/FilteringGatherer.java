package io.github.jhspetersson.packrat;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Gatherer;

/**
 * Filters all the elements with some predicate or based on their equality to the specific value.
 *
 * @param <T> element type
 * @param <U> mapped element type
 * @author jhspetersson
 */
@SuppressWarnings("preview")
class FilteringGatherer<T, U> implements Gatherer<T, Void, T> {
    private final Function<? super T, ? extends U> mapper;
    private final U value;
    private final BiPredicate<? super U, ? super U> predicate;
    private final boolean invert;

    FilteringGatherer(Function<? super T, ? extends U> mapper, U value, BiPredicate<? super U, ? super U> predicate) {
        this(mapper, value, predicate, false);
    }

    FilteringGatherer(Function<? super T, ? extends U> mapper, U value, BiPredicate<? super U, ? super U> predicate, boolean invert) {
        Objects.requireNonNull(mapper, "mapper cannot be null");
        Objects.requireNonNull(predicate, "predicate cannot be null");

        this.mapper = mapper;
        this.value = value;
        this.predicate = predicate;
        this.invert = invert;
    }

    @Override
    public Integrator<Void, T, T> integrator() {
        return Integrator.of((_, element, downstream) -> {
            var mappedValue = mapper.apply(element);
            var testResult = predicate.test(mappedValue, value);
            if (testResult ^ invert) {
                return downstream.push(element);
            }
            return true;
        });
    }
}