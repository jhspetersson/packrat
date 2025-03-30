package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZipWithIndexTest {
    @Test
    public void defaultTest() {
        var names = List.of("Anna", "Mike", "Sandra", "Rudolf", "Monica");

        var users = names.stream().gather(Packrat.zipWithIndex()).toList();

        assertEquals(names.size(), users.size());
        assertEquals(Map.entry(0L, "Anna"), users.getFirst());
        assertEquals(Map.entry(4L, "Monica"), users.getLast());
    }

    @Test
    public void startIndexTest() {
        var names = List.of("Anna", "Mike", "Sandra", "Rudolf", "Monica");

        var users = names.stream().gather(Packrat.zipWithIndex(10)).toList();

        assertEquals(names.size(), users.size());
        assertEquals(Map.entry(10L, "Anna"), users.getFirst());
        assertEquals(Map.entry(14L, "Monica"), users.getLast());
    }

    @Test
    public void mapperTest() {
        var names = List.of("Anna", "Mike", "Sandra", "Rudolf", "Monica");

        var users = names.stream().gather(Packrat.zipWithIndex(User::new)).toList();

        assertEquals(names.size(), users.size());
        assertEquals(new User(0L, "Anna"), users.getFirst());
        assertEquals(new User(4L, "Monica"), users.getLast());

        System.out.println(users);
    }

    @Test
    public void mapperWithStartIndexTest() {
        var names = List.of("Anna", "Mike", "Sandra", "Rudolf", "Monica");

        var users = names.stream().gather(Packrat.zipWithIndex(User::new, 10)).toList();

        assertEquals(names.size(), users.size());
        assertEquals(new User(10L, "Anna"), users.getFirst());
        assertEquals(new User(14L, "Monica"), users.getLast());
    }

    record User(long index, String name) {}
}