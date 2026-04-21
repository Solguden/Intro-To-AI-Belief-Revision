package dk.group42;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class BeliefBase {

    private final List<Belief> beliefs;

    public BeliefBase() {
        this.beliefs = new ArrayList<>();
    }

    public BeliefBase(Collection<Belief> beliefs) {
        this.beliefs = new ArrayList<>(beliefs);
    }


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

    @Override
    public String toString() {
        var beliefsString = beliefs.stream()
            .map(b -> b.toString() + "\n")
            .reduce("", String::concat);

        return "BeliefBase{" + "beliefs=" + beliefs + '}';
    }
}
