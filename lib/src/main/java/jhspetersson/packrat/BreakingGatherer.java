package jhspetersson.packrat;

import java.text.BreakIterator;
import java.util.stream.Gatherer;

/**
 * Returns strings such as graphemes, words, lines or sentences parsed from the stream elements.
 *
 * @param <T> element type
 * @author jhspetersson
 */
@SuppressWarnings("preview")
class BreakingGatherer<T> implements Gatherer<T, Void, String> {
    private final BreakIterator breakIterator;
    private final boolean skipBlanks;

    BreakingGatherer(BreakIterator breakIterator) {
        this(breakIterator, false);
    }

    BreakingGatherer(BreakIterator breakIterator, boolean skipBlanks) {
        this.breakIterator = breakIterator;
        this.skipBlanks = skipBlanks;
    }

    @Override
    public Integrator<Void, T, String> integrator() {
        return Integrator.of((_, element, downstream) -> {
            if (element == null) {
                downstream.push(null);
            } else {
                var str = element.toString();
                breakIterator.setText(str);

                var idx = breakIterator.first();
                var prevIdx = -1;
                while (idx != BreakIterator.DONE) {
                    if (prevIdx != -1) {
                        var part = str.substring(prevIdx, idx);
                        if (!skipBlanks || !part.isBlank()) {
                            downstream.push(part);
                        }
                    }

                    prevIdx = idx;
                    idx = breakIterator.next();
                }
            }
            return true;
        });
    }
}