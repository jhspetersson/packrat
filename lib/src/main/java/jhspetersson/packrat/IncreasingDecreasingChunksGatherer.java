package jhspetersson.packrat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * Filters all the elements going in some order specified by the supplied comparator.
 *
 * @param <T> element type
 * @author jhspetersson
 */
@SuppressWarnings("preview")
class IncreasingDecreasingChunksGatherer<T> implements Gatherer<T, List<T>, List<T>> {
    private final Comparator<? super T> comparator;
    private final Predicate<Integer> predicate;
    private T value;

    IncreasingDecreasingChunksGatherer(Comparator<? super T> comparator, Predicate<Integer> predicate) {
        this.comparator = comparator;
        this.predicate = predicate;
    }

    @Override
    public Supplier<List<T>> initializer() {
        return ArrayList::new;
    }

    @Override
    public Integrator<List<T>, T, List<T>> integrator() {
        return Integrator.of((state, element, downstream) -> {
            if (value == null) {
                value = element;
                state.add(element);
            } else {
                var result = comparator.compare(value, element);
                value = element;
                if (predicate.test(result)) {
                    state.add(element);
                } else {
                    var chunk = List.copyOf(state);
                    state.clear();
                    state.add(element);
                    return downstream.push(chunk);
                }
            }

            return true;
        });
    }

    @Override
    public BiConsumer<List<T>, Downstream<? super List<T>>> finisher() {
        return (state, downstream) -> {
            if (!state.isEmpty()) {
                var chunk = List.copyOf(state);
                downstream.push(chunk);
            }
        };
    }
}