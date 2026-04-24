package dk.group42;

import java.util.Objects;


public final class Literal {

  private final String atomName;
  private final boolean positive;

  public Literal(String atomName, boolean positive) {
    this.atomName = Objects.requireNonNull(atomName, "atomName");
    this.positive = positive;
  }

  public String atomName() {
    return atomName;
  }

  public boolean positive() {
    return positive;
  }


  public Literal complement() {
    return new Literal(atomName, !positive);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Literal l)) {
      return false;
    }
    return positive == l.positive && atomName.equals(l.atomName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(atomName, positive);
  }

  @Override
  public String toString() {
    return positive ? atomName : "!" + atomName;
  }
}
