package dk.group42;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Converts a parsed {@link Sentence} to Conjunctive Normal Form
 * (a conjunction of clauses, each clause being a disjunction of literals).
 *
 * <h3>Pipeline</h3>
 * <ol>
 *   <li>Eliminate {@code <->} and {@code ->} in favour of {@code &}, {@code |}, {@code !}.</li>
 *   <li>Push negations inward until they sit only directly on atoms (NNF).</li>
 *   <li>Distribute OR over AND repeatedly until the formula is in CNF.</li>
 *   <li>Flatten the AND-chain into a {@code List<Clause>}.</li>
 * </ol>
 *
 * <p><b>Note.</b> Distributive CNF conversion can blow up exponentially on
 * highly nested IFFs; that's acceptable at the scale of this assignment but
 * worth mentioning in the report.
 */
public final class CNFConverter {

    private CNFConverter() { /* no instances */ }

    /** Convenience: parse a formula string and convert it to clauses. */
    public static List<Clause> toClauses(String formula) {
        return toClauses(FormulaParser.parse(formula));
    }

    public static List<Clause> toClauses(Sentence s) {
        Sentence noImpl = eliminateImplications(s);
        Sentence nnf    = toNNF(noImpl);
        Sentence cnf    = toCnfAst(nnf);
        return collectClauses(cnf);
    }

    // ----- step 1: implications / biconditionals -----

    private static Sentence eliminateImplications(Sentence s) {
        if (s instanceof AtomicSentence) return s;
        ComplexSentence cs = (ComplexSentence) s;
        switch (cs.connective()) {
            case NOT:
                return ComplexSentence.not(eliminateImplications(cs.left()));
            case IMPLIES: {
                Sentence a = eliminateImplications(cs.left());
                Sentence b = eliminateImplications(cs.right());
                return ComplexSentence.binary(Connective.OR, ComplexSentence.not(a), b);
            }
            case IFF: {
                Sentence a = eliminateImplications(cs.left());
                Sentence b = eliminateImplications(cs.right());
                Sentence l = ComplexSentence.binary(Connective.OR, ComplexSentence.not(a), b);
                Sentence r = ComplexSentence.binary(Connective.OR, ComplexSentence.not(b), a);
                return ComplexSentence.binary(Connective.AND, l, r);
            }
            case AND:
            case OR:
                return ComplexSentence.binary(
                        cs.connective(),
                        eliminateImplications(cs.left()),
                        eliminateImplications(cs.right()));
            default:
                throw new IllegalStateException("Unknown connective: " + cs.connective());
        }
    }

    // ----- step 2: Negation Normal Form -----

    /** Push {@code !} down until it sits only on atoms. */
    private static Sentence toNNF(Sentence s) {
        if (s instanceof AtomicSentence) return s;
        ComplexSentence cs = (ComplexSentence) s;

        if (cs.connective() == Connective.NOT) {
            Sentence child = cs.left();
            if (child instanceof AtomicSentence) return cs;

            ComplexSentence cchild = (ComplexSentence) child;
            switch (cchild.connective()) {
                case NOT:
                    return toNNF(cchild.left());                 // !!x -> x
                case AND:                                         // !(a & b) -> !a | !b
                    return ComplexSentence.binary(
                            Connective.OR,
                            toNNF(ComplexSentence.not(cchild.left())),
                            toNNF(ComplexSentence.not(cchild.right())));
                case OR:                                          // !(a | b) -> !a & !b
                    return ComplexSentence.binary(
                            Connective.AND,
                            toNNF(ComplexSentence.not(cchild.left())),
                            toNNF(ComplexSentence.not(cchild.right())));
                default:
                    throw new IllegalStateException(
                            "Implications/IFF should already be eliminated, got: "
                                    + cchild.connective());
            }
        }
        return ComplexSentence.binary(
                cs.connective(),
                toNNF(cs.left()),
                toNNF(cs.right()));
    }

    // ----- step 3: distribute OR over AND -----

    /**
     * (&alpha; &and; &beta;) &or; &gamma;  =  (&alpha; &or; &gamma;) &and; (&beta; &or; &gamma;),
     * applied recursively so the whole formula becomes a conjunction of disjunctions.
     */
    private static Sentence distributeOr(Sentence a, Sentence b) {
        if (a instanceof ComplexSentence && ((ComplexSentence) a).connective() == Connective.AND) {
            ComplexSentence ca = (ComplexSentence) a;
            return ComplexSentence.binary(Connective.AND,
                    distributeOr(ca.left(),  b),
                    distributeOr(ca.right(), b));
        }
        if (b instanceof ComplexSentence && ((ComplexSentence) b).connective() == Connective.AND) {
            ComplexSentence cb = (ComplexSentence) b;
            return ComplexSentence.binary(Connective.AND,
                    distributeOr(a, cb.left()),
                    distributeOr(a, cb.right()));
        }
        return ComplexSentence.binary(Connective.OR, a, b);
    }

    private static Sentence toCnfAst(Sentence s) {
        if (s instanceof AtomicSentence) return s;
        ComplexSentence cs = (ComplexSentence) s;

        // At this point (we are in NNF) NOT may only sit directly on an atom.
        if (cs.connective() == Connective.NOT) {
            if (cs.left() instanceof AtomicSentence) return cs;
            throw new IllegalStateException("Expected NNF literal but got NOT over " + cs.left());
        }
        if (cs.connective() == Connective.AND) {
            return ComplexSentence.binary(Connective.AND,
                    toCnfAst(cs.left()),
                    toCnfAst(cs.right()));
        }
        if (cs.connective() == Connective.OR) {
            return distributeOr(toCnfAst(cs.left()), toCnfAst(cs.right()));
        }
        throw new IllegalStateException("Unexpected connective in CNF conversion: " + cs.connective());
    }

    // ----- step 4: flatten into clauses -----

    private static List<Clause> collectClauses(Sentence s) {
        List<Clause> clauses = new ArrayList<>();
        collectAnd(s, clauses);
        return clauses;
    }

    private static void collectAnd(Sentence s, List<Clause> out) {
        if (s instanceof ComplexSentence
                && ((ComplexSentence) s).connective() == Connective.AND) {
            ComplexSentence cs = (ComplexSentence) s;
            collectAnd(cs.left(),  out);
            collectAnd(cs.right(), out);
        } else {
            Set<Literal> literals = new HashSet<>();
            collectOrInto(s, literals);
            out.add(new Clause(literals));
        }
    }

    private static void collectOrInto(Sentence s, Set<Literal> out) {
        if (s instanceof ComplexSentence) {
            ComplexSentence cs = (ComplexSentence) s;
            if (cs.connective() == Connective.OR) {
                collectOrInto(cs.left(),  out);
                collectOrInto(cs.right(), out);
                return;
            }
            if (cs.connective() == Connective.NOT && cs.left() instanceof AtomicSentence) {
                out.add(new Literal(((AtomicSentence) cs.left()).name(), false));
                return;
            }
        }
        if (s instanceof AtomicSentence) {
            out.add(new Literal(((AtomicSentence) s).name(), true));
            return;
        }
        throw new IllegalStateException("Expected literal or disjunction, got " + s);
    }
}
