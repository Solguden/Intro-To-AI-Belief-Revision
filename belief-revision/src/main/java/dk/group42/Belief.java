package dk.group42;

import java.util.Objects;

/// Represents a belief held by the {@link BeliefRevisionAgent} based on priority
public record Belief(String formula, int priority) {

  public Belief(String formula, int priority) {
    this.formula = Objects.requireNonNull(formula, "formula");
    this.priority = priority;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Belief b)) {
      return false;
    }
    return priority == b.priority && formula.equals(b.formula);
  }

  @Override
  public String toString() {
    return formula + " [p=" + priority + "]";
  }
}
