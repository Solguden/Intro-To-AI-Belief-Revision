package dk.group42;

import java.util.Objects;

/// Represents a literal such as `p` or `q` in a {@link Clause}
public record Literal(String atomName, boolean positive) {

  public Literal(String atomName, boolean positive) {
    this.atomName = Objects.requireNonNull(atomName, "atomName");
    this.positive = positive;
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
  public String toString() {
    return positive ? atomName : "!" + atomName;
  }
}
