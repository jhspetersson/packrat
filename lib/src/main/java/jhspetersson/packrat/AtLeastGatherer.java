package jhspetersson.packrat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
class AtLeastGatherer<T, U> implements Gatherer<T, Map<? super U, List<T>>, T> {
    private final long atLeast;
    private final Function<? super T, ? extends U> mapper;

    AtLeastGatherer(long atLeast, Function<? super T, ? extends U> mapper) {
        this.atLeast = atLeast;
        this.mapper = mapper;
    }

    @Override
    public Supplier<Map<? super U, List<T>>> initializer() {
        return HashMap::new;
    }

    @Override
    public Integrator<Map<? super U, List<T>>, T, T> integrator() {
        return Integrator.of((state, element, downstream) -> {
            var mappedValue = mapper.apply(element);
            var elementList = state.computeIfAbsent(mappedValue, _ -> new ArrayList<>());
            var count = elementList.size();
            if (count + 1 == atLeast) {
                for (var t : elementList) {
                    var res = downstream.push(t);
                    if (!res) {
                        return false;
                    }
                }
                elementList.add(element);
                return downstream.push(element);
            } else if (count + 1 >= atLeast) {
                return downstream.push(element);
            } else {
                elementList.add(element);
            }
            return true;
        });
    }
}