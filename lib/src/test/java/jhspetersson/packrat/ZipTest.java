package jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("preview")
public class ZipTest {
    @Test
    public void zipWithIterableTest() {
        var names = List.of("Anna", "Mike", "Sandra", "Rudolf", "Monica");
        var ages = List.of(20, 30, 40, 50, 60, 70, 80, 90);

        var users = names.stream().gather(Packrat.zip(ages, User::new)).toList();

        assertEquals(names.size(), users.size());
        assertEquals(users.getFirst(), new User("Anna", 20));
        assertEquals(users.getLast(), new User("Monica", 60));
    }

    @Test
    public void zipWithStreamTest() {
        var names = List.of("Anna", "Mike", "Sandra", "Rudolf", "Monica");
        var ages = Stream.of(20, 30, 40, 50, 60, 70, 80, 90);

        var users = names.stream().gather(Packrat.zip(ages, User::new)).toList();

        assertEquals(names.size(), users.size());
        assertEquals(users.getFirst(), new User("Anna", 20));
        assertEquals(users.getLast(), new User("Monica", 60));
    }

    record User(String name, int age) {}
}
