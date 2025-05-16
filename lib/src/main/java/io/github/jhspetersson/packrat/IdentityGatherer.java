package io.github.jhspetersson.packrat;

import java.util.stream.Gatherer;

/**
 * A gatherer that passes elements through unchanged.
 * This is an identity operation that doesn't modify the stream elements.
 *
 * @param <T> element type
 * @author jhspetersson
 */
public class IdentityGatherer<T> implements Gatherer<T, Void, T> {
    @Override
    public Integrator<Void, T, T> integrator() {
        return Integrator.of((_, element, downstream) -> downstream.push(element));
    }
}
