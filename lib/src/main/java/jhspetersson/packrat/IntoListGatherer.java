package jhspetersson.packrat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

@SuppressWarnings("preview")
class IntoListGatherer<T> implements Gatherer<T, List<T>, T> {
    private final Consumer<List<T>> consumer;

    IntoListGatherer(Consumer<List<T>> consumer) {
        this.consumer = consumer;
    }

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
            consumer.accept(state);
            state.forEach(downstream::push);
        };
    }
}