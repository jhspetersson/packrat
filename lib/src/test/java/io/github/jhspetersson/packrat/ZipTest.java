package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZipTest {
    @Test
    public void zipWithIterableTest() {
        var names = List.of("Anna", "Mike", "Sandra", "Rudolf", "Monica");
        var ages = List.of(20, 30, 40, 50, 60, 70, 80, 90);

        var users = names.stream().gather(Packrat.zip(ages, User::new)).toList();

        assertEquals(names.size(), users.size());
        assertEquals(new User("Anna", 20), users.getFirst());
        assertEquals(new User("Monica", 60), users.getLast());
    }

    @Test
    public void zipWithStreamTest() {
        var names = List.of("Anna", "Mike", "Sandra", "Rudolf", "Monica");
        var ages = Stream.of(20, 30, 40, 50, 60, 70, 80, 90);

        var users = names.stream().gather(Packrat.zip(ages, User::new)).toList();

        assertEquals(names.size(), users.size());
        assertEquals(new User("Anna", 20), users.getFirst());
        assertEquals(new User("Monica", 60), users.getLast());
    }

    @Test
    public void zipToMapTest() {
        var names = List.of("Anna", "Mike", "Sandra", "Rudolf", "Monica");
        var ages = List.of(20, 30, 40, 50, 60, 70, 80, 90);

        var users = names.stream().gather(Packrat.zip(ages)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        System.out.println(users);
        assertEquals(names.size(), users.size());
        assertEquals(20, users.get("Anna"));
        assertEquals(60, users.get("Monica"));
    }

    @Test
    public void zipStreamToMapTest() {
        var names = List.of("Anna", "Mike", "Sandra", "Rudolf", "Monica");
        var ages = Stream.of(20, 30, 40, 50, 60, 70, 80, 90);

        var users = names.stream().gather(Packrat.zip(ages)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertEquals(names.size(), users.size());
        assertEquals(20, users.get("Anna"));
        assertEquals(60, users.get("Monica"));
    }

    @Test
    public void zipLongerWithShorterTest() {
        var names = List.of("Anna", "Mike", "Sandra", "Rudolf", "Monica");
        var ages = List.of(20, 30, 40);

        var users = names.stream().gather(Packrat.zip(ages, User::new)).toList();

        assertEquals(ages.size(), users.size());
        assertEquals(new User("Anna", 20), users.getFirst());
        assertEquals(new User("Sandra", 40), users.getLast());
    }

    record User(String name, int age) {}

    @Test
    public void zipWithIteratorTest() {
        var names = List.of("Anna", "Mike", "Sandra", "Rudolf", "Monica");
        var ages = List.of(20, 30, 40, 50, 60, 70, 80, 90);
        Iterator<Integer> agesIterator = ages.iterator();

        var users = names.stream().gather(Packrat.zip(agesIterator)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertEquals(names.size(), users.size());
        assertEquals(20, users.get("Anna"));
        assertEquals(60, users.get("Monica"));
    }

    @Test
    public void zipWithIteratorAndMapperTest() {
        var names = List.of("Anna", "Mike", "Sandra", "Rudolf", "Monica");
        var ages = List.of(20, 30, 40, 50, 60, 70, 80, 90);
        Iterator<Integer> agesIterator = ages.iterator();

        var users = names.stream().gather(Packrat.zip(agesIterator, User::new)).toList();

        assertEquals(names.size(), users.size());
        assertEquals(new User("Anna", 20), users.getFirst());
        assertEquals(new User("Monica", 60), users.getLast());
    }
}
