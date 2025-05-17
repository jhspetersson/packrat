package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilterEntriesTest {
    @Test
    public void filterEntriesTest() {
        var map = new HashMap<String, Integer>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.put("four", 4);
        map.put("five", 5);

        // Filter entries where the value is even
        var evenValues = map.entrySet().stream()
                .gather(Packrat.filterEntries((key, value) -> value % 2 == 0))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertEquals(2, evenValues.size());
        assertTrue(evenValues.containsKey("two"));
        assertTrue(evenValues.containsKey("four"));
        assertEquals(2, evenValues.get("two"));
        assertEquals(4, evenValues.get("four"));

        // Filter entries where the key starts with 't'
        var tKeys = map.entrySet().stream()
                .gather(Packrat.filterEntries((key, value) -> key.startsWith("t")))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertEquals(2, tKeys.size());
        assertTrue(tKeys.containsKey("two"));
        assertTrue(tKeys.containsKey("three"));
        assertEquals(2, tKeys.get("two"));
        assertEquals(3, tKeys.get("three"));

        // Filter entries where key length equals value
        var keyLengthEqualsValue = map.entrySet().stream()
                .gather(Packrat.filterEntries((key, value) -> key.length() == value))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertEquals(1, keyLengthEqualsValue.size());
        assertTrue(keyLengthEqualsValue.containsKey("four"));
        assertEquals(4, keyLengthEqualsValue.get("four"));
    }

    @Test
    public void removeEntriesTest() {
        var map = new HashMap<String, Integer>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.put("four", 4);
        map.put("five", 5);

        // Remove entries where the value is even
        var oddValues = map.entrySet().stream()
                .gather(Packrat.removeEntries((key, value) -> value % 2 == 0))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertEquals(3, oddValues.size());
        assertTrue(oddValues.containsKey("one"));
        assertTrue(oddValues.containsKey("three"));
        assertTrue(oddValues.containsKey("five"));
        assertEquals(1, oddValues.get("one"));
        assertEquals(3, oddValues.get("three"));
        assertEquals(5, oddValues.get("five"));

        // Remove entries where the key starts with 'f'
        var nonFKeys = map.entrySet().stream()
                .gather(Packrat.removeEntries((key, value) -> key.startsWith("f")))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertEquals(3, nonFKeys.size());
        assertTrue(nonFKeys.containsKey("one"));
        assertTrue(nonFKeys.containsKey("two"));
        assertTrue(nonFKeys.containsKey("three"));
        assertEquals(1, nonFKeys.get("one"));
        assertEquals(2, nonFKeys.get("two"));
        assertEquals(3, nonFKeys.get("three"));
    }

    @Test
    public void emptyMapTest() {
        var map = new HashMap<String, Integer>();

        var filtered = map.entrySet().stream()
                .gather(Packrat.filterEntries((key, value) -> true))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertTrue(filtered.isEmpty());

        var removed = map.entrySet().stream()
                .gather(Packrat.removeEntries((key, value) -> true))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertTrue(removed.isEmpty());
    }
}
