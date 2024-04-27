package jhspetersson.packrat;

/**
 * Provides Gatherer instances from its fabric methods.
 *
 * @author jhspetersson
 */
public final class Packrat {
    /**
     * Provides {@link ShuffleGatherer} instance
     *
     * @return shuffle gatherer
     * @param <T> element type
     */
    public static <T> ShuffleGatherer<T> shuffle() {
        return new ShuffleGatherer<>();
    }

    private Packrat() {}
}