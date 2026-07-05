package io.github.jhspetersson.packrat;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Gatherer;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ArchUnit-based test to ensure every public static method in Packrat is exercised by tests.
 * A method is considered used if it is:
 * - directly called from any class whose simple name ends with "Test"; or
 * - reachable via transitive calls from such directly called Packrat methods
 *   (i.e., Packrat methods calling other Packrat methods).
 */
public class PackratArchTest {

    @Test
    void allPublicStaticPackratMethodsAreUsedInTests() {
        JavaClasses classes = new ClassFileImporter().importPackages("io.github.jhspetersson.packrat");

        JavaClass packrat = classes.get(Packrat.class);

        // Collect all public static methods declared in Packrat
        Set<JavaMethod> apiMethods = new HashSet<>();
        for (JavaMethod m : packrat.getMethods()) {
            if (m.getOwner().equals(packrat)
                    && m.getModifiers().contains(JavaModifier.PUBLIC)
                    && m.getModifiers().contains(JavaModifier.STATIC)) {
                apiMethods.add(m);
            }
        }

        // Map: Packrat method -> Packrat methods it calls (to build transitive closure within Packrat)
        Map<JavaMethod, Set<JavaMethod>> edges = new HashMap<>();
        for (JavaMethod m : packrat.getMethods()) {
            Set<JavaMethod> targets = new HashSet<>();
            for (JavaMethodCall call : m.getMethodCallsFromSelf()) {
                call.getTarget().resolveMember().ifPresent(resolved -> {
                    if (resolved.getOwner().equals(packrat)) {
                        targets.add(resolved);
                    }
                });
            }
            edges.put(m, targets);
        }

        // Seed: Packrat methods directly called by any method in classes whose name ends with "Test"
        Set<JavaMethod> directlyUsed = new HashSet<>();
        for (JavaClass jc : classes) {
            if (jc.getSimpleName().endsWith("Test")) {
                for (JavaMethodCall call : jc.getMethodCallsFromSelf()) {
                    call.getTarget().resolveMember().ifPresent(target -> {
                        if (target.getOwner().equals(packrat)) {
                            directlyUsed.add(target);
                        }
                    });
                }
            }
        }

        // Compute transitive closure: from directly used methods, follow Packrat->Packrat calls
        Set<JavaMethod> used = new HashSet<>(directlyUsed);
        ArrayDeque<JavaMethod> stack = new ArrayDeque<>(directlyUsed);
        while (!stack.isEmpty()) {
            JavaMethod current = stack.pop();
            for (JavaMethod nxt : edges.getOrDefault(current, Set.of())) {
                if (used.add(nxt)) {
                    stack.push(nxt);
                }
            }
        }

        // Determine unused public static Packrat methods
        Set<JavaMethod> unused = new HashSet<>(apiMethods);
        unused.removeAll(used);

        // Produce readable message if assertion fails
        StringBuilder message = new StringBuilder();
        if (!unused.isEmpty()) {
            message.append("The following public static Packrat methods are not used by any test (directly or transitively):\n");
            unused.stream()
                    .sorted((a, b) -> a.getFullName().compareTo(b.getFullName()))
                    .forEach(m -> message.append(" - ").append(signatureOf(m)).append('\n'));
        }

        assertTrue(unused.isEmpty(), message.toString());
    }

    /**
     * Only {@code Packrat} is intended public API. Every other class in the package
     * (gatherer implementations and internal helpers such as {@code RingBuffer})
     * must stay package-private so they do not leak into the published surface.
     */
    @Test
    void onlyPackratIsPublic() {
        JavaClasses mainClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("io.github.jhspetersson.packrat");

        ArchRule rule = noClasses()
                .that().haveSimpleNameNotEndingWith("Packrat")
                .and().areTopLevelClasses()
                .should().bePublic()
                .because("only Packrat is intended public API; helper and gatherer classes must stay package-private");

        rule.check(mainClasses);
    }

    /**
     * All {@link Gatherer} implementations are exposed only through {@code Packrat}
     * factory methods that return the {@code Gatherer} interface, so the concrete
     * implementation classes must never be public.
     */
    @Test
    void gathererImplementationsAreNotPublic() {
        JavaClasses mainClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("io.github.jhspetersson.packrat");

        ArchRule rule = noClasses()
                .that().areAssignableTo(Gatherer.class)
                .should().bePublic()
                .because("gatherer implementations are only returned via the Gatherer interface from Packrat");

        rule.check(mainClasses);
    }

    private static String signatureOf(JavaMethod m) {
        var params = m.getRawParameterTypes().stream().map(JavaClass::getName).toList();
        return m.getOwner().getName() + "." + m.getName() + "(" + String.join(", ", params) + ")";
    }
}
