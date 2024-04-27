package jhspetersson.packrat;

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
class FilteringGatherer<T, U> implements Gatherer<T, Object, T> {
    private final Function<T, U> mapper;
    private final U value;
    private final BiPredicate<U, U> predicate;
    private final boolean invert;

    FilteringGatherer(Function<T, U> mapper, U value) {
        this(mapper, value, Objects::equals, false);
    }

    FilteringGatherer(Function<T, U> mapper, U value, boolean invert) {
        this(mapper, value, Objects::equals, invert);
    }

    FilteringGatherer(Function<T, U> mapper, U value, BiPredicate<U, U> predicate) {
        this(mapper, value, predicate, false);
    }

    FilteringGatherer(Function<T, U> mapper, U value, BiPredicate<U, U> predicate, boolean invert) {
        this.mapper = mapper;
        this.value = value;
        this.predicate = predicate;
        this.invert = invert;
    }

    @Override
    public Integrator<Object, T, T> integrator() {
        return Integrator.of((_, element, downstream) -> {
            var mappedValue = mapper.apply(element);
            var testResult = predicate.test(mappedValue, value);
            if (testResult ^ invert) {
                downstream.push(element);
            }
            return true;
        });
    }
}