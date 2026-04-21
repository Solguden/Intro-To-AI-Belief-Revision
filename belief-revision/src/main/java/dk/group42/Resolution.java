package dk.group42;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Resolution-based logical services (self-implemented; no external SAT solver).
 *
 * <p>The key fact is the classic refutation theorem:
 * <pre>
 *     KB &#8872; &alpha;   iff   KB &cup; {&not;&alpha;} is unsatisfiable.
 * </pre>
 *
 * <p>We detect unsatisfiability by repeatedly applying the binary
 * resolution rule until we either derive the empty clause (&perp;)
 * or no new clause can be produced.
 */
public final class Resolution {

    private Resolution() { /* no instances */ }

    /**
     * Apply the binary resolution rule to a pair of clauses.
     * Returns every non-tautological resolvent that can be derived
     * by cancelling a single complementary pair.
     */
    public static Set<Clause> resolve(Clause c1, Clause c2) {
        Set<Clause> out = new HashSet<>();
        for (Literal l : c1.literals()) {
            Literal comp = l.complement();
            if (c2.literals().contains(comp)) {
                Set<Literal> merged = new HashSet<>(c1.literals());
                merged.remove(l);
                for (Literal l2 : c2.literals()) {
                    if (!l2.equals(comp)) {
                        merged.add(l2);
                    }
                }
                Clause candidate = new Clause(merged);
                if (!candidate.isTautology()) {
                    out.add(candidate);
                }
            }
        }
        return out;
    }

    /** True iff the supplied set of clauses is unsatisfiable. */
    public static boolean isUnsatisfiable(Collection<Clause> clauses) {
        Set<Clause> current = new HashSet<>(clauses);

        // A literal empty clause in the input is already falsum.
        for (Clause c : current) {
            if (c.isEmpty()) return true;
        }

        while (true) {
            List<Clause> snapshot = new ArrayList<>(current);
            Set<Clause> produced = new HashSet<>();
            int n = snapshot.size();

            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    Set<Clause> resolvents = resolve(snapshot.get(i), snapshot.get(j));
                    for (Clause r : resolvents) {
                        if (r.isEmpty()) return true;
                    }
                    produced.addAll(resolvents);
                }
            }

            if (current.containsAll(produced)) return false; // saturation reached
            current.addAll(produced);
        }
    }

    /** True iff the knowledge base logically entails the query. */
    public static boolean entails(Iterable<String> kb, String query) {
        List<Clause> clauses = new ArrayList<>();
        for (String f : kb) {
            clauses.addAll(CNFConverter.toClauses(f));
        }
        clauses.addAll(CNFConverter.toClauses("!(" + query + ")"));
        return isUnsatisfiable(clauses);
    }

    /** True iff the given set of formulas is jointly consistent. */
    public static boolean isConsistent(Iterable<String> formulas) {
        List<Clause> clauses = new ArrayList<>();
        for (String f : formulas) {
            clauses.addAll(CNFConverter.toClauses(f));
        }
        return !isUnsatisfiable(clauses);
    }

    /** True iff the two formulas are logically equivalent. */
    public static boolean equivalent(String a, String b) {
        return entails(List.of(), "(" + a + ") -> (" + b + ")")
            && entails(List.of(), "(" + b + ") -> (" + a + ")");
    }
}
