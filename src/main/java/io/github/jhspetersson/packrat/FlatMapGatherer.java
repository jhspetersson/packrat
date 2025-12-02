package io.github.jhspetersson.packrat;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;

/**
 * Optionally flattens elements mapped to streams depending on the supplied predicate.
 *
 * @param <T> element type
 * @author jhspetersson
 */
class FlatMapGatherer<T> implements Gatherer<T, Void, T> {
    private final Function<? super T, Stream<? extends T>> mapper;
    private final Predicate<? super T> predicate;

    FlatMapGatherer(@NonNull Function<? super T, Stream<? extends T>> mapper,
                    @NonNull Predicate<? super T> predicate) {
        Objects.requireNonNull(mapper, "mapper cannot be null");
        Objects.requireNonNull(predicate, "predicate cannot be null");

        this.mapper = mapper;
        this.predicate = predicate;
    }

    @Override
    public Integrator<Void, T, T> integrator() {
        return Integrator.of((_, element, downstream) -> {
            if (predicate.test(element)) {
                var stream = mapper.apply(element);
                for (var it = stream.iterator(); it.hasNext(); ) {
                    var elem = it.next();
                    var res = downstream.push(elem);
                    if (!res) {
                        return false;
                    }
                }
                return !downstream.isRejecting();
            } else {
                return downstream.push(element);
            }
        });
    }
}