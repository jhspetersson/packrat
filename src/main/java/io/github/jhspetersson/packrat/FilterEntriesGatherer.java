package io.github.jhspetersson.packrat;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Gatherer;

import org.jspecify.annotations.NonNull;

/**
 * Filters Map.Entry elements using a BiPredicate that tests the key and value of each entry.
 *
 * @param <K> key type
 * @param <V> value type
 * @author jhspetersson
 */
class FilterEntriesGatherer<K, V> implements Gatherer<Map.Entry<K, V>, Void, Map.Entry<K, V>> {
    private final BiPredicate<? super K, ? super V> predicate;
    private final boolean invert;

    FilterEntriesGatherer(@NonNull BiPredicate<? super K, ? super V> predicate) {
        this(predicate, false);
    }

    FilterEntriesGatherer(@NonNull BiPredicate<? super K, ? super V> predicate, boolean invert) {
        Objects.requireNonNull(predicate, "predicate cannot be null");

        this.predicate = predicate;
        this.invert = invert;
    }

    @Override
    public Integrator<Void, Map.Entry<K, V>, Map.Entry<K, V>> integrator() {
        return Integrator.of((_, element, downstream) -> {
            var testResult = predicate.test(element.getKey(), element.getValue());
            if (testResult ^ invert) {
                return downstream.push(element);
            }
            return !downstream.isRejecting();
        });
    }
}