package io.github.jhspetersson.packrat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * Repeats the whole stream <code>n</code> times.
 * <p>
 * The first copy is passed downstream as the elements arrive, while they are buffered for the remaining copies,
 * which are replayed once the upstream is exhausted. A short-circuiting downstream thus stops the upstream early.
 *
 * @param <T> element type
 * @author jhspetersson
 */
class RepeatGatherer<T> implements Gatherer<T, List<T>, T> {
    private final long n;

    /**
     * Constructs a new RepeatGatherer instance.
     *
     * @param n how many times to repeat the stream, value equal to zero effectively empties the stream
     * @throws IllegalArgumentException if <code>n</code> is negative
     */
    RepeatGatherer(long n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be a non-negative number");
        }
        this.n = n;
    }

    @Override
    public Supplier<List<T>> initializer() {
        return ArrayList::new;
    }

    @Override
    public Integrator<List<T>, T, T> integrator() {
        if (n == 0) {
            return (_, _, _) -> false;
        }

        return Integrator.ofGreedy((state, element, downstream) -> {
            state.add(element);
            return downstream.push(element);
        });
    }

    @Override
    public BiConsumer<List<T>, Gatherer.Downstream<? super T>> finisher() {
        return (state, downstream) -> {
            if (state.isEmpty() || downstream.isRejecting()) {
                return;
            }

            for (var i = 1L; i < n; i++) {
                for (T element : state) {
                    if (!downstream.push(element)) {
                        return;
                    }
                }
            }
        };
    }
}
