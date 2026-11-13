package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

    @Test
    public void concreteEntryTypeTest() {
        List<Map.Entry<Long, String>> fromZero = Stream.of("a", "b").gather(Packrat.zipWithIndex()).toList();
        List<Map.Entry<Long, String>> fromTen = Stream.of("a", "b").gather(Packrat.zipWithIndex(10)).toList();

        assertEquals(List.of(Map.entry(0L, "a"), Map.entry(1L, "b")), fromZero);
        assertEquals(List.of(Map.entry(10L, "a"), Map.entry(11L, "b")), fromTen);
    }

    record User(long index, String name) {}

    @Test
    public void relaxedIndexedCallbackBoundsTest() {
        java.util.function.BiFunction<Number, Object, String> mapper = (index, element) -> index + ":" + element;
        java.util.function.BiPredicate<Number, Object> predicate = (index, _) -> index.longValue() > 0;
        java.util.function.BiFunction<Number, java.util.Collection<String>, Integer> windowMapper =
                (index, window) -> index.intValue() + window.size();
        var peeked = new java.util.ArrayList<String>();
        java.util.function.BiConsumer<Number, Object> consumer = (index, element) -> peeked.add(index + ":" + element);

        assertEquals(List.of("0:a", "1:b"), Stream.of("a", "b").gather(Packrat.zipWithIndex(mapper)).toList());
        assertEquals(List.of("5:a", "6:b"), Stream.of("a", "b").gather(Packrat.mapWithIndex(mapper, 5)).toList());
        assertEquals(List.of("b"), Stream.of("a", "b").gather(Packrat.filterWithIndex(predicate)).toList());
        assertEquals(List.of("a"), Stream.of("a", "b").gather(Packrat.removeWithIndex(predicate)).toList());
        assertEquals(List.of("a", "b"), Stream.of("a", "b").gather(Packrat.peekWithIndex(consumer)).toList());
        assertEquals(List.of("0:a", "1:b"), peeked);
        assertEquals(List.of(2, 3), Stream.of("a", "b", "c").gather(Packrat.windowSlidingWithIndex(2, windowMapper)).toList());
        assertEquals(List.of(2, 2), Stream.of("a", "b", "c").gather(Packrat.windowFixedWithIndex(2, windowMapper)).toList());
    }
}
