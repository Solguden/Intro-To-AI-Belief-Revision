package dk.group42;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A prioritised belief base: a finite list of (formula, priority) pairs.
 *
 * <p>A belief base, as opposed to a belief <i>set</i>, is NOT assumed to be
 * logically closed. This matters because it lets us distinguish
 * <i>contracted</i> from <i>merely-absent</i>: contracting by &alpha;
 * means the base no longer <i>entails</i> &alpha;, not just that &alpha;
 * is not in the formula list.
 */
public final class BeliefBase {

    private final List<Belief> beliefs;

    public BeliefBase() {
        this.beliefs = new ArrayList<>();
    }

    public BeliefBase(Collection<Belief> beliefs) {
        this.beliefs = new ArrayList<>(beliefs);
    }

    /** Unmodifiable view of the beliefs currently in the base. */
    public List<Belief> beliefs() {
        return Collections.unmodifiableList(beliefs);
    }

    public List<String> formulas() {
        List<String> out = new ArrayList<>(beliefs.size());
        for (Belief b : beliefs) {
            out.add(b.formula());
        }
        return out;
    }

    public BeliefBase copy() {
        return new BeliefBase(beliefs);
    }

    public void add(String formula, int priority) {
        beliefs.add(new Belief(formula, priority));
    }

    public boolean entails(String query) {
        return Resolution.entails(formulas(), query);
    }

    public boolean isConsistent() {
        return Resolution.isConsistent(formulas());
    }

    // ---------- expansion ----------

    /**
     * Expansion K + &alpha;: add the new formula at a priority one higher
     * than anything currently in the base, so the newcomer is the most
     * entrenched belief.
     */
    public BeliefBase expand(String formula) {
        return expand(formula, null);
    }

    public BeliefBase expand(String formula, Integer priority) {
        BeliefBase next = this.copy();
        int p = (priority != null) ? priority : (maxPriority(next.beliefs) + 1);
        next.add(formula, p);
        return next;
    }

    private static int maxPriority(List<Belief> bs) {
        int max = 0;
        for (Belief b : bs) {
            if (b.priority() > max) max = b.priority();
        }
        return max;
    }

    // ---------- contraction ----------

    /**
     * Priority-based contraction K &minus; &alpha;:
     * while the base still entails &alpha;, drop one belief of minimum priority.
     * Stops either when the base no longer entails &alpha; (success), or when
     * the working list is empty (no beliefs left to remove).
     *
     * <p>Edge case: if &alpha; is a tautology the loop will remove every
     * belief and leave the base empty. AGM's Success postulate explicitly
     * exempts tautologies, so this corner does not affect the postulate
     * checks we run in {@link AGMChecks}, but it matches the Python
     * reference implementation for auditability.
     *
     * <p>This is a simple priority-sweep, not partial-meet contraction,
     * and the report should call it out as such.
     */
    public BeliefBase contract(String formula) {
        if (!this.entails(formula)) {
            return this.copy();
        }

        List<Belief> working = new ArrayList<>(this.beliefs);
        while (true) {
            BeliefBase probe = new BeliefBase(working);
            if (!probe.entails(formula)) break;
            if (working.isEmpty()) break;

            int min = Integer.MAX_VALUE;
            for (Belief b : working) {
                if (b.priority() < min) min = b.priority();
            }
            for (int i = 0; i < working.size(); i++) {
                if (working.get(i).priority() == min) {
                    working.remove(i);
                    break;
                }
            }
        }
        return new BeliefBase(working);
    }
}
