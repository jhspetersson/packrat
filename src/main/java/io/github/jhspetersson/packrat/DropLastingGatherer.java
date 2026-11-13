package io.github.jhspetersson.packrat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import org.jspecify.annotations.NonNull;

/**
 * A {@link Gatherer} that drops trailing elements based on the last {@code n} unique keys
 * encountered in the upstream.
 * <p>
 * Underpins {@code Packrat.dropLastUnique(n)}, {@code Packrat.dropLastUniqueBy(n, mapper)} and
 * {@code Packrat.dropLastBy(n, mapper)}.
 * <p>
 * When {@code dropAllOccurrences} is {@code false} (the {@code dropLastUnique} /
 * {@code dropLastUniqueBy} behavior), only the final occurrence of each of the last {@code n}
 * unique keys is dropped. When {@code dropAllOccurrences} is {@code true} (the
 * {@code dropLastBy} behavior), every element whose key matches one of the last {@code n}
 * unique keys is dropped.
 * <p>
 * In the first mode elements are passed downstream as soon as it is known that they will not be dropped:
 * an element is released once its key occurs again or {@code n} other unique keys follow it, and all
 * the preceding elements are released. In the second mode the whole upstream is buffered, since any key
 * may still occur among the last {@code n} unique ones.
 *
 * @param <T> element type
 * @author jhspetersson
 */
class DropLastingGatherer<T> implements Gatherer<T, DropLastingGatherer.State<T>, T> {
    private final long n;
    private final boolean dropAllOccurrences;
    private final Function<? super T, ?> mapper;

    DropLastingGatherer(long n, boolean dropAllOccurrences, @NonNull Function<? super T, ?> mapper) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be a non-negative number");
        }

        Objects.requireNonNull(mapper, "mapper cannot be null");

        this.n = n;
        this.dropAllOccurrences = dropAllOccurrences;
        this.mapper = mapper;
    }

    @Override
    public Supplier<State<T>> initializer() {
        return State::new;
    }

    @Override
    public Integrator<State<T>, T, T> integrator() {
        if (n == 0) {
            return Integrator.ofGreedy((_, element, downstream) -> downstream.push(element));
        }

        if (dropAllOccurrences) {
            return Integrator.ofGreedy((state, element, downstream) -> {
                state.elements.add(element);
                var key = mapper.apply(element);
                state.keys.add(key);
                state.lastIndexByKey.putLast(key, state.count++);
                if (state.lastIndexByKey.size() > n) {
                    state.lastIndexByKey.pollFirstEntry();
                }
                return !downstream.isRejecting();
            });
        }

        return Integrator.ofGreedy((state, element, downstream) -> {
            var key = mapper.apply(element);
            state.elements.add(element);
            state.lastIndexByKey.putLast(key, state.count++);
            if (state.lastIndexByKey.size() > n) {
                state.lastIndexByKey.pollFirstEntry();
            }

            // putLast keeps the last-occurrence indices in ascending order, so everything
            // before the first of them can never be dropped.
            long firstDropIndex = state.lastIndexByKey.firstEntry().getValue();
            if (state.emitted == firstDropIndex) {
                return !downstream.isRejecting();
            }
            while (state.emitted < firstDropIndex) {
                if (!downstream.push(state.poll())) {
                    return false;
                }
            }
            return true;
        });
    }

    @Override
    public BiConsumer<State<T>, Downstream<? super T>> finisher() {
        return (state, downstream) -> {
            if (n == 0) {
                return;
            }

            if (dropAllOccurrences) {
                for (var i = 0; i < state.elements.size(); i++) {
                    if (state.lastIndexByKey.containsKey(state.keys.get(i))) {
                        continue;
                    }
                    if (!downstream.push(state.elements.get(i))) {
                        return;
                    }
                }
            } else {
                // putLast keeps the last-occurrence indices in ascending order.
                var dropIndices = state.lastIndexByKey.values().iterator();
                var nextDropIndex = dropIndices.hasNext() ? dropIndices.next() : -1L;
                while (state.emitted < state.count) {
                    if (state.emitted == nextDropIndex) {
                        nextDropIndex = dropIndices.hasNext() ? dropIndices.next() : -1L;
                        state.poll();
                        continue;
                    }
                    if (!downstream.push(state.poll())) {
                        return;
                    }
                }
            }
        };
    }

    static class State<T> {
        /** Buffered elements; in the lazy mode the ones before {@link #head} are already released. */
        final List<T> elements = new ArrayList<>();
        final List<Object> keys = new ArrayList<>();
        /** Last-occurrence stream indices of the most recent unique keys, in ascending order. */
        final LinkedHashMap<Object, Long> lastIndexByKey = new LinkedHashMap<>();
        /** Number of elements seen so far. */
        long count;
        /** Stream index of the next buffered element to release (lazy mode only). */
        long emitted;
        /** Position of that element in {@link #elements}. */
        int head;

        /**
         * Removes and returns the oldest buffered element.
         */
        T poll() {
            var element = elements.set(head++, null);
            emitted++;
            if (head >= 1024 && head >= elements.size() - head) {
                elements.subList(0, head).clear();
                head = 0;
            }
            return element;
        }
    }
}
