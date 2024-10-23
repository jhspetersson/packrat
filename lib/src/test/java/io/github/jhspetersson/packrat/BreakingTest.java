package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("preview")
public class BreakingTest {
    private static final String TEST_STRING = "Test \u270B\uD83C\uDFFF\uD83D\uDC22 \u65E5\u672C\u8A9E\u3057\u3083\u3079\u308B\u304B \uD83D\uDC69\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC67\uD83D\uDC69\u200D\uD83D\uDC69\u200D\uD83D\uDC67\uD83D\uDC68\uD83C\uDFFC\u200D\u2764\uFE0F\u200D\uD83D\uDC68\uD83C\uDFFE";

    @Test
    public void charsTest() {
        var chars = Stream.of(TEST_STRING).gather(Packrat.chars()).toList();
        assertEquals(20, chars.size());
    }

    @Test
    public void wordsTest() {
        var words = Stream.of("Another test").gather(Packrat.words()).toList();

        assertEquals(2, words.size());
    }

    @Test
    public void sentencesTest() {
        var sentences = Stream.of("What a sentence! Another test...\nI can't handle this, what is your proposition?").gather(Packrat.sentences()).toList();

        assertEquals(3, sentences.size());
    }
}
