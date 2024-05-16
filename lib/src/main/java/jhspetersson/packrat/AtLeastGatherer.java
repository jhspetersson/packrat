package jhspetersson.packrat;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * Returns distinct elements that appear at least <code>n</code> times in the stream.
 *
 * @param <T> element type
 * @param <U> mapped element type
 * @author jhspetersson
 */
@SuppressWarnings("preview")
class AtLeastGatherer<T, U> implements Gatherer<T, Map<? super U, Long>, T> {
    private final long atLeast;
    private final Function<? super T, ? extends U> mapper;

    AtLeastGatherer(long atLeast, Function<? super T, ? extends U> mapper) {
        this.atLeast = atLeast;
        this.mapper = mapper;
    }

    @Override
    public Supplier<Map<? super U, Long>> initializer() {
        return HashMap::new;
    }

    @Override
    public Integrator<Map<? super U, Long>, T, T> integrator() {
        return Integrator.of((state, element, downstream) -> {
            var mappedValue = mapper.apply(element);
            var count = state.getOrDefault(mappedValue, 0L);
            if (count + 1 == atLeast) {
                for (var i = 0; i < count + 1; i++) {
                    var res = downstream.push(element);
                    if (!res) {
                        return false;
                    }
                }
                state.put(mappedValue, count + 1);
            } else if (count + 1 >= atLeast) {
                return downstream.push(element);
            } else {
                state.put(mappedValue, count + 1);
            }
            return true;
        });
    }
}