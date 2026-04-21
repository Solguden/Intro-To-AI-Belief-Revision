package dk.group42;

import java.util.Objects;

/**
 * A belief: a propositional formula together with a numeric priority.
 * Higher priority = more entrenched; lower-priority beliefs are
 * surrendered first during contraction.
 *
 * <p>Immutable and value-equal.
 */
public final class Belief {

    private final String formula;
    private final int priority;

    public Belief(String formula, int priority) {
        this.formula = Objects.requireNonNull(formula, "formula");
        this.priority = priority;
    }

    public String formula() { return formula; }
    public int priority()   { return priority; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Belief)) return false;
        Belief b = (Belief) o;
        return priority == b.priority && formula.equals(b.formula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formula, priority);
    }

    @Override
    public String toString() {
        return formula + " [p=" + priority + "]";
    }
}
