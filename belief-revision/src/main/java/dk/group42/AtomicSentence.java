package dk.group42;

import java.util.Objects;

public final class AtomicSentence implements Sentence {

  private final String name;

  public AtomicSentence(String name) {
    this.name = Objects.requireNonNull(name, "name");
    if (name.isEmpty()) {
      throw new IllegalArgumentException("atom name must not be empty");
    }

  }

  public String name() {
    return name;
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
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return name;
  }
}
