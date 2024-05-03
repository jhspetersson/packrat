package jhspetersson.packrat;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * Returns elements with optional mapping applied to the specified number of them.
 *
 * @param <T> element type
 * @author jhspetersson
 */
@SuppressWarnings("preview")
class MappingGatherer<T> implements Gatherer<T, Long[], T> {
    private final long skipN;
    private final long mapN;
    private final Function<? super T, ? extends T> mapper;

    MappingGatherer(long skipN, long mapN, Function<? super T, ? extends T> mapper) {
        this.skipN = skipN;
        this.mapN = mapN;
        this.mapper = mapper;
    }

    @Override
    public Supplier<Long[]> initializer() {
        return () -> new Long[] { 0L, 0L };
    }

    @Override
    public Integrator<Long[], T, T> integrator() {
        return Integrator.of((state, element, downstream) -> {
            if (skipN > 0 && state[0] < skipN) {
                state[0] += 1;
                downstream.push(element);
            } else {
                if (mapN < 0 || state[1] < mapN) {
                    state[1] += 1;
                    var mappedValue = mapper.apply(element);
                    downstream.push(mappedValue);
                } else {
                    downstream.push(element);
                }
            }
            return true;
        });
    }
}