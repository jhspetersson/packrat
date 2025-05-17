package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IdentityGathererTest {

    @Test
    void identityTest() {
        var original = new ArrayList<Integer>();
        IntStream.range(0, 100).forEach(original::add);

        var result = original.stream().gather(new IdentityGatherer<>()).toList();

        assertEquals(original.size(), result.size());
        for (int i = 0; i < original.size(); i++) {
            assertEquals(original.get(i), result.get(i));
        }
    }

    @Test
    void identityEmptyTest() {
        var original = new ArrayList<Integer>();

        var result = original.stream().gather(new IdentityGatherer<>()).toList();

        assertTrue(result.isEmpty());
    }

    @Test
    void identityParallelTest() {
        var size = 100000;
        var original = new ArrayList<Integer>();
        IntStream.range(0, size).forEach(original::add);

        var result = original.parallelStream().gather(new IdentityGatherer<>()).toList();

        assertEquals(size, result.size());
        assertEquals(size, Set.copyOf(result).size());

        // Sort both lists to ensure they contain the same elements
        var sortedOriginal = new ArrayList<>(original);
        sortedOriginal.sort(Integer::compareTo);

        var sortedResult = new ArrayList<>(result);
        sortedResult.sort(Integer::compareTo);

        assertEquals(sortedOriginal, sortedResult);
    }

    @Test
    void identityViaPackratRotateTest() {
        // Test the IdentityGatherer through Packrat.rotate(0)
        var original = new ArrayList<Integer>();
        IntStream.range(0, 10).forEach(original::add);

        var result = original.stream().gather(Packrat.rotate(0)).toList();

        assertEquals(original, result);
    }

    @Test
    void identityViaPackratIdentityTest() {
        // Test the IdentityGatherer through Packrat.identity()
        var original = new ArrayList<Integer>();
        IntStream.range(0, 10).forEach(original::add);

        var result = original.stream().gather(Packrat.identity()).toList();

        assertEquals(original, result);
    }
}
