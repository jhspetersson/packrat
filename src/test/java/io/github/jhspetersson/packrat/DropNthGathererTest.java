package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DropNthGathererTest {
    @Test
    public void dropNthShouldKeepCadenceBeyondMaxIntElements() {
        var n = 5;
        var gatherer = new DropNthGatherer<Integer>(n);
        var integrator = gatherer.integrator();

        // simulate having already consumed 2^31 - 3 elements
        var state = new long[] { Integer.MAX_VALUE - 2 };
        var pushCount = new int[1];
        var dropped = new java.util.ArrayList<Long>();
        Gatherer.Downstream<Integer> downstream = _ -> {
            pushCount[0]++;
            return true;
        };

        for (var i = 0; i < 10; i++) {
            var pushesBefore = pushCount[0];
            integrator.integrate(state, 0, downstream);
            if (pushCount[0] == pushesBefore) {
                dropped.add(state[0]);
            }
        }

        // dropped positions must stay exact multiples of n across the int boundary
        for (var position : dropped) {
            assertEquals(0, position % n, "dropped wrong position " + position);
        }
        assertEquals(2, dropped.size());
    }

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