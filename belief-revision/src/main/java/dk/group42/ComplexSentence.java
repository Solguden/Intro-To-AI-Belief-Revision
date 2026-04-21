package dk.group42;

import java.util.Objects;

/**
 * Internal node of the formula AST: a {@link Connective} with one
 * operand (for {@code NOT}) or two operands (for {@code AND},
 * {@code OR}, {@code IMPLIES}, {@code IFF}).
 *
 * <p>Immutable and value-equal on (connective, left, right).
 */
public final class ComplexSentence implements Sentence {

    private final Connective connective;
    private final Sentence left;
    private final Sentence right; // null iff connective == NOT

    public ComplexSentence(Connective connective, Sentence left, Sentence right) {
        this.connective = Objects.requireNonNull(connective, "connective");
        this.left = Objects.requireNonNull(left, "left");
        if (connective == Connective.NOT) {
            if (right != null) {
                throw new IllegalArgumentException("NOT must not have a right operand");
            }
        } else {
            Objects.requireNonNull(right, "right operand required for " + connective);
        }
        this.right = right;
    }

    /** Factory for unary NOT. */
    public static ComplexSentence not(Sentence s) {
        return new ComplexSentence(Connective.NOT, s, null);
    }

    /** Factory for binary connectives. */
    public static ComplexSentence binary(Connective op, Sentence a, Sentence b) {
        if (op == Connective.NOT) {
            throw new IllegalArgumentException("NOT is unary; use ComplexSentence.not(...)");
        }
        return new ComplexSentence(op, a, b);
    }

    public Connective connective() { return connective; }
    public Sentence left()         { return left; }
    public Sentence right()        { return right; }

    @Override
    public String toFormulaString() {
        if (connective == Connective.NOT) {
            return "!" + wrap(left);
        }
        String op;
        switch (connective) {
            case AND:     op = " & ";   break;
            case OR:      op = " | ";   break;
            case IMPLIES: op = " -> ";  break;
            case IFF:     op = " <-> "; break;
            default:
                throw new IllegalStateException("Unexpected connective: " + connective);
        }
        return wrap(left) + op + wrap(right);
    }

    private static String wrap(Sentence s) {
        if (s instanceof AtomicSentence) return s.toFormulaString();
        if (s instanceof ComplexSentence && ((ComplexSentence) s).connective == Connective.NOT) {
            return s.toFormulaString();
        }
        return "(" + s.toFormulaString() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComplexSentence)) return false;
        ComplexSentence c = (ComplexSentence) o;
        return connective == c.connective
                && left.equals(c.left)
                && Objects.equals(right, c.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connective, left, right);
    }

    @Override
    public String toString() {
        return toFormulaString();
    }
}
