package dk.group42;

import java.util.Objects;

/// Represents a literal in an arbitrary propositional {@link Sentence}
public record AtomicSentence(String name) implements Sentence {

  public AtomicSentence(String name) {
    this.name = Objects.requireNonNull(name, "name");
    if (name.isEmpty()) {
      throw new IllegalArgumentException("atom name must not be empty");
    }

  }

  @Override
  public String toFormulaString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AtomicSentence)) {
      return false;
    }
    return name.equals(((AtomicSentence) o).name);
  }

  @Override
  public String toString() {
    return name;
  }
}
