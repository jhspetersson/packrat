package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.text.BreakIterator;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new DistinctByGatherer<Integer, Integer>(Function.identity()).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new FilteringGatherer<Integer, Integer>(Function.identity(), 1, Objects::equals).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new FilterEntriesGatherer<Integer, Integer>((k, v) -> true).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new FilteringWithIndexGatherer<Integer>((index, element) -> true, 0).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new FlatMapGatherer<Integer>(Stream::of, element -> true).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new IdentityGatherer<Integer>().integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new MappingGatherer<Integer>(0, 1, Function.identity()).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new NthGatherer<Integer>(2).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new DropNthGatherer<Integer>(2).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new NCopiesGatherer<Integer>(2).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new PeekWithIndexGatherer<Integer>((index, element) -> {}, 0).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new ZipWithIndexGatherer<Integer, Integer>((index, element) -> element, 0).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new RemoveDuplicatesGatherer<Integer, Integer>(Function.identity()).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new ThrowIfNotOrderedGatherer<Integer, Integer>(Function.identity(), IllegalStateException::new, result -> result <= 0).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new RandomFilterGatherer<Integer>(0.5).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new SamplingGatherer<Integer>(2).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new EqualChunksGatherer<Integer, Integer>(Function.identity()).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new IncreasingDecreasingGatherer<Integer>(Comparator.naturalOrder(), result -> result < 0).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new IncreasingDecreasingChunksGatherer<Integer>(Comparator.naturalOrder(), result -> result < 0).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new WindowFixedWithIndexGatherer<Integer, Object>(2, (index, window) -> window, 0).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new WindowSlidingWithIndexGatherer<Integer, Object>(2, (index, window) -> window, 0).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new DropLastingGatherer<Integer>(0, false, Function.identity()).integrator());
        assertInstanceOf(Gatherer.Integrator.Greedy.class,
                new DropLastingGatherer<Integer>(3, false, Function.identity()).integrator());
    }

    @Test
    void shortCircuitingIntegratorsAreNotGreedy() {
        // zip stops as soon as the other input is exhausted
        assertFalse(new ZipGatherer<Integer, Integer, Integer>(List.of(1, 2, 3), (a, b) -> a).integrator()
                instanceof Gatherer.Integrator.Greedy);
        // last(0) cancels upstream immediately
        assertFalse(new LastingGatherer<Integer>(0).integrator()
                instanceof Gatherer.Integrator.Greedy);
    }
}
