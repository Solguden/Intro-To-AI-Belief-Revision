package dk.group42;

import java.util.*;

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
    return beliefs.stream().map(Belief::formula).toList();
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
    BeliefBase newBeliefBase = this.copy();
    int p = (priority != null) ? priority : (maxPriority(newBeliefBase.beliefs) + 1);
    newBeliefBase.add(formula, p);
    return newBeliefBase;
  }

  private static int maxPriority(List<Belief> bs) {
    return bs.stream().mapToInt(Belief::priority).max().orElse(0);
  }


  public BeliefBase contract(String formula) {
    if (!this.entails(formula)) {
      return this.copy();
    }

    List<Belief> working = new ArrayList<>(this.beliefs);
    while (true) {
      BeliefBase probe = new BeliefBase(working);
        if (!probe.entails(formula)) {
            break;
        }
        if (working.isEmpty()) {
            break;
        }

      int min = working.stream()
          .mapToInt(Belief::priority)
          .min()
          .orElse(Integer.MAX_VALUE);

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
    return "BeliefBase{" + "beliefs=" + beliefs + '}';
  }
}
