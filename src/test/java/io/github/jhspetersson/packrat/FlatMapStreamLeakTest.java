package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlatMapStreamLeakTest {

    @Test
    void flatMapIfShouldCloseStreams() {
        var closeCount = new AtomicInteger(0);

        Stream.of(1, 2, 3)
                .gather(Packrat.flatMapIf(
                        i -> Stream.of(i, i * 10).onClose(closeCount::incrementAndGet),
                        i -> true
                ))
                .toList();

        assertEquals(3, closeCount.get());
    }

    @Test
    void flatMapIfShouldCloseStreamOnEarlyTermination() {
        var closeCount = new AtomicInteger(0);

        Stream.of(1, 2, 3)
                .gather(Packrat.flatMapIf(
                        i -> Stream.of(i, i * 10).onClose(closeCount::incrementAndGet),
                        i -> true
                ))
                .limit(1)
                .toList();

        assertEquals(1, closeCount.get());
    }
}
