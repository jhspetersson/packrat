package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DropNthGathererTest {
    @Test
    public void dropEveryThirdElement() {
        var numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        var result = numbers.stream().gather(Packrat.dropNth(3)).toList();
        
        assertEquals(List.of(1, 2, 4, 5, 7, 8, 10), result);
    }
    
    @Test
    public void dropEverySecondElement() {
        var numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        var result = numbers.stream().gather(Packrat.dropNth(2)).toList();
        
        assertEquals(List.of(1, 3, 5, 7, 9), result);
    }
    
    @Test
    public void dropEveryElement() {
        var numbers = List.of(1, 2, 3, 4, 5);
        
        var result = numbers.stream().gather(Packrat.dropNth(1)).toList();
        
        assertEquals(List.of(), result);
    }
    
    @Test
    public void invalidNValueThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> Packrat.dropNth(0));
        assertThrows(IllegalArgumentException.class, () -> Packrat.dropNth(-1));
    }
    
    @Test
    public void emptyStreamReturnsEmptyList() {
        var result = IntStream.range(0, 0).boxed().gather(Packrat.dropNth(3)).toList();
        
        assertEquals(List.of(), result);
    }
}