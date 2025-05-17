package io.github.jhspetersson.packrat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * Collects the whole stream and repeats it <code>n</code> times.
 *
 * @param <T> element type
 * @author jhspetersson
 */
public class RepeatGatherer<T> implements Gatherer<T, List<T>, T> {
    private final long n;

    /**
     * Constructs a new RepeatGatherer instance.
     *
     * @param n how many times to repeat the stream, value equal to zero effectively empties the stream
     * @throws IllegalArgumentException if <code>n</code> is negative
     */
    public RepeatGatherer(long n) {
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
        return Integrator.ofGreedy((state, element, downstream) -> {
            state.add(element);
            return !downstream.isRejecting();
        });
    }

    @Override
    public BiConsumer<List<T>, Gatherer.Downstream<? super T>> finisher() {
        return (state, downstream) -> {
            for (var i = 0L; i < n; i++) {
                for (T element : state) {
                    if (!downstream.push(element)) {
                        return;
                    }
                }
            }
        };
    }
}