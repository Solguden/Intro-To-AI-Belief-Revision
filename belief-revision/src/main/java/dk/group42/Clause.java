package dk.group42;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A clause: a disjunction of literals.
 * The empty clause represents falsum (&perp;) and therefore unsatisfiability.
 *
 * <p>Immutable after construction and value-equal on the literal set,
 * so clauses can be stored in hash-based collections safely.
 */
public final class Clause {

    private final Set<Literal> literals;

    /** Defensive copy: the caller can mutate their set without affecting this clause. */
    public Clause(Set<Literal> literals) {
        this.literals = Collections.unmodifiableSet(new HashSet<>(literals));
    }

    public static Clause empty() {
        return new Clause(Collections.emptySet());
    }

    public Set<Literal> literals() {
        return literals;
    }

    public boolean isEmpty() {
        return literals.isEmpty();
    }

    /**
     * True iff the clause contains both some atom and its negation,
     * which makes the clause a tautology and safe to discard from a
     * resolution proof.
     */
    public boolean isTautology() {
        Set<String> positives = new HashSet<>();
        Set<String> negatives = new HashSet<>();
        for (Literal l : literals) {
            if (l.positive()) {
                positives.add(l.atomName());
            } else {
                negatives.add(l.atomName());
            }
            if (positives.contains(l.atomName()) && negatives.contains(l.atomName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Clause)) return false;
        return literals.equals(((Clause) o).literals);
    }

    @Override
    public int hashCode() {
        return literals.hashCode();
    }

    @Override
    public String toString() {
        if (literals.isEmpty()) return "\u22A5"; // ⊥
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Literal l : literals) {
            if (!first) sb.append(", ");
            sb.append(l);
            first = false;
        }
        return sb.append("}").toString();
    }
}
