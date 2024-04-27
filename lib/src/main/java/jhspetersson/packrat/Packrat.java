package jhspetersson.packrat;

public final class Packrat {
    public static <T> ShuffleGatherer<T> shuffle() {
        return new ShuffleGatherer<>();
    }

    private Packrat() {}
}