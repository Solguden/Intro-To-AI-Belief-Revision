package dk.group42;

/**
 * Root of the propositional-formula AST.
 * Implemented by {@link AtomicSentence} and {@link ComplexSentence}.
 *
 * <p>All {@code Sentence} instances are immutable and implement
 * {@code equals} and {@code hashCode} based on structural content so
 * that formulas can be used safely as {@code Map} / {@code Set} keys.
 */
public interface Sentence {

    /** Returns a human-readable infix representation. Mainly for debugging. */
    String toFormulaString();
}
