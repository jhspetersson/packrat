package io.github.jhspetersson.packrat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

/**
 * Optimized gatherer for left rotation of elements in a stream.
 * It saves the first N elements (where N is the rotation distance) and then
 * outputs the rest of the elements first, followed by the saved elements.
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
            for (var element : state) {
                if (!downstream.push(element)) {
                    break;
                }
            }
        };
    }
}