package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WindowFixedWithIndexTest {
    @Test
    public void windowFixedWithIndexTest() {
        var numbers = IntStream.rangeClosed(1, 10).boxed();
        var result = numbers.gather(Packrat.windowFixedWithIndex(3)).toList();
        
        assertEquals(3, result.size());
        
        assertEquals(0L, result.get(0).getKey());
        assertEquals(List.of(1, 2, 3), result.get(0).getValue());
        
        assertEquals(1L, result.get(1).getKey());
        assertEquals(List.of(4, 5, 6), result.get(1).getValue());
        
        assertEquals(2L, result.get(2).getKey());
        assertEquals(List.of(7, 8, 9), result.get(2).getValue());
    }
    
    @Test
    public void windowFixedWithIndexRemainingElementsTest() {
        var numbers = IntStream.rangeClosed(1, 11).boxed();
        var result = numbers.gather(Packrat.windowFixedWithIndex(3)).toList();
        
        assertEquals(3, result.size());
        
        // The last element (11) is not included in any window because it doesn't form a complete window
        assertEquals(0L, result.get(0).getKey());
        assertEquals(List.of(1, 2, 3), result.get(0).getValue());
        
        assertEquals(1L, result.get(1).getKey());
        assertEquals(List.of(4, 5, 6), result.get(1).getValue());
        
        assertEquals(2L, result.get(2).getKey());
        assertEquals(List.of(7, 8, 9), result.get(2).getValue());
    }
    
    @Test
    public void windowFixedWithIndexCustomStartTest() {
        var numbers = IntStream.rangeClosed(1, 6).boxed();
        var result = numbers.gather(Packrat.windowFixedWithIndex(2, 10)).toList();
        
        assertEquals(3, result.size());
        
        assertEquals(10L, result.get(0).getKey());
        assertEquals(List.of(1, 2), result.get(0).getValue());
        
        assertEquals(11L, result.get(1).getKey());
        assertEquals(List.of(3, 4), result.get(1).getValue());
        
        assertEquals(12L, result.get(2).getKey());
        assertEquals(List.of(5, 6), result.get(2).getValue());
    }
    
    @Test
    public void windowFixedWithIndexCustomMapperTest() {
        var numbers = IntStream.rangeClosed(1, 6).boxed();
        var result = numbers.gather(Packrat.windowFixedWithIndex(3, (index, window) -> 
            "Window " + index + ": " + window)).toList();
        
        assertEquals(2, result.size());
        assertEquals("Window 0: [1, 2, 3]", result.get(0));
        assertEquals("Window 1: [4, 5, 6]", result.get(1));
    }
    
    @Test
    public void windowFixedWithIndexCustomMapperAndStartTest() {
        var numbers = IntStream.rangeClosed(1, 6).boxed();
        var result = numbers.gather(Packrat.windowFixedWithIndex(3, (index, window) -> 
            "Window " + index + ": " + window, 100)).toList();
        
        assertEquals(2, result.size());
        assertEquals("Window 100: [1, 2, 3]", result.get(0));
        assertEquals("Window 101: [4, 5, 6]", result.get(1));
    }
    
    @Test
    public void windowFixedWithIndexEmptySourceTest() {
        var numbers = Stream.<Integer>empty();
        var result = numbers.gather(Packrat.windowFixedWithIndex(3)).toList();
        
        assertEquals(0, result.size());
    }
    
    @Test
    public void windowFixedWithIndexInsufficientElementsTest() {
        var numbers = Stream.of(1, 2);
        var result = numbers.gather(Packrat.windowFixedWithIndex(3)).toList();
        
        assertEquals(0, result.size());
    }
    
    @Test
    public void windowFixedWithIndexInvalidWindowSizeTest() {
        assertThrows(IllegalArgumentException.class, () -> Packrat.windowFixedWithIndex(0));
        assertThrows(IllegalArgumentException.class, () -> Packrat.windowFixedWithIndex(-1));
    }
}