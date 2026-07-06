package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.text.BreakIterator;
import java.util.function.Function;
import java.util.stream.Gatherer;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Integrators that never initiate a short-circuit on their own must be declared greedy,
 * so that the stream machinery can skip per-element cancellation bookkeeping.
 */
public class GreedyIntegratorTest {
    @Test
    void relayOnlyIntegratorsAreGreedy() {
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new MapWhileUntilGatherer<Integer>(Function.identity(), n -> true).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new BreakingGatherer<String>(BreakIterator.getWordInstance()).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new DropLastNGatherer<Integer>(3).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new DropLastNGatherer<Integer>(0).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new RotateLeftGatherer<Integer>(3).integrator());
    }
}
