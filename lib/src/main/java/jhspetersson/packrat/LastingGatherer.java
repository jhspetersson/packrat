package jhspetersson.packrat;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * Returns last n elements.
 *
 * @param <T> element type
 * @author jhspetersson
 */
@SuppressWarnings("preview")
class LastingGatherer<T> implements Gatherer<T, Deque<T>, T> {
    private final long n;

    LastingGatherer(long n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be a positive number");
        }

        this.n = n;
    }

    @Override
    public Supplier<Deque<T>> initializer() {
        return ArrayDeque::new;
    }

    @Override
    public Integrator<Deque<T>, T, T> integrator() {
        return Integrator.ofGreedy((state, element, _) -> {
            if (state.size() == n) {
                state.removeFirst();
            }
            state.add(element);
            return true;
        });
    }

    @Override
    public BiConsumer<Deque<T>, Downstream<? super T>> finisher() {
        return (state, downstream) -> {
            for (var element : state) {
                if (!downstream.push(element)) {
                    break;
                }
            }
        };
    }
}