package dk.group42;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Clause {

  private final Set<Literal> literals;

  public Clause(Set<Literal> literals) {
    this.literals = Set.copyOf(literals);
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

  public boolean isTautology() {
    Set<String> positives = literals.stream()
        .filter(Literal::positive)
        .map(Literal::atomName)
        .collect(HashSet::new, HashSet::add, HashSet::addAll);

    Set<String> negatives = literals.stream()
        .filter(l -> !l.positive())
        .map(Literal::atomName)
        .collect(HashSet::new, HashSet::add, HashSet::addAll);

    return literals.stream()
        .anyMatch(l -> positives.contains(l.atomName()) && negatives.contains(l.atomName()));

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Clause)) {
      return false;
    }
    return literals.equals(((Clause) o).literals);
  }

  @Override
  public int hashCode() {
    return literals.hashCode();
  }

  @Override
  public String toString() {
    if (literals.isEmpty()) {
      return "⊥";
    }
    return "{" + String.join(", ", literals.stream().map(Literal::toString).toList()) + "}";
  }
}
