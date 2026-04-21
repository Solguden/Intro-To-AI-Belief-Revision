package dk.group42;

import java.util.Objects;

/**
 * A literal in a CNF clause: an atom name paired with a sign.
 * <ul>
 *   <li>{@code positive = true}  &mdash; the atom itself (e.g. {@code p}).</li>
 *   <li>{@code positive = false} &mdash; its negation    (e.g. {@code !p}).</li>
 * </ul>
 * Immutable and value-equal.
 */
public final class Literal {

    private final String atomName;
    private final boolean positive;

    public Literal(String atomName, boolean positive) {
        this.atomName = Objects.requireNonNull(atomName, "atomName");
        this.positive = positive;
    }

    public String atomName()  { return atomName; }
    public boolean positive() { return positive; }

    /** Returns the literal with the opposite sign. */
    public Literal complement() {
        return new Literal(atomName, !positive);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Literal)) return false;
        Literal l = (Literal) o;
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
