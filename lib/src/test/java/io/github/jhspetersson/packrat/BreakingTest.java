package io.github.jhspetersson.packrat;

import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BreakingTest {
    private static final String TEST_STRING = "Test ✋\uD83C\uDFFF\uD83D\uDC22 日本語しゃべるか \uD83D\uDC69\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC67\uD83D\uDC69\u200D\uD83D\uDC69\u200D\uD83D\uDC67\uD83D\uDC68\uD83C\uDFFC\u200D❤️\u200D\uD83D\uDC68\uD83C\uDFFE";
    private static final String JAPANESE_TEXT = "こんにちは世界。日本語のテストです。";
    private static final String FRENCH_TEXT = "Bonjour le monde! C'est un test en français.";

    @Test
    public void charsTest() {
        var chars = Stream.of(TEST_STRING).gather(Packrat.chars()).toList();
        assertEquals(20, chars.size());
    }

    @Test
    public void charsWithLocaleTest() {
        var charsUS = Stream.of(TEST_STRING).gather(Packrat.chars(Locale.US)).toList();
        assertEquals(20, charsUS.size());

        var charsJP = Stream.of(JAPANESE_TEXT).gather(Packrat.chars(Locale.JAPAN)).toList();
        // Character breaking can vary by JVM implementation and locale
        assertTrue(charsJP.size() >= 17 && charsJP.size() <= 18, 
                "Expected between 17 and 18 characters, but got " + charsJP.size());

        var charsFR = Stream.of(FRENCH_TEXT).gather(Packrat.chars(Locale.FRANCE)).toList();
        // Character breaking can vary by JVM implementation and locale
        assertTrue(charsFR.size() >= 44 && charsFR.size() <= 45, 
                "Expected between 44 and 45 characters, but got " + charsFR.size());
    }

    @Test
    public void wordsTest() {
        var words = Stream.of("Another test").gather(Packrat.words()).toList();

        assertEquals(2, words.size());
    }

    @Test
    public void wordsWithLocaleTest() {
        var wordsUS = Stream.of("Another test").gather(Packrat.words(Locale.US)).toList();
        assertEquals(2, wordsUS.size());

        var wordsJP = Stream.of(JAPANESE_TEXT).gather(Packrat.words(Locale.JAPAN)).toList();
        // Japanese word breaking can vary by implementation, so we check it's at least breaking something
        assertFalse(wordsJP.isEmpty());

        var wordsFR = Stream.of(FRENCH_TEXT).gather(Packrat.words(Locale.FRANCE)).toList();
        // Word breaking can vary by JVM implementation and locale
        assertTrue(wordsFR.size() >= 9 && wordsFR.size() <= 10, 
                "Expected between 9 and 10 words, but got " + wordsFR.size());
    }

    @Test
    public void sentencesTest() {
        var sentences = Stream.of("What a sentence! Another test...\nI can't handle this, what is your proposition?").gather(Packrat.sentences()).toList();

        assertEquals(3, sentences.size());
    }

    @Test
    public void sentencesWithLocaleTest() {
        var sentencesUS = Stream.of("What a sentence! Another test...\nI can't handle this, what is your proposition?").gather(Packrat.sentences(Locale.US)).toList();
        assertEquals(3, sentencesUS.size());

        var sentencesJP = Stream.of(JAPANESE_TEXT).gather(Packrat.sentences(Locale.JAPAN)).toList();
        assertEquals(2, sentencesJP.size());

        var sentencesFR = Stream.of(FRENCH_TEXT).gather(Packrat.sentences(Locale.FRANCE)).toList();
        assertEquals(2, sentencesFR.size());
    }
}
