package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RemoveDuplicatesTest {
    @Test
    public void removeDuplicatesTest() {
        var listWithCopies = List.of(0, 1, 2, 2, 3, 4, 5, 5, 6, 7, 8, 8, 8, 9, 8, 7, 7, 6, 5, 4, 4, 4, 3, 2, 1, 0);
        var unique = listWithCopies.stream().gather(Packrat.removeDuplicates()).toList();
        
        var expected = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        
        assertEquals(expected, unique);
    }
    
    @Test
    public void emptyListTest() {
        var emptyList = List.<Integer>of();
        var result = emptyList.stream().gather(Packrat.removeDuplicates()).toList();
        
        assertEquals(List.of(), result);
    }
    
    @Test
    public void singleElementTest() {
        var singleElement = List.of(42);
        var result = singleElement.stream().gather(Packrat.removeDuplicates()).toList();
        
        assertEquals(List.of(42), result);
    }
    
    @Test
    public void allDuplicatesTest() {
        var allDuplicates = List.of(1, 1, 1, 1, 1);
        var result = allDuplicates.stream().gather(Packrat.removeDuplicates()).toList();
        
        assertEquals(List.of(1), result);
    }
    
    @Test
    public void noDuplicatesTest() {
        var noDuplicates = List.of(1, 2, 3, 4, 5);
        var result = noDuplicates.stream().gather(Packrat.removeDuplicates()).toList();
        
        assertEquals(List.of(1, 2, 3, 4, 5), result);
    }
}