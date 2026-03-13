package io.github.jhspetersson.packrat;

import java.text.BreakIterator;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import org.jspecify.annotations.NonNull;

/**
 * Returns strings such as graphemes, words, lines, or sentences parsed from the stream elements.
 *
 * @param <T> element type
 * @author jhspetersson
 */
class BreakingGatherer<T> implements Gatherer<T, BreakingGatherer.State, String> {
    private final BreakIterator breakIterator;
    private final boolean skipBlanks;

    BreakingGatherer(@NonNull BreakIterator breakIterator) {
        this(breakIterator, false);
    }

    BreakingGatherer(@NonNull BreakIterator breakIterator, boolean skipBlanks) {
        Objects.requireNonNull(breakIterator, "breakIterator cannot be null");

        this.breakIterator = breakIterator;
        this.skipBlanks = skipBlanks;
    }

    @Override
    public Supplier<State> initializer() {
        return () -> new State((BreakIterator) breakIterator.clone());
    }

    @Override
    public Integrator<State, T, String> integrator() {
        return Integrator.of((state, element, downstream) -> {
            if (element == null) {
                return downstream.push(null);
            } else {
                var str = element.toString();
                state.breakIterator.setText(str);

                var idx = state.breakIterator.first();
                var prevIdx = -1;
                while (idx != BreakIterator.DONE) {
                    if (prevIdx != -1) {
                        var part = str.substring(prevIdx, idx);
                        if (!skipBlanks || !part.isBlank()) {
                            var res = downstream.push(part);
                            if (!res) {
                                return false;
                            }
                        }
                    }

                    prevIdx = idx;
                    idx = state.breakIterator.next();
                }
            }
            return !downstream.isRejecting();
        });
    }

    static class State {
        final BreakIterator breakIterator;

        State(BreakIterator breakIterator) {
            this.breakIterator = breakIterator;
        }
    }
}