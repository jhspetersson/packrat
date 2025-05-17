package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PeekWithIndexTest {
    @Test
    public void defaultTest() {
        var names = List.of("Anna", "Mike", "Sandra", "Rudolf", "Monica");
        var indices = new ArrayList<Long>();
        var elements = new ArrayList<String>();

        var result = names.stream()
                .gather(Packrat.peekWithIndex((index, element) -> {
                    indices.add(index);
                    elements.add(element);
                }))
                .toList();

        // Verify original elements are passed through
        assertEquals(names, result);
        
        // Verify consumer was called with correct indices and elements
        assertEquals(List.of(0L, 1L, 2L, 3L, 4L), indices);
        assertEquals(names, elements);
    }

    @Test
    public void startIndexTest() {
        var names = List.of("Anna", "Mike", "Sandra", "Rudolf", "Monica");
        var indices = new ArrayList<Long>();
        var elements = new ArrayList<String>();
        long startIndex = 10;

        var result = names.stream()
                .gather(Packrat.peekWithIndex((index, element) -> {
                    indices.add(index);
                    elements.add(element);
                }, startIndex))
                .toList();

        // Verify original elements are passed through
        assertEquals(names, result);
        
        // Verify consumer was called with correct indices and elements
        assertEquals(List.of(10L, 11L, 12L, 13L, 14L), indices);
        assertEquals(names, elements);
    }
}