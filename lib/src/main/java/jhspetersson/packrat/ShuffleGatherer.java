package jhspetersson.packrat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

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
