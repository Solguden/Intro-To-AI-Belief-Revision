package dk.group42;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tests the five AGM postulates the assignment asks us to validate:
 * <b>Success</b>, <b>Inclusion</b>, <b>Vacuity</b>, <b>Consistency</b>,
 * <b>Extensionality</b>.
 *
 * <p>Because we're working on a belief <i>base</i> (not a logically
 * closed belief set), postulates are checked via mutual entailment
 * rather than literal set equality: two bases are treated as
 * equivalent when they entail the same formulas out of the union of
 * their formula sets.
 */
public final class AGMChecks {

    private AGMChecks() { /* no instances */ }

    /**
     * True iff the two bases entail the same formulas from the union
     * of their formula sets. This is sound (detects differences in
     * entailed membership among the tracked formulas) but not complete
     * for general logical equivalence, and we call that out in the report.
     */
    public static boolean baseEquivalent(BeliefBase a, BeliefBase b) {
        Set<String> union = new LinkedHashSet<>(a.formulas());
        union.addAll(b.formulas());
        for (String f : union) {
            if (a.entails(f) != b.entails(f)) return false;
        }
        return true;
    }

    // ----- the five postulates -----

    /** Success: K &lowast; &alpha; entails &alpha; (unless &alpha; is a contradiction). */
    public static boolean success(BeliefBase initial, String alpha) {
        if (!Resolution.isConsistent(List.of(alpha))) return true; // vacuous
        BeliefRevisionAgent a = new BeliefRevisionAgent(initial.copy());
        return a.revise(alpha).entails(alpha);
    }

    /**
     * Inclusion: K &lowast; &alpha; &sube; K + &alpha;.
     * For belief bases we read this as: every formula retained after
     * revision is entailed by the expanded base.
     */
    public static boolean inclusion(BeliefBase initial, String alpha) {
        BeliefRevisionAgent a = new BeliefRevisionAgent(initial.copy());
        BeliefBase revised  = a.revise(alpha);
        BeliefBase expanded = initial.expand(alpha);
        for (String f : revised.formulas()) {
            if (!expanded.entails(f)) return false;
        }
        return true;
    }

    /**
     * Vacuity: if K does not entail &not;&alpha;, then K &lowast; &alpha;
     * behaves exactly like K + &alpha;.
     */
    public static boolean vacuity(BeliefBase initial, String alpha) {
        if (initial.entails("!(" + alpha + ")")) return true; // premise false, vacuously ok
        BeliefRevisionAgent a = new BeliefRevisionAgent(initial.copy());
        BeliefBase revised  = a.revise(alpha);
        BeliefBase expanded = initial.expand(alpha);
        return baseEquivalent(revised, expanded);
    }

    /** Consistency: K &lowast; &alpha; is consistent whenever &alpha; is. */
    public static boolean consistency(BeliefBase initial, String alpha) {
        if (!Resolution.isConsistent(List.of(alpha))) return true; // vacuous
        BeliefRevisionAgent a = new BeliefRevisionAgent(initial.copy());
        return a.revise(alpha).isConsistent();
    }

    /**
     * Extensionality: if &alpha; is logically equivalent to &beta;,
     * then K &lowast; &alpha; equals K &lowast; &beta;.
     */
    public static boolean extensionality(BeliefBase initial, String alpha, String beta) {
        if (!Resolution.equivalent(alpha, beta)) return true; // vacuous
        BeliefRevisionAgent a1 = new BeliefRevisionAgent(initial.copy());
        BeliefRevisionAgent a2 = new BeliefRevisionAgent(initial.copy());
        return baseEquivalent(a1.revise(alpha), a2.revise(beta));
    }

    /** Runs all five checks and returns a map from postulate name to pass/fail. */
    public static Map<String, Boolean> runAll(BeliefBase initial, String alpha, String beta) {
        Map<String, Boolean> out = new LinkedHashMap<>();
        out.put("success",        success(initial, alpha));
        out.put("inclusion",      inclusion(initial, alpha));
        out.put("vacuity",        vacuity(initial, alpha));
        out.put("consistency",    consistency(initial, alpha));
        out.put("extensionality", extensionality(initial, alpha, beta));
        return out;
    }
}
