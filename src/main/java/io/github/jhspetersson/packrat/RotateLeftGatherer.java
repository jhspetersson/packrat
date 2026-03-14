package io.github.jhspetersson.packrat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * Rotates elements to the left by the given distance.
 * The distance is taken modulo the stream size, so distances larger
 * than the stream length are handled correctly.
 *
 * @param <T> element type
 * @author jhspetersson
 */
class RotateLeftGatherer<T> implements Gatherer<T, List<T>, T> {
    private final int distance;

    RotateLeftGatherer(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("distance must be positive");
        }
        this.distance = distance;
    }

    @Override
    public Supplier<List<T>> initializer() {
        return ArrayList::new;
    }

    @Override
    public Integrator<List<T>, T, T> integrator() {
        return (state, element, downstream) -> {
            if (state.size() < distance) {
                state.add(element);
            } else {
                if (!downstream.push(element)) {
                    return false;
                }
            }
            return !downstream.isRejecting();
        };
    }

    @Override
    public BiConsumer<List<T>, Downstream<? super T>> finisher() {
        return (state, downstream) -> {
            if (state.isEmpty()) {
                return;
            }

            var start = state.size() < distance ? distance % state.size() : 0;

            for (var i = start; i < state.size(); i++) {
                if (!downstream.push(state.get(i))) {
                    return;
                }
            }
            for (var i = 0; i < start; i++) {
                if (!downstream.push(state.get(i))) {
                    return;
                }
            }
        };
    }
}