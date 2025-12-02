package io.github.jhspetersson.packrat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import org.jspecify.annotations.NonNull;

/**
 * Returns fixed-size non-overlapping windows of elements along with their indices.
 * Each window contains a fixed number of elements and is emitted as a list.
 * Windows are not sliding but go one after another with a step equal to the window's size.
 *
 * @param <T> element type
 * @param <R> result type
 * @author jhspetersson
 */
class WindowFixedWithIndexGatherer<T, R> implements Gatherer<T, WindowFixedWithIndexGatherer.State<T>, R> {
    private final int windowSize;
    private final BiFunction<Long, List<T>, ? extends R> mapper;
    private final long startIndex;

    /**
     * Creates a new WindowFixedWithIndexGatherer with the specified window size and mapper function.
     *
     * @param windowSize the size of each window
     * @param mapper the function to map each window with its index to a result
     * @param startIndex the starting index
     */
    WindowFixedWithIndexGatherer(int windowSize, @NonNull BiFunction<Long, List<T>, ? extends R> mapper, long startIndex) {
        if (windowSize <= 0) {
            throw new IllegalArgumentException("windowSize must be positive");
        }
        Objects.requireNonNull(mapper, "mapper cannot be null");

        this.windowSize = windowSize;
        this.mapper = mapper;
        this.startIndex = startIndex;
    }

    @Override
    public Supplier<State<T>> initializer() {
        return () -> new State<>(windowSize, startIndex);
    }

    @Override
    public Integrator<State<T>, T, R> integrator() {
        return Integrator.of((state, element, downstream) -> {
            state.window.add(element);
            
            if (state.window.size() == windowSize) {
                var windowCopy = new ArrayList<>(state.window);
                var result = mapper.apply(state.index++, windowCopy);
                state.window.clear();
                
                return downstream.push(result);
            }
            
            return !downstream.isRejecting();
        });
    }

    /**
     * State class for the WindowFixedWithIndexGatherer.
     *
     * @param <T> element type
     */
    static class State<T> {
        private final List<T> window;
        private long index;

        State(int windowSize, long startIndex) {
            this.window = new ArrayList<>(windowSize);
            this.index = startIndex;
        }
    }
}