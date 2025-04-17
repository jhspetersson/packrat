package io.github.jhspetersson.packrat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * Returns elements that appear at most <code>n</code> times in the stream.
 *
 * @param <T> element type
 * @param <U> mapped element type
 * @author jhspetersson
 */
class AtMostGatherer<T, U> implements Gatherer<T, Map<? super U, List<T>>, T> {
    private final long atMost;
    private final Function<? super T, ? extends U> mapper;

    AtMostGatherer(long atMost, Function<? super T, ? extends U> mapper) {
        if (atMost < 0) {
            throw new IllegalArgumentException("atMost must be a positive number");
        }
        Objects.requireNonNull(mapper, "mapper cannot be null");

        this.atMost = atMost;
        this.mapper = mapper;
    }

    @Override
    public Supplier<Map<? super U, List<T>>> initializer() {
        return HashMap::new;
    }

    @Override
    public Integrator<Map<? super U, List<T>>, T, T> integrator() {
        return Integrator.ofGreedy((state, element, _) -> {
            var mappedValue = mapper.apply(element);
            var elementList = state.computeIfAbsent(mappedValue, _ -> new ArrayList<>());
            if (elementList.size() <= atMost) {
                elementList.add(element);
            }
            return true;
        });
    }

    @Override
    public BiConsumer<Map<? super U, List<T>>, Downstream<? super T>> finisher() {
        return (state, downstream) -> {
            for (var entry : state.entrySet()) {
                var elementList = entry.getValue();
                if (elementList.size() <= atMost) {
                    for (var element : elementList) {
                        if (!downstream.push(element)) {
                            return;
                        }
                    }
                }
            }
        };
    }
}
