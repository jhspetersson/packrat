package jhspetersson.packrat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * Shuffles the element stream.
 * <p>
 *
 * <pre>
 *   var randomlyOrdered = IntStream.range(0, 10).boxed().gather(shuffle()).toList();
 *   System.out.println(randomlyOrdered);
 *
 *   [2, 7, 6, 9, 8, 5, 1, 3, 0, 4]
 * </pre>
 *
 * @param <T> element type
 * @author jhspetersson
 */
@SuppressWarnings("preview")
public class ShuffleGatherer<T> implements Gatherer<T, List<T>, T> {
    @Override
    public Supplier<List<T>> initializer() {
        return ArrayList::new;
    }

    @Override
    public Integrator<List<T>, T, T> integrator() {
        return Integrator.ofGreedy((state, element, _) -> {
            state.add(element);
            return true;
        });
    }

    @Override
    public BinaryOperator<List<T>> combiner() {
        return (state, state2) -> {
            state.addAll(state2);
            return state;
        };
    }

    @Override
    public BiConsumer<List<T>, Downstream<? super T>> finisher() {
        return (state, downstream) -> {
            Collections.shuffle(state);
            state.forEach(downstream::push);
        };
    }
}
