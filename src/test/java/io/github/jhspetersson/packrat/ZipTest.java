package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void zipShouldShortCircuitWhenSourceExhausted() {
        var pulled = new AtomicInteger();

        var result = Stream.iterate(1, i -> i + 1)
                .limit(1000)
                .peek(_ -> pulled.incrementAndGet())
                .gather(Packrat.zip(List.of("a", "b", "c"), (i, s) -> s + i))
                .toList();

        assertEquals(List.of("a1", "b2", "c3"), result);
        assertTrue(pulled.get() <= 4, "upstream pulled " + pulled.get() + " elements");
    }

    @Test
    public void zipShouldTerminateOnInfiniteStream() {
        var result = assertTimeoutPreemptively(Duration.ofSeconds(5), () ->
                Stream.iterate(0, i -> i + 1).gather(Packrat.zip(List.of("a", "b", "c"))).toList());

        assertEquals(3, result.size());
    }

    @Test
    public void zipShouldCloseSuppliedStream() {
        var closed = new AtomicBoolean();
        var ages = Stream.of(20, 30, 40).onClose(() -> closed.set(true));

        List.of("Anna", "Mike", "Sandra").stream().gather(Packrat.zip(ages, User::new)).toList();

        assertTrue(closed.get());
    }

    @Test
    public void zipWithStreamShouldThrowOnReuse() {
        var gatherer = Packrat.zip(Stream.of(10, 20, 30), (Integer a, Integer b) -> a + b);

        var result = Stream.of(1, 2, 3).gather(gatherer).toList();
        assertEquals(List.of(11, 22, 33), result);

        assertThrows(IllegalStateException.class, () -> Stream.of(4, 5, 6).gather(gatherer).toList());
    }

    @Test
    public void zipWithIterableShouldBeReusable() {
        var source = List.of(10, 20, 30);
        var gatherer = Packrat.zip(source, (Integer a, Integer b) -> a + b);

        var result1 = Stream.of(1, 2, 3).gather(gatherer).toList();
        assertEquals(List.of(11, 22, 33), result1);

        var result2 = Stream.of(4, 5, 6).gather(gatherer).toList();
        assertEquals(List.of(14, 25, 36), result2);
    }
}
