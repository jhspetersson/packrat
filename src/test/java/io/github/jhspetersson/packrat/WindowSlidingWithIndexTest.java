package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WindowSlidingWithIndexTest {
    @Test
    public void windowSlidingWithIndexTest() {
        var numbers = IntStream.rangeClosed(1, 10).boxed();
        var result = numbers.gather(Packrat.windowSlidingWithIndex(3)).toList();
        
        assertEquals(8, result.size());
        
        assertEquals(0L, result.get(0).getKey());
        assertEquals(List.of(1, 2, 3), result.get(0).getValue());
        
        assertEquals(1L, result.get(1).getKey());
        assertEquals(List.of(2, 3, 4), result.get(1).getValue());
        
        assertEquals(7L, result.get(7).getKey());
        assertEquals(List.of(8, 9, 10), result.get(7).getValue());
    }
    
    @Test
    public void windowSlidingWithIndexCustomStartTest() {
        var numbers = IntStream.rangeClosed(1, 5).boxed();
        var result = numbers.gather(Packrat.windowSlidingWithIndex(2, 10)).toList();
        
        assertEquals(4, result.size());
        
        assertEquals(10L, result.get(0).getKey());
        assertEquals(List.of(1, 2), result.get(0).getValue());
        
        assertEquals(13L, result.get(3).getKey());
        assertEquals(List.of(4, 5), result.get(3).getValue());
    }
    
    @Test
    public void windowSlidingWithIndexCustomMapperTest() {
        var numbers = IntStream.rangeClosed(1, 5).boxed();
        var result = numbers.gather(Packrat.windowSlidingWithIndex(3, (index, window) -> 
            "Window " + index + ": " + window)).toList();
        
        assertEquals(3, result.size());
        assertEquals("Window 0: [1, 2, 3]", result.get(0));
        assertEquals("Window 1: [2, 3, 4]", result.get(1));
        assertEquals("Window 2: [3, 4, 5]", result.get(2));
    }
    
    @Test
    public void windowSlidingWithIndexCustomMapperAndStartTest() {
        var numbers = IntStream.rangeClosed(1, 5).boxed();
        var result = numbers.gather(Packrat.windowSlidingWithIndex(3, (index, window) -> 
            "Window " + index + ": " + window, 100)).toList();
        
        assertEquals(3, result.size());
        assertEquals("Window 100: [1, 2, 3]", result.get(0));
        assertEquals("Window 101: [2, 3, 4]", result.get(1));
        assertEquals("Window 102: [3, 4, 5]", result.get(2));
    }
    
    @Test
    public void windowSlidingWithIndexEmptySourceTest() {
        var numbers = Stream.<Integer>empty();
        var result = numbers.gather(Packrat.windowSlidingWithIndex(3)).toList();
        
        assertEquals(0, result.size());
    }
    
    @Test
    public void windowSlidingWithIndexInsufficientElementsTest() {
        var numbers = Stream.of(1, 2);
        var result = numbers.gather(Packrat.windowSlidingWithIndex(3)).toList();
        
        assertEquals(0, result.size());
    }
    
    @Test
    public void windowSlidingWithIndexInvalidWindowSizeTest() {
        assertThrows(IllegalArgumentException.class, () -> Packrat.windowSlidingWithIndex(0));
        assertThrows(IllegalArgumentException.class, () -> Packrat.windowSlidingWithIndex(-1));
    }
}